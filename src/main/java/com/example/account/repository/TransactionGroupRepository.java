package com.example.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.account.entity.TransactionGroup;

public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, Long> {

    Optional<TransactionGroup> findByExternalRef(String externalRef);
}