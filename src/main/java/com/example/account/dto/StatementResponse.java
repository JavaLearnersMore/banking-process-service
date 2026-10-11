package com.example.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.account.entity.EntryType;

public class StatementResponse {

    private EntryType entryType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String narration;
    private String externalRef;
    private LocalDateTime createdAt;

    public StatementResponse(
    		EntryType entryType,
            BigDecimal amount,
            BigDecimal balanceAfter,
            String narration,
            String externalRef,
            LocalDateTime createdAt) {

        this.entryType = entryType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.narration = narration;
        this.externalRef = externalRef;
        this.createdAt = createdAt;
    }

	public EntryType getEntryType() {
		return entryType;
	}

	public void setEntryType(EntryType entryType) {
		this.entryType = entryType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}