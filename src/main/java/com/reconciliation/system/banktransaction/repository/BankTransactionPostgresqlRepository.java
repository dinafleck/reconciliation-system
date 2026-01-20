package com.reconciliation.system.banktransaction.repository;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.banktransaction.BankTransactionRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class BankTransactionPostgresqlRepository implements BankTransactionRepository {
    private final Connection connection;

    public BankTransactionPostgresqlRepository(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void save(BankTransaction bankTransaction) throws SQLException {
        String query = """
                INSERT INTO bank_transactions (
                                               bankTransactionId,
                                               post_date,
                                               amount,
                                               currency,
                                               description,
                                               direction
                                                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, bankTransaction.getTransactionId());
        preparedStatement.setObject(2, bankTransaction.getPostDate());
        preparedStatement.setObject(3, bankTransaction.getAmount());
        preparedStatement.setObject(4, bankTransaction.getCurrency());
        preparedStatement.setObject(5, bankTransaction.getDescription());
        preparedStatement.setObject(6, bankTransaction.getDirection());

        preparedStatement.executeUpdate();

    }
}
