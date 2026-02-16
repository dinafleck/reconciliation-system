package com.reconciliation.system.banktransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransaction {
    private final String transactionId;
    private final LocalDateTime postDate;
    private final BigDecimal amount;
    private final String description;
    private final String direction;
    private final String status;

    public BankTransaction(
            String transactionId,
            LocalDateTime postDate,
            BigDecimal amount,
            String description,
            String direction
    ) {
        this.transactionId = transactionId;
        this.postDate = postDate;
        this.amount = amount;
        this.description = description;
        this.direction = direction;
        this.status = "PENDING";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDirection() {
        return direction;
    }

    public String getStatus() {
        return status;
    }
}

