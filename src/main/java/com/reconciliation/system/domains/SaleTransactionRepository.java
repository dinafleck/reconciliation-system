package com.reconciliation.system.domains;

import com.reconciliation.system.domains.models.SaleTransaction;

import java.sql.SQLException;

public interface SaleTransactionRepository {

    void save(SaleTransaction saleTransaction) throws SQLException;
}
