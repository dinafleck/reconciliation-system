package com.reconciliation.system.reconciliation.controller;


import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.List;

@RestController
@EnableScheduling
@RequestMapping("/reconciliation")
public class ReconciliationController {
    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/date")
    public ResponseEntity<List<SaleTransaction>> reconciliationReport(@RequestParam("date") @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate date) throws SQLException {

        List<SaleTransaction> reconciledSales = reconciliationService.reportReconciliation(date);
        return ResponseEntity.ok().body(reconciledSales);
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void reconciliation() throws SQLException {
        reconciliationService.reconcile();
    }

    @GetMapping("/reconcile-now")
    public ResponseEntity<String> reconcileNow() throws SQLException {
        reconciliationService.reconcile();
        return ResponseEntity.ok().body("Successfully reconciled");
    }



}
