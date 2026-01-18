package com.reconciliation.system.saletransaction;

import java.sql.SQLException;

public interface SaleTransactionRepository {

    void save(SaleTransaction saleTransaction) throws SQLException;
}
