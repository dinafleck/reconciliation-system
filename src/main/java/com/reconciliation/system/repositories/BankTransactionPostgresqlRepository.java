package com.reconciliation.system.repositories;

import com.reconciliation.system.domains.BankTransactionRepository;
import com.reconciliation.system.domains.models.BankTransaction;
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
                                               description,
                                               direction,
                                               status
                                                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, bankTransaction.getTransactionId());
        preparedStatement.setObject(2, bankTransaction.getPostDate());
        preparedStatement.setObject(3, bankTransaction.getAmount());
        preparedStatement.setObject(4, bankTransaction.getDescription());
        preparedStatement.setObject(5, bankTransaction.getDirection());
        preparedStatement.setObject(6, bankTransaction.getStatus());

        preparedStatement.executeUpdate();

    }
}
