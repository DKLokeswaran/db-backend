package com.lokeswarandk.db_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

@Table("event")
public class Event {

    @Id
    private Long id;

    @NotBlank(message = "Event name is required")
    private String name;

    @Nullable
    private String description;

    @Nullable
    private LocalDate startDate;

    @Nullable
    private LocalDate endDate;

    @Nullable
    private LocalDateTime createdAt;

    public Event() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
