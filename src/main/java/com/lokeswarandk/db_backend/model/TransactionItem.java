package com.lokeswarandk.db_backend.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction_item")
public class TransactionItem {

    @Id private Long id;

    @NotNull(message = "Transaction ID is required")
    private AggregateReference<Transaction, Long> transactionId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Nullable private String unit;

    public TransactionItem() {
        // Required by Spring Data JDBC for object materialization.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AggregateReference<Transaction, Long> getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(AggregateReference<Transaction, Long> transactionId) {
        this.transactionId = transactionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
