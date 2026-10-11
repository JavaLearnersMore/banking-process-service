package com.example.account.dto;

import com.example.account.entity.TransactionStatus;

public class TransactionResponse {

    private Long transactionGroupId;
    private String externalRef;
    private TransactionStatus status;
    private String message;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long transactionGroupId,
            String externalRef,
            TransactionStatus status,
            String message) {

        this.transactionGroupId = transactionGroupId;
        this.externalRef = externalRef;
        this.status = status;
        this.message = message;
    }

    public Long getTransactionGroupId() {
        return transactionGroupId;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}