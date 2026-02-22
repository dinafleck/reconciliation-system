package com.reconciliation.system.domains.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleTransaction {
    private final String saleId;
    private final BigDecimal grossAmount;
    private final LocalDateTime orderDate;
    private final String paymentMethod;
    private final Integer installments;
    private final String status;
    private final String matchID;
    private final BigDecimal netAmount;
    private LocalDateTime receivedAt;
    private final String clientName;

    public SaleTransaction(
            String saleId,
            BigDecimal grossAmount,
            LocalDateTime orderDate,
            String paymentMethod,
            Integer installments,
            BigDecimal netAmount,
            LocalDateTime receivedAt,
            String clientName
    ) {
        this.saleId = saleId;
        this.grossAmount = grossAmount;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.installments = installments;
        this.status = "PENDING";
        this.matchID = null;
        this.netAmount = netAmount;
        this.receivedAt = receivedAt;
        this.clientName = clientName;
    }

    public SaleTransaction(
            String saleId,
            BigDecimal grossAmount,
            LocalDateTime orderDate,
            String paymentMethod,
            Integer installments,
            String status,
            String matchID,
            BigDecimal netAmount,
            LocalDateTime receivedAt,
            String clientName
    ) {
        this.saleId = saleId;
        this.grossAmount = grossAmount;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.installments = installments;
        this.status = status;
        this.matchID = matchID;
        this.netAmount = netAmount;
        this.receivedAt = receivedAt;
        this.clientName = clientName;
    }


    public String getSaleId() {
        return saleId;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Integer getInstallments() {
        return installments;
    }

    public String getStatus() {
        return status;
    }

    public String getMatchID() {
        return matchID;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime date) {
        this.receivedAt = date;
    }

    public String getClientName() {
        return clientName;
    }


}
