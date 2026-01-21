package com.reconciliation.system.reconciliation.repository;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReconciliationPostgresqlRepository implements ReconciliationRepository {
    private final Connection connection;

    public ReconciliationPostgresqlRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Reconciliation reconciliation) throws SQLException {
        String query = """
                INSERT INTO reconciliations (
                                            matchID,
                                            reconciliationDate
                                            ) VALUES (?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, reconciliation.getMatchID());
        preparedStatement.setObject(2, reconciliation.getReconciliationDate());

        preparedStatement.executeUpdate();
    }

    @Override
    public List<SaleTransaction> getPendingSales() throws SQLException {

        String pendingSalesQuery = """
                    SELECT *
                    FROM sale_transactions
                    WHERE status = 'PENDING'
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(pendingSalesQuery);

        List<SaleTransaction> saleTransactions = new ArrayList<>();

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String saleId = resultSet.getString("saleId");
                BigDecimal grossAmount = resultSet.getBigDecimal("grossAmount");
                String currency = resultSet.getString("currency");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String paymentMethod = resultSet.getString("paymentMethod");
                Integer installments = resultSet.getInt("installments");
                String status = resultSet.getString("status");
                String bankTransactionId = resultSet.getString("bankTransactionId");


                SaleTransaction saleTransaction = new SaleTransaction(
                        saleId,
                        grossAmount,
                        currency,
                        date,
                        paymentMethod,
                        installments,
                        status,
                        bankTransactionId
                );
                saleTransactions.add(saleTransaction);
            }
        }

        return saleTransactions;
    }

    @Override
    public List<BankTransaction> getPendingBankTransactions() throws SQLException {

        String pendingBankQuery = """
                    SELECT *
                    FROM bank_transactions
                    WHERE status = 'PENDING'
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(pendingBankQuery);

        List<BankTransaction> bankTransactions = new ArrayList<>();

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String transactionId = resultSet.getString("bankTransactionId");
                LocalDateTime postDate = resultSet.getTimestamp("post_date").toLocalDateTime();
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String currency = resultSet.getString("currency");
                String description = resultSet.getString("description");
                String direction = resultSet.getString("direction");

                BankTransaction bankTransaction = new BankTransaction(
                        transactionId,
                        postDate,
                        amount,
                        currency,
                        description,
                        direction
                );
                bankTransactions.add(bankTransaction);
            }
        }
        return bankTransactions;
    }

    @Override
    public void updateReconciliationStatus(String saleId, String bankId) throws SQLException {

        String updateQuery = """
                                UPDATE sale_transactions
                                SET status = 'RECONCILED' 
                                SET bankTransactionId = ? 
                                WHERE saleId = ?
                                """;

        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setObject(1, bankId);
        updateStatement.setObject(2, saleId);




    }

    @Override
    public List<SaleTransaction> getReconciledSales(LocalDate reconciliationDate) throws SQLException {

        String reconciliationsQuery = """
                SELECT *
                FROM sale_transactions
                WHERE date = ?
                AND status = 'RECONCILED'
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(reconciliationsQuery);
        preparedStatement.setObject(1, reconciliationDate);

        try (ResultSet reconciliationsSet = preparedStatement.executeQuery()) {
            return convertResultSetToSaleTransactions(reconciliationsSet);
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving reconciled sales", e);
        }

    }

    private List<SaleTransaction> convertResultSetToSaleTransactions(ResultSet resultSet) throws SQLException {
        List<SaleTransaction> saleTransactions = new ArrayList<>();
        while (resultSet.next()) {
            String saleId = resultSet.getString("saleId");
            BigDecimal grossAmount = resultSet.getBigDecimal("grossAmount");
            String currency = resultSet.getString("currency");
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            String paymentMethod = resultSet.getString("paymentMethod");
            Integer installments = resultSet.getInt("installments");
            String status = resultSet.getString("status");
            String bankTransactionId = resultSet.getString("bankTransactionId");


            SaleTransaction saleTransaction = new SaleTransaction(
                    saleId,
                    grossAmount,
                    currency,
                    date,
                    paymentMethod,
                    installments,
                    status,
                    bankTransactionId
            );
            saleTransactions.add(saleTransaction);
        }
        return saleTransactions;
    }

}
