package com.reconciliation.system.controllers;


import com.reconciliation.system.domains.ReconciliationService;
import com.reconciliation.system.domains.models.Reconciliation;
import com.reconciliation.system.domains.models.SaleTransaction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationController {
    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/date")
    public ResponseEntity<List<SaleTransaction>> reconciliationReport(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws SQLException {

        List<SaleTransaction> reconciledSales = reconciliationService.reportReconciliation(date);
        return ResponseEntity.ok().body(reconciledSales);
    }

    @GetMapping("/reconcile-now")
    public ResponseEntity<List<Reconciliation>> reconcileNow() throws Exception {

        return ResponseEntity.ok().body(reconciliationService.reconcile());
    }
}
