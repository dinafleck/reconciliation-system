package com.reconciliation.system.reconciliation;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.saletransaction.SaleTransaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reconciliation {
    private final UUID matchID = UUID.randomUUID();
    private final LocalDateTime reconciliationDate = LocalDateTime.now();
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
        System.out.println("Calculating total amount for " + reconciliationDate);
        for (SaleTransaction saleTransaction : saleTransactions) {
            System.out.println(saleTransaction.toString());
            totalSaleTransactionAmount = totalSaleTransactionAmount.add(saleTransaction.getGrossAmount());
        }

        for (BankTransaction bankTransaction : bankTransactions) {
            System.out.println(bankTransaction.toString());
            totalBankTransactionAmount = totalBankTransactionAmount.add(bankTransaction.getAmount());
        }

        totalReconciliationAmount = totalBankTransactionAmount.subtract(totalSaleTransactionAmount).abs();
        System.out.println("Total amount for " + reconciliationDate);
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
}
