package com.example.account.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class AccountRequest {

    @NotBlank(message = "Account number is required")
    @Pattern(
        regexp = "^AC\\d{10}$",
        message = "Account number must be in format AC followed by 10 digits"
    )
    private String accountNumber;

    @NotBlank(message = "Owner type is required")
    @Pattern(
        regexp = "CUSTOMER|MERCHANT",
        message = "Owner type must be CUSTOMER or MERCHANT"
    )
    private String ownerType;

    @NotNull(message = "Owner reference ID is required")
    @Positive(message = "Owner reference ID must be greater than 0")
    private Long ownerRefId;

    @NotBlank(message = "IFSC is required")
    @Pattern(
        regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
        message = "Invalid IFSC code"
    )
    private String ifsc;

    @NotBlank(message = "Account type is required")
    @Pattern(
        regexp = "SAVINGS|CURRENT",
        message = "Account type must be SAVINGS or CURRENT"
    )
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