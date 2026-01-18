package com.reconciliation.system.report;

import java.sql.SQLException;

public interface ReportRepository {

    void save(Report report) throws SQLException;
}
