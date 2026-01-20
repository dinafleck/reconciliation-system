package com.reconciliation.system.saletransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleTransaction {
    private final String saleId;
    private final BigDecimal grossAmount;
    private final String currency;
    private final LocalDateTime date;
    private final String paymentMethod;
    private final Integer installments;
    private final String status;
    private final String bankTransactionId;

    public SaleTransaction(
            String saleId,
            BigDecimal grossAmount,
            String currency,
            LocalDateTime date,
            String paymentMethod,
            Integer installments
    ) {
        this.saleId = saleId;
        this.grossAmount = grossAmount;
        this.currency = currency;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.installments = installments;
        this.status = "PENDING";
        this.bankTransactionId = null;
    }

    public SaleTransaction(
            String saleId,
            BigDecimal grossAmount,
            String currency,
            LocalDateTime date,
            String paymentMethod,
            Integer installments,
            String status,
            String bankTransactionId
    ) {
        this.saleId = saleId;
        this.grossAmount = grossAmount;
        this.currency = currency;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.installments = installments;
        this.status = status;
        this.bankTransactionId = bankTransactionId;
    }


    public String getSaleId() {
        return saleId;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getDate() {
        return date;
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

    public String getBankTransactionId() {
        return bankTransactionId;
    }


}
