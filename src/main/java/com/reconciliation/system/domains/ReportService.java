package com.reconciliation.system.domains;

import org.springframework.web.multipart.MultipartFile;

public interface ReportService {
    void saveSaleTransactions(MultipartFile fileParts) throws Exception;

    void saveBankTransactions(MultipartFile fileParts) throws Exception;

}
