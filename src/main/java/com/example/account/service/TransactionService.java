package com.example.account.service;

import java.math.BigDecimal;
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
import com.example.account.repository.AccountRepository;
import com.example.account.repository.LedgerEntryRepository;
import com.example.account.repository.TransactionGroupRepository;

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

        transactionGroup.setStatus("POSTED");

        transactionGroup.setInitiatedBy("SERVICE");

        transactionGroup = transactionGroupRepository.save(transactionGroup);

        /*
         * 8. Create DEBIT LedgerEntry
         */
        LedgerEntry debitEntry = new LedgerEntry();

        debitEntry.setAccount(debitAccount);

        debitEntry.setTxnGroupId(
                transactionGroup.getId());

        debitEntry.setEntryType(
                "DEBIT");

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

        creditEntry.setTxnGroupId(
                transactionGroup.getId());

        creditEntry.setEntryType("CREDIT");

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
}