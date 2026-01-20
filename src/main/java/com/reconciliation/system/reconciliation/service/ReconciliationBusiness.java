package com.reconciliation.system.reconciliation.service;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReconciliationBusiness implements ReconciliationService {
    private final ReconciliationRepository reconciliationRepository;
    private final Connection connection;

    public ReconciliationBusiness(ReconciliationRepository reconciliationRepository, Connection connection) {
        this.reconciliationRepository = reconciliationRepository;
        this.connection = connection;
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

    @Override
    public List<SaleTransaction> reportReconciliation(LocalDate reconcileDate) throws SQLException {

        String reconciliationsQuery = """
                SELECT *
                FROM sale_transactions
                WHERE date = ?
                AND status = 'RECONCILED'
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(reconciliationsQuery);
        preparedStatement.setObject(1, reconcileDate);

        try (ResultSet reconciliationsSet = preparedStatement.executeQuery()) {
            return convertResultSetToSaleTransactions(reconciliationsSet);
        }
    }

    @Override
    public void reconcile(BankTransaction bankTransaction) throws SQLException {
        String bankDirection = bankTransaction.getDirection();
        String bankDescription = bankTransaction.getDescription();
        LocalDate startDate = LocalDate.from(bankTransaction.getPostDate());
        LocalDate endDate = LocalDate.from(startDate.atTime(LocalTime.MAX));

        if (bankDirection.equals("DEBIT")) {
            return;
        }

        List<SaleTransaction> saleTransactions = null;
        if (bankDescription.contains("PIX")) {
            String SaleTransactionsQuery = """
                    SELECT *
                    FROM sale_transactions
                    WHERE date BETWEEN ? AND ?
                    AND value = ?
                    AND status = 'PENDING'
                    AND description LIKE ?
                    """;

            PreparedStatement preparedStatement = connection.prepareStatement(SaleTransactionsQuery);
            preparedStatement.setObject(1, startDate);
            preparedStatement.setObject(2, endDate);
            preparedStatement.setObject(3, bankTransaction.getAmount());
            preparedStatement.setObject(3, "%PIX%");


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                saleTransactions = convertResultSetToSaleTransactions(resultSet);
            }
        }

        if (saleTransactions == null) {
            return;
        }

        Reconciliation reconciliation = new Reconciliation();

        for (SaleTransaction saleTransaction : saleTransactions) {
            if (saleTransaction == null) {
                continue;
            }
            if (saleTransaction.getGrossAmount().compareTo(bankTransaction.getAmount()) == 0) {
                reconciliation.addSaleTransaction(saleTransaction);

                String updateQuery = """
                        UPDATE sale_transactions
                        SET status = 'RECONCILED' 
                        SET bankTransactionId = ? 
                        WHERE saleId = ?
                        """;

                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setObject(1, bankTransaction.getTransactionId());
                updateStatement.setObject(2, saleTransaction.getSaleId());
            }
        }

        reconciliationRepository.save(reconciliation);
    }
}
