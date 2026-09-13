package com.example.account.dto;

public class AccountPageRequest {

    private String accountNumber;
    private String ownerType;
    private Long ownerRefId;
    private String ifsc;
    private String accountType;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public Long getOwnerRefId() {
        return ownerRefId;
    }

    public void setOwnerRefId(Long ownerRefId) {
        this.ownerRefId = ownerRefId;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}