package com.reconciliation.system.domains.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Report {
    private final UUID id = UUID.randomUUID();
    private final LocalDateTime reportReceivedAt = LocalDateTime.now();
    private final List<SaleTransaction> saleTransactions = new ArrayList<SaleTransaction>();
    private final List<BankTransaction> bankTransactions = new ArrayList<>();
    private final List<Reconciliation> reconciliations = new ArrayList<>();

    public List<SaleTransaction> getSaleTransactions() {
        return saleTransactions;
    }

    public List<BankTransaction> getBankTransactions() {
        return bankTransactions;
    }

    public void addSalesTransaction(SaleTransaction saleTransaction) {
        saleTransactions.add(saleTransaction);
    }

    public void addBankTransaction(BankTransaction bankTransaction) {
        bankTransactions.add(bankTransaction);
    }

    public void addReconciliation(Reconciliation reconciliation) {
        reconciliations.add(reconciliation);
    }

    public LocalDateTime getReportReceivedAt() {
        return reportReceivedAt;
    }

    public UUID getId() {
        return id;
    }
}
