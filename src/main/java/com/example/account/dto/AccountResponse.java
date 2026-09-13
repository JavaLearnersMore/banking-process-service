package com.example.account.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private String accountNumber;
    private String ownerType;
    private Long ownerRefId;
    private String ifsc;
    private String accountType;
    private String status;
    private BigDecimal availableBalance;

    public AccountResponse(
            String accountNumber,
            String ownerType,
            Long ownerRefId,
            String ifsc,
            String accountType,
            String status,
            BigDecimal availableBalance) {

        this.accountNumber = accountNumber;
        this.ownerType = ownerType;
        this.ownerRefId = ownerRefId;
        this.ifsc = ifsc;
        this.accountType = accountType;
        this.status = status;
        this.availableBalance = availableBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public Long getOwnerRefId() {
        return ownerRefId;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}