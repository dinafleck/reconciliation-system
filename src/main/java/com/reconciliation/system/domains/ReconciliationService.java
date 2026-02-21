package com.reconciliation.system.domains;

import com.reconciliation.system.domains.models.Reconciliation;
import com.reconciliation.system.domains.models.SaleTransaction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReconciliationService {

    List<SaleTransaction> reportReconciliation(LocalDate date) throws SQLException;

    List<Reconciliation> reconcile() throws Exception;
}
