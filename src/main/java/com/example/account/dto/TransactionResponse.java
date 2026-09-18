package com.example.account.dto;

public class TransactionResponse {

    private Long transactionGroupId;
    private String externalRef;
    private String status;
    private String message;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long transactionGroupId,
            String externalRef,
            String status,
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

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}