package com.reconciliation.system.domains;

import com.reconciliation.system.domains.models.BankTransaction;

import java.sql.SQLException;

public interface BankTransactionRepository {

    void save(BankTransaction bankTransaction) throws SQLException;
}
