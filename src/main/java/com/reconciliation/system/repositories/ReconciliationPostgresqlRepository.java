package com.reconciliation.system.repositories;

import com.reconciliation.system.domains.ReconciliationRepository;
import com.reconciliation.system.domains.models.BankTransaction;
import com.reconciliation.system.domains.models.Reconciliation;
import com.reconciliation.system.domains.models.SaleTransaction;
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
import java.util.UUID;

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
                                            reconciliationDate,
                                            bankTransactionDate,
                                            totalSaleTransactionAmount,
                                            totalBankTransactionAmount,
                                            totalReconciliationAmount
                                            ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, reconciliation.getMatchID());
        preparedStatement.setObject(2, reconciliation.getReconciliationDate());
        preparedStatement.setObject(3, reconciliation.getBankTransactionDate());
        preparedStatement.setObject(4, reconciliation.getTotalSaleTransactionAmount());
        preparedStatement.setObject(5, reconciliation.getTotalBankTransactionAmount());
        preparedStatement.setObject(6, reconciliation.getTotalReconciliationAmount());

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

        List<SaleTransaction> saleTransactions;

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            saleTransactions = convertResultSetToSaleTransactions(resultSet);
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
                String description = resultSet.getString("description");
                String direction = resultSet.getString("direction");

                BankTransaction bankTransaction = new BankTransaction(
                        transactionId,
                        postDate,
                        amount,
                        description,
                        direction
                );
                bankTransactions.add(bankTransaction);
            }
        }
        return bankTransactions;
    }

    @Override
    public void updateReconciliationStatus(List<SaleTransaction> saleTransactions, List<BankTransaction> bankTransactions, UUID matchID) throws SQLException {
        for (SaleTransaction saleTransaction : saleTransactions) {
            String saleId = saleTransaction.getSaleId();

            String updateQuery = """
                    UPDATE sale_transactions
                    SET status = 'RECONCILED',
                        matchID = ?
                    WHERE saleId = ?
                    """;

            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setObject(1, matchID);
            updateStatement.setObject(2, saleId);

            updateStatement.executeUpdate();

            System.out.println("Status updated for saleId: " + saleId + " and matchID: " + matchID);
        }

        for (BankTransaction bankTransaction : bankTransactions) {
            String transactionId = bankTransaction.getTransactionId();

            String updateQuery = """
                    UPDATE bank_transactions
                    SET status = 'RECONCILED' ,
                        matchID = ?
                    WHERE bankTransactionId = ?
                    """;

            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setObject(1, matchID);
            updateStatement.setObject(2, transactionId);

            updateStatement.executeUpdate();

        }
    }

    @Override
    public List<SaleTransaction> getReconciledSales(LocalDate reconciliationDate) throws SQLException {

        String reconciliationsQuery = """
                SELECT *
                FROM sale_transactions
                WHERE receivedAt = ?
                AND status = 'RECONCILED'
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(reconciliationsQuery);
        preparedStatement.setObject(1, reconciliationDate);

        System.out.println("Retrieving reconciled sales for date: " + reconciliationDate);

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
            LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
            String paymentMethod = resultSet.getString("paymentMethod");
            Integer installments = resultSet.getInt("installments");
            String status = resultSet.getString("status");
            String matchID = resultSet.getString("matchID");
            BigDecimal netAmount = resultSet.getBigDecimal("netAmount");
            LocalDateTime receivedAt = resultSet.getTimestamp("receivedAt").toLocalDateTime();
            String clientName = resultSet.getString("clientName");


            SaleTransaction saleTransaction = new SaleTransaction(
                    saleId,
                    grossAmount,
                    date,
                    paymentMethod,
                    installments,
                    status,
                    matchID,
                    netAmount,
                    receivedAt,
                    clientName
            );
            saleTransactions.add(saleTransaction);
        }
        return saleTransactions;
    }

}
