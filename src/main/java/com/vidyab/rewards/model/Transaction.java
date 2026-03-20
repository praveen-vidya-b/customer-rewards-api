package com.vidyab.rewards.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a customer purchase transaction.
 */
public class Transaction {

    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private LocalDate transactionDate;

    public Transaction() {
    }

    public Transaction(Long id, Long customerId, String customerName, BigDecimal amount, LocalDate transactionDate) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}