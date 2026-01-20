package com.reconciliation.system.reconciliation;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.saletransaction.SaleTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReconciliationService {

    List<SaleTransaction> reportReconciliation(LocalDate date) throws SQLException;

    void reconcile(BankTransaction bankTransaction) throws SQLException;
}
