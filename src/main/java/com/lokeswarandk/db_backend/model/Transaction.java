package com.lokeswarandk.db_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Table("transaction")
public class Transaction {

    @Id
    private Long id;

    @NotNull(message = "Receipt ID is required")
    private AggregateReference<Receipt, Long> receiptId;

    @NotNull(message = "Offering type ID is required")
    private AggregateReference<OfferingType, Long> offeringTypeId;

    @Nullable
    private AggregateReference<PaymentMode, Long> paymentModeId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    @Nullable
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Nullable
    private String notes;

    @Nullable
    private LocalDateTime createdAt;

    public Transaction() {
        // Required by Spring Data JDBC for object materialization.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AggregateReference<Receipt, Long> getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(AggregateReference<Receipt, Long> receiptId) {
        this.receiptId = receiptId;
    }

    public AggregateReference<OfferingType, Long> getOfferingTypeId() {
        return offeringTypeId;
    }

    public void setOfferingTypeId(AggregateReference<OfferingType, Long> offeringTypeId) {
        this.offeringTypeId = offeringTypeId;
    }

    public AggregateReference<PaymentMode, Long> getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(AggregateReference<PaymentMode, Long> paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
