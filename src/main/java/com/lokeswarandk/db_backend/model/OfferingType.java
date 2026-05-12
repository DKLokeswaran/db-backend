package com.lokeswarandk.db_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Table("offering_type")
public class OfferingType {

    @Id
    private Long id;

    @NotBlank(message = "OfferingType name is required")
    private String name;

    @NotNull(message = "Offering category is required")
    private OfferingCategory category;

    @NotNull(message = "Pricing type is required")
    private PricingType pricingType;

    @Nullable
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Allow multiple flag is required")
    private Boolean allowMultiple;

    @NotNull(message = "Is active flag is required")
    private Boolean isActive;

    @Nullable
    private LocalDateTime createdAt;

    public OfferingType() {
        // Required by Spring Data JDBC for object materialization.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OfferingCategory getCategory() {
        return category;
    }

    public void setCategory(OfferingCategory category) {
        this.category = category;
    }

    public PricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingType pricingType) {
        this.pricingType = pricingType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(Boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
