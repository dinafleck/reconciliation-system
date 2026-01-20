package com.reconciliation.system.report.controller;

import com.reconciliation.system.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    @PostMapping("/sale-transactions")
    public ResponseEntity<String> sales(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        reportService.saveSaleTransactions(file);

        return ResponseEntity.ok().body("Successfully uploaded");
    }

    @PostMapping("/bank-transactions")
    public ResponseEntity<String> bank(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        reportService.saveBankTransactions(file);

        return ResponseEntity.ok().body("Successfully uploaded");
    }

}
