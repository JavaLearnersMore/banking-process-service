package com.example.account.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.dto.TransactionRequest;
import com.example.account.dto.TransactionResponse;
import com.example.account.entity.Account;
import com.example.account.entity.LedgerEntry;
import com.example.account.entity.TransactionGroup;
import com.example.account.exception.AccountNotFoundException;
import com.example.account.exception.InsufficientFundsException;
import com.example.account.exception.InvalidTransactionStatusException;
import com.example.account.exception.TransactionAlreadyReversedException;
import com.example.account.exception.TransactionNotFoundException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.LedgerEntryRepository;
import com.example.account.repository.TransactionGroupRepository;
import com.example.account.entity.TransactionStatus;
import com.example.account.entity.EntryType;


@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TransactionService(
            AccountRepository accountRepository,
            TransactionGroupRepository transactionGroupRepository,
            LedgerEntryRepository ledgerEntryRepository) {

        this.accountRepository = accountRepository;
        this.transactionGroupRepository = transactionGroupRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public TransactionResponse postTransaction(
            TransactionRequest request) {

        validateRequest(request);

        /*
         * 1. Idempotency check
         */
        Optional<TransactionGroup> existingTransaction =
                transactionGroupRepository.findByExternalRef(request.getExternalRef());

        if (existingTransaction.isPresent()) {

            TransactionGroup transaction =
                    existingTransaction.get();

            return new TransactionResponse(
                    transaction.getId(),
                    transaction.getExternalRef(),
                    transaction.getStatus(),
                    "Transaction already processed"
            );
        }

        /*
         * 2. Find and lock debit account
         */
        Account debitAccount =
                accountRepository
                        .findWithLockByAccountNumber(
                                request.getDebitAccount())
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Debit account not found: "
                                        + request.getDebitAccount()
                                )
                        );

        /*
         * 3. Find and lock credit account
         */
        Account creditAccount =
                accountRepository
                        .findWithLockByAccountNumber(
                                request.getCreditAccount())
                        .orElseThrow(() ->
                                new AccountNotFoundException(
                                        "Credit account not found: "
                                        + request.getCreditAccount()
                                )
                        );

        /*
         * 4. Check account status
         */
        if (!"ACTIVE".equalsIgnoreCase(
                debitAccount.getStatus())) {

            throw new IllegalStateException(
                    "Debit account is not active: "
                    + request.getDebitAccount()
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(
                creditAccount.getStatus())) {

            throw new IllegalStateException(
                    "Credit account is not active: "
                    + request.getCreditAccount()
            );
        }

        /*
         * 5. Check sufficient balance
         */
        BigDecimal availableBalance =
                debitAccount.getAvailableBalance();

        if (availableBalance == null) {
            availableBalance = BigDecimal.ZERO;
        }

        if (availableBalance.compareTo(
                request.getAmount()) < 0) {

            throw new InsufficientFundsException(
                    "Insufficient funds in account "
                    + request.getDebitAccount()
                    + ": available="
                    + availableBalance
                    + ", required="
                    + request.getAmount()
            );
        }

        /*
         * 6. Calculate balances
         */
        BigDecimal debitAvailableAfter =
                debitAccount.getAvailableBalance()
                        .subtract(request.getAmount());

        BigDecimal debitLedgerAfter =
                debitAccount.getLedgerBalance()
                        .subtract(request.getAmount());

        BigDecimal creditAvailableAfter =
                creditAccount.getAvailableBalance()
                        .add(request.getAmount());

        BigDecimal creditLedgerAfter =
                creditAccount.getLedgerBalance()
                        .add(request.getAmount());

        /*
         * 7. Create TransactionGroup
         */
        TransactionGroup transactionGroup = new TransactionGroup();

        transactionGroup.setExternalRef(
                request.getExternalRef());

        transactionGroup.setType(
                request.getType());
        
        transactionGroup.setStatus(TransactionStatus.POSTED);

        transactionGroup.setInitiatedBy("SERVICE");

        transactionGroup = transactionGroupRepository.save(transactionGroup);

        /*
         * 8. Create DEBIT LedgerEntry
         */
        LedgerEntry debitEntry = new LedgerEntry();

        debitEntry.setAccount(debitAccount);

        debitEntry.setTxnGroupId(
                transactionGroup.getId());

        debitEntry.setEntryType(EntryType.DEBIT);

        debitEntry.setAmount(
                request.getAmount());

        debitEntry.setBalanceAfter(
                debitAvailableAfter);

        debitEntry.setNarration(
                request.getNarration());

        debitEntry.setExternalRef(
                request.getExternalRef());

        ledgerEntryRepository.save(debitEntry);

        /*
         * 9. Create CREDIT LedgerEntry
         */
        LedgerEntry creditEntry = new LedgerEntry();

        creditEntry.setAccount(creditAccount);

        creditEntry.setTxnGroupId(transactionGroup.getId());

        creditEntry.setEntryType(EntryType.CREDIT);

        creditEntry.setAmount(request.getAmount());

        creditEntry.setBalanceAfter(creditAvailableAfter);

        creditEntry.setNarration(request.getNarration());

        creditEntry.setExternalRef(request.getExternalRef());

        ledgerEntryRepository.save(creditEntry);

        /*
         * 10. Update debit account
         */
        debitAccount.setAvailableBalance(debitAvailableAfter);

        debitAccount.setLedgerBalance(debitLedgerAfter);

        accountRepository.save(debitAccount);

        /*
         * 11. Update credit account
         */
        creditAccount.setAvailableBalance(creditAvailableAfter);

        creditAccount.setLedgerBalance(creditLedgerAfter);

        accountRepository.save(creditAccount);

        /*
         * 12. Return response
         */
        return new TransactionResponse(
                transactionGroup.getId(),
                transactionGroup.getExternalRef(),
                transactionGroup.getStatus(),
                "Transaction posted successfully"
        );
    }

    private void validateRequest(TransactionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request body is required");
        }

        if (request.getExternalRef() == null ||
                request.getExternalRef().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "externalRef is required");
        }

        if (request.getDebitAccount() == null ||
                request.getDebitAccount().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "debitAccount is required");
        }

        if (request.getCreditAccount() == null ||
                request.getCreditAccount().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "creditAccount is required");
        }

        if (request.getAmount() == null ||
                request.getAmount().compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "amount must be greater than zero");
        }

        if (request.getType() == null ||
                request.getType().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "type is required");
        }

        if (request.getNarration() == null ||
                request.getNarration().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "narration is required");
        }

        if (request.getDebitAccount()
                .equals(request.getCreditAccount())) {

            throw new IllegalArgumentException(
                    "Debit and credit accounts cannot be same");
        }
    }
    
    @Transactional
    public void reverseTransaction(String externalRef, String initiatedBy) {

    	// 1. Fetch original transaction group
    	TransactionGroup originalGroup =
    	        transactionGroupRepository.findByExternalRef(externalRef)
    	                .orElseThrow(() ->
    	                        new TransactionNotFoundException(
    	                                "Transaction not found: " + externalRef));

    	// 2. Check original transaction status
    	if (originalGroup.getStatus() != TransactionStatus.POSTED) {

    	    throw new InvalidTransactionStatusException(
    	            "Only POSTED transactions can be reversed");
    	}

    	// 3. Check whether reversal already exists
    	String reversalExternalRef = "REV-" + externalRef;

    	if (transactionGroupRepository.existsByExternalRef(reversalExternalRef)) {

    	    throw new TransactionAlreadyReversedException(
    	            "Transaction already reversed: " + externalRef);
    	}

        // 4. Fetch ledger entries belonging to original transaction
        List<LedgerEntry> originalEntries = ledgerEntryRepository.findByTxnGroupId( originalGroup.getId());

        // 5. Settlement check
        if (originalEntries.size() > 2) {

            throw new RuntimeException(
                    "Transaction contains more than two ledger entries. "
                    + "Settlement is already cleared and cannot be reversed.");
        }

        // Also make sure we actually have the expected double-entry
        if (originalEntries.size() != 2) {

            throw new RuntimeException(
                    "Invalid transaction. Expected exactly two ledger entries.");
        }

        // 6. Create REV transaction group with IN_PROGRESS
        TransactionGroup reversalGroup = new TransactionGroup();

        reversalGroup.setExternalRef(reversalExternalRef);
        reversalGroup.setType("REVERSAL");
        reversalGroup.setStatus(TransactionStatus.IN_PROGRESS);
        reversalGroup.setInitiatedBy(initiatedBy);
        reversalGroup.setCreatedAt(LocalDateTime.now());

        reversalGroup = transactionGroupRepository.save(reversalGroup);

        // 7. Reverse both ledger entries
        for (LedgerEntry originalEntry : originalEntries) {

        	Account account = accountRepository.findById(originalEntry.getAccount().getId()
        	).orElseThrow(() ->
        	        new RuntimeException(
        	                "Account not found: "
        	                + originalEntry.getAccount().getId()
        	        )
        	);

            BigDecimal amount = originalEntry.getAmount();

            BigDecimal currentBalance = account.getAvailableBalance();

            LedgerEntry reversalEntry = new LedgerEntry();

            reversalEntry.setAccount(account);
            reversalEntry.setTxnGroupId(reversalGroup.getId());

            // DEBIT becomes CREDIT
            if (originalEntry.getEntryType() == EntryType.DEBIT) {

                reversalEntry.setEntryType(EntryType.CREDIT);

                BigDecimal newBalance = currentBalance.add(amount);

                account.setAvailableBalance(newBalance);
                account.setLedgerBalance(account.getLedgerBalance().add(amount));
                reversalEntry.setBalanceAfter(newBalance);

            }

            // CREDIT becomes DEBIT
            else {

                reversalEntry.setEntryType(EntryType.DEBIT);

                BigDecimal newBalance = currentBalance.subtract(amount);

                account.setAvailableBalance(newBalance);
                account.setLedgerBalance( account.getLedgerBalance().subtract(amount));
                reversalEntry.setBalanceAfter(newBalance);
            }

            reversalEntry.setAmount(amount);
            reversalEntry.setNarration("Reversal of transaction " + externalRef);
            reversalEntry.setExternalRef(reversalExternalRef);
            reversalEntry.setCreatedAt(LocalDateTime.now());

            ledgerEntryRepository.save(reversalEntry);

            accountRepository.save(account);
        }

        // 8. Mark reversal transaction as REVERSED
        reversalGroup.setStatus(TransactionStatus.REVERSED);

        transactionGroupRepository.save(reversalGroup);

        // 9. Mark original transaction as REVERSED
        originalGroup.setStatus(TransactionStatus.REVERSED);

        transactionGroupRepository.save(originalGroup);
    }
}