package com.example.account.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(
    name = "transaction_group",
    uniqueConstraints = {@UniqueConstraint(name = "uk_transaction_external_ref",columnNames = "external_ref")
    }
)
public class TransactionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_ref", nullable = false, unique = true)
    private String externalRef;

    @Column(name = "type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "initiated_by", nullable = false)
    private String initiatedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

	public void setCreatedAt(LocalDateTime now) {
		// TODO Auto-generated method stub
		
	}
}