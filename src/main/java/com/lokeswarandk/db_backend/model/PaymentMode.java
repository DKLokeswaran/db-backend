package com.lokeswarandk.db_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;

@Table("payment_mode")
public class PaymentMode {

    @Id
    private Long id;

    @NotBlank(message = "PaymentMode name is required")
    private String name;

    public PaymentMode() {
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
}
