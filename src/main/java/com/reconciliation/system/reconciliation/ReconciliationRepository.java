package com.reconciliation.system.reconciliation;

import java.sql.SQLException;

public interface ReconciliationRepository {
    void save(Reconciliation reconciliation) throws SQLException;
}
