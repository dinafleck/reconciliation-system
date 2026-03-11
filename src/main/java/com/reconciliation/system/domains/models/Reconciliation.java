package com.reconciliation.system.domains.models;

import com.reconciliation.system.domains.ReconciliationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reconciliation {
    private final UUID matchID = UUID.randomUUID();
    private final LocalDateTime reconciliationDate = LocalDateTime.now();
    private LocalDate bankTransactionDate;
    private final List<SaleTransaction> saleTransactions = new ArrayList<>();
    private final List<BankTransaction> bankTransactions = new ArrayList<>();
    private BigDecimal totalSaleTransactionAmount = BigDecimal.ZERO;
    private BigDecimal totalBankTransactionAmount = BigDecimal.ZERO;
    private BigDecimal totalReconciliationAmount = BigDecimal.ZERO;

    public void save(ReconciliationRepository reconciliationRepository) throws Exception {

        calculateTotalAmounts();

        reconciliationRepository.save(this);
    }

    public void calculateTotalAmounts() {
        for (SaleTransaction saleTransaction : saleTransactions) {
            totalSaleTransactionAmount = totalSaleTransactionAmount.add(saleTransaction.getNetAmount());
        }

        for (BankTransaction bankTransaction : bankTransactions) {
            totalBankTransactionAmount = totalBankTransactionAmount.add(bankTransaction.getAmount());
        }

        totalReconciliationAmount = totalBankTransactionAmount.subtract(totalSaleTransactionAmount).abs();
    }

    public void addAllSaleTransaction(List<SaleTransaction> saleTransactionToAdd) {
        saleTransactions.addAll(saleTransactionToAdd);
    }

    public void addAllBankTransaction(List<BankTransaction> bankTransactionsToAdd) {
        bankTransactions.addAll(bankTransactionsToAdd);
    }

    public LocalDateTime getReconciliationDate() {
        return reconciliationDate;
    }

    public UUID getMatchID() {
        return matchID;
    }

    public List<SaleTransaction> getSaleTransactions() {
        return saleTransactions;
    }

    public List<BankTransaction> getBankTransactions() {
        return bankTransactions;
    }

    public BigDecimal getTotalSaleTransactionAmount() {
        return totalSaleTransactionAmount;
    }

    public BigDecimal getTotalBankTransactionAmount() {
        return totalBankTransactionAmount;
    }

    public BigDecimal getTotalReconciliationAmount() {
        return totalReconciliationAmount;
    }

    public LocalDate getBankTransactionDate() {
        return bankTransactionDate;
    }

    public void setBankTransactionDate(LocalDate bankTransactionDate) {
        this.bankTransactionDate = bankTransactionDate;
    }
}
