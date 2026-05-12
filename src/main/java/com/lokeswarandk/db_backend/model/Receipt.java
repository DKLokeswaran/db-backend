package com.lokeswarandk.db_backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Table("receipt")
public class Receipt {

    @Id
    private Long id;

    @NotNull(message = "Event ID is required")
    private AggregateReference<Event, Long> eventId;

    @NotNull(message = "Receipt number is required")
    @Min(value = 1, message = "Receipt number must be positive")
    private Integer receiptNo;

    @Nullable
    private AggregateReference<User, Long> userId;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @Nullable
    private String displayLocality;

    @Nullable
    private LocalDateTime createdAt;

    public Receipt() {
        // Required by Spring Data JDBC for object materialization.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AggregateReference<Event, Long> getEventId() {
        return eventId;
    }

    public void setEventId(AggregateReference<Event, Long> eventId) {
        this.eventId = eventId;
    }

    public Integer getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(Integer receiptNo) {
        this.receiptNo = receiptNo;
    }

    public AggregateReference<User, Long> getUserId() {
        return userId;
    }

    public void setUserId(AggregateReference<User, Long> userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayLocality() {
        return displayLocality;
    }

    public void setDisplayLocality(String displayLocality) {
        this.displayLocality = displayLocality;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
