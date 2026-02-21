package com.reconciliation.system.domains;

import com.reconciliation.system.domains.models.BankTransaction;
import com.reconciliation.system.domains.models.Reconciliation;
import com.reconciliation.system.domains.models.SaleTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReconciliationRepository {
    void save(Reconciliation reconciliation) throws SQLException;

    List<SaleTransaction> getPendingSales() throws SQLException;

    List<BankTransaction> getPendingBankTransactions() throws SQLException;

    void updateReconciliationStatus(List<SaleTransaction> saleTransactions, List<BankTransaction> bankTransactions, UUID matchID) throws SQLException;

    List<SaleTransaction> getReconciledSales(LocalDate reconciliationDate) throws SQLException;
}
