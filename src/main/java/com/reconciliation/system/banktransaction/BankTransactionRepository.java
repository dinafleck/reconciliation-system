package com.reconciliation.system.banktransaction;

import java.sql.SQLException;

public interface BankTransactionRepository {

    void save(BankTransaction bankTransaction) throws SQLException;
}
