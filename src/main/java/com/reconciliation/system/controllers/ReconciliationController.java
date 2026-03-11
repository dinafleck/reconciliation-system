package com.reconciliation.system.controllers;


import com.reconciliation.system.domains.ReconciliationService;
import com.reconciliation.system.domains.models.Reconciliation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationController {
    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/reconcile-now")
    public ResponseEntity<List<Reconciliation>> reconcileNow() throws Exception {

        return ResponseEntity.ok().body(reconciliationService.reconcile());
    }

    @PostMapping("write-off")
    public ResponseEntity<String> writeOff() throws Exception {
        reconciliationService.writeOff();
        return ResponseEntity.ok().body("Write off process finished.");
    }
}
