package com.reconciliation.system.saletransaction.repository;

import com.reconciliation.system.saletransaction.SaleTransaction;
import com.reconciliation.system.saletransaction.SaleTransactionRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class SaleTransactionPostgresqlRepository implements SaleTransactionRepository {
    private final Connection connection;

    public SaleTransactionPostgresqlRepository(Connection connection) {
        this.connection = connection;

    }

    @Override
    public void save(SaleTransaction saleTransaction) throws SQLException {
        String query = """
                INSERT INTO sale_transactions (
                                          id,
                                          saleId,
                                          grossAmount,
                                          currency,
                                          date,
                                          PaymentMethod,
                                          installments
                                          ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, UUID.randomUUID());
        preparedStatement.setObject(2, saleTransaction.getSaleId());
        preparedStatement.setObject(3, saleTransaction.getGrossAmount());
        preparedStatement.setObject(4, saleTransaction.getCurrency());
        preparedStatement.setObject(5, saleTransaction.getDate());
        preparedStatement.setObject(6, saleTransaction.getPaymentMethod());
        preparedStatement.setObject(7, saleTransaction.getInstallments());

        preparedStatement.executeUpdate();
    }
}
