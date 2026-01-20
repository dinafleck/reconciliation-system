package com.reconciliation.system.reconciliation.controller;


import com.reconciliation.system.reconciliation.ReconciliationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationController {
    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/date")
    public ResponseEntity<String> reconciliationReport(@RequestParam("date") @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate date) throws SQLException {
        reconciliationService.reportReconciliation(date);

        return ResponseEntity.ok().body("Successfully reconciled");
    }


}
