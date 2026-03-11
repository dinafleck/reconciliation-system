package com.reconciliation.system.controllers;


import com.reconciliation.system.infrastructure.clients.ExternalHttpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/contas/receber")
public class SaleTransactionController {
    private final ExternalHttpClient externalHttpClient;

    public SaleTransactionController(ExternalHttpClient externalHttpClient) {
        this.externalHttpClient = externalHttpClient;
    }

    @GetMapping()
    public ResponseEntity<String> getSales() throws IOException, InterruptedException {

        var getResponse = externalHttpClient.get("api/contas/receber");
        return ResponseEntity.ok().body(getResponse.body());

    }

}
