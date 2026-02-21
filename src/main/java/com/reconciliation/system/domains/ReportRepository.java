package com.reconciliation.system.domains;

import com.reconciliation.system.domains.models.Report;

import java.sql.SQLException;

public interface ReportRepository {

    void save(Report report) throws SQLException;
}
