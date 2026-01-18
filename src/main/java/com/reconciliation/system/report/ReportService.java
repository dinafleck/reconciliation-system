package com.reconciliation.system.report;

import org.springframework.web.multipart.MultipartFile;

public interface ReportService {
    void saveSaleTransactions(MultipartFile fileParts) throws Exception;

    void saveBankTransactions(MultipartFile fileParts) throws Exception;
}
