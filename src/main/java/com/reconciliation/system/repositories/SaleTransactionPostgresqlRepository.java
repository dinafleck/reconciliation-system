package com.reconciliation.system.repositories;

import com.reconciliation.system.domains.SaleTransactionRepository;
import com.reconciliation.system.domains.models.SaleTransaction;
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
                                          date,
                                          PaymentMethod,
                                          installments,
                                          status,
                                          netAmount,
                                          receivedAt
                                          ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, UUID.randomUUID());
        preparedStatement.setObject(2, saleTransaction.getSaleId());
        preparedStatement.setObject(3, saleTransaction.getGrossAmount());
        preparedStatement.setObject(4, saleTransaction.getOrderDate());
        preparedStatement.setObject(5, saleTransaction.getPaymentMethod());
        preparedStatement.setObject(6, saleTransaction.getInstallments());
        preparedStatement.setObject(7, saleTransaction.getStatus());
        preparedStatement.setObject(8, saleTransaction.getNetAmount());
        preparedStatement.setObject(9, saleTransaction.getReceivedAt());

        preparedStatement.executeUpdate();
    }
}
