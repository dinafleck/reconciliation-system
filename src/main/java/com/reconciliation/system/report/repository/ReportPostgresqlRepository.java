package com.reconciliation.system.report.repository;

import com.reconciliation.system.report.Report;
import com.reconciliation.system.report.ReportRepository;
import com.reconciliation.system.saletransaction.SaleTransaction;
import com.reconciliation.system.saletransaction.SaleTransactionRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class ReportPostgresqlRepository implements ReportRepository {
    private final Connection connection;

    public ReportPostgresqlRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Report report) throws SQLException {
        String query = "INSERT INTO reports (id, reportReceivedAt) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, report.getId());
        preparedStatement.setObject(2, report.getReportReceivedAt());

        preparedStatement.executeUpdate();
    }
}
