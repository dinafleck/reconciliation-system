package com.reconciliation.system.report.service;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.banktransaction.BankTransactionRepository;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.report.Report;
import com.reconciliation.system.report.ReportRepository;
import com.reconciliation.system.report.ReportService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import com.reconciliation.system.saletransaction.SaleTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportBusiness implements ReportService {
    private final String SEPARATOR = ";";
    private final ReportRepository reportRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final ReconciliationRepository reconciliationRepository;

    public ReportBusiness(
            ReportRepository reportRepository,
            SaleTransactionRepository saleTransactionRepository,
            BankTransactionRepository bankTransactionRepository,
            ReconciliationRepository reconciliationRepository,
            ReconciliationService reconciliationService
    ) {
        this.reportRepository = reportRepository;
        this.saleTransactionRepository = saleTransactionRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.reconciliationRepository = reconciliationRepository;
    }

    @Override
    public void saveSaleTransactions(MultipartFile fileParts) throws Exception {

        if (fileParts == null || fileParts.isEmpty()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        fileParts.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {
            List<String> lines = new ArrayList<String>(br.lines().toList());
            Report report = new Report();

            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }

                String[] lineParts = line.split(SEPARATOR);

                SaleTransaction saleTransaction = new SaleTransaction(
                        lineParts[0],
                        new BigDecimal(lineParts[1]),
                        lineParts[2],
                        LocalDateTime.parse(lineParts[3]),
                        lineParts[4],
                        Integer.parseInt(lineParts[5])
                );

                report.addSalesTransaction(saleTransaction);
            }

            reportRepository.save(report);

            for (SaleTransaction saleTransaction : report.getSaleTransactions()) {
                saleTransactionRepository.save(saleTransaction);
            }

        } catch (IOException e) {
            throw new IOException("Failed");

        }

    }

    @Override
    public void saveBankTransactions(MultipartFile fileParts) throws Exception {
        if (fileParts == null || fileParts.isEmpty()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        fileParts.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {

            List<String> lines = new ArrayList<>(br.lines().toList());
            Report report = new Report();

            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }

                String[] lineParts = line.split(SEPARATOR);

                BankTransaction bankTransaction = new BankTransaction(
                        lineParts[0],
                        LocalDateTime.parse(lineParts[1]),
                        new BigDecimal(lineParts[2]),
                        lineParts[3],
                        lineParts[4],
                        lineParts[5]
                );

                report.addBankTransaction(bankTransaction);
            }

            reportRepository.save(report);

            for (BankTransaction bankTransaction : report.getBankTransactions()) {
                bankTransactionRepository.save(bankTransaction);
            }


        } catch (IOException e) {
            throw new IOException("Failed");

        }
    }
}