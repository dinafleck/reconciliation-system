package com.reconciliation.system.banktransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransaction {
    private final String bank_tx_id;
    private final LocalDateTime post_date;
    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final String direction;

    public BankTransaction(
            String bank_tx_id,
            LocalDateTime post_date,
            BigDecimal amount,
            String currency,
            String description,
            String direction
    ){
        this.bank_tx_id = bank_tx_id;
        this.post_date = post_date;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.direction = direction;
    }

    public String getBank_tx_id() {
        return bank_tx_id;
    }

    public LocalDateTime getPost_date() {
        return post_date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getDirection() {
        return direction;
    }
}

