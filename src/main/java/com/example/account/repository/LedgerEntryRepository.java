package com.example.account.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.account.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByTxnGroupId(Long txnGroupId);
    
    Page<LedgerEntry> findByAccount_AccountNumber( String accountNumber, Pageable pageable );
}

   