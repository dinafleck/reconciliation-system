package com.reconciliation.system.reconciliation.repository;

import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                                            ) VALUES (?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setObject(1, reconciliation.getMatchID());
        preparedStatement.setObject(2, reconciliation.getReconciliationDate());

        preparedStatement.executeUpdate();
    }

}
