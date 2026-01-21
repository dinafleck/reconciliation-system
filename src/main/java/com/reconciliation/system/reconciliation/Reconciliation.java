package com.reconciliation.system.reconciliation;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.saletransaction.SaleTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reconciliation {
    private final UUID matchID = UUID.randomUUID();
    private final LocalDateTime reconciliationDate = LocalDateTime.now();
    private final List<SaleTransaction> saleTransactions = new ArrayList<>();
    private final List<BankTransaction> bankTransactions = new ArrayList<>();

    public void addSaleTransaction(SaleTransaction saleTransaction) {
        saleTransactions.add(saleTransaction);
    }

    public void addBankTransaction(BankTransaction bankTransaction) {
        bankTransactions.add(bankTransaction);
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

}
