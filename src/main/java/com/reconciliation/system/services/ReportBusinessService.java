package com.reconciliation.system.services;

import com.reconciliation.system.domains.BankTransactionRepository;
import com.reconciliation.system.domains.ReportRepository;
import com.reconciliation.system.domains.ReportService;
import com.reconciliation.system.domains.SaleTransactionRepository;
import com.reconciliation.system.domains.models.BankTransaction;
import com.reconciliation.system.domains.models.Report;
import com.reconciliation.system.domains.models.SaleTransaction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportBusinessService implements ReportService {
    private final String SEPARATOR = ";";
    private final ReportRepository reportRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final BankTransactionRepository bankTransactionRepository;

    public ReportBusinessService(
            ReportRepository reportRepository,
            SaleTransactionRepository saleTransactionRepository,
            BankTransactionRepository bankTransactionRepository
    ) {
        this.reportRepository = reportRepository;
        this.saleTransactionRepository = saleTransactionRepository;
        this.bankTransactionRepository = bankTransactionRepository;
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
            List<String> lines = new ArrayList<>(br.lines().toList());
            Report report = new Report();

            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }

                line = removeBOM(line);

                String[] lineParts = line.split(SEPARATOR);

                SaleTransaction saleTransaction = new SaleTransaction(
                        lineParts[1].trim(),
                        parseDecimal(lineParts[9]),
                        LocalDateTime.parse(lineParts[0].trim() + " 00:00:00"
                                , DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        lineParts[14].trim(),
                        Integer.parseInt(lineParts[23].trim()),
                        parseDecimal(lineParts[39]),
                        LocalDateTime.parse(lineParts[27].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
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

                line = removeBOM(line);

                String[] lineParts = line.split(SEPARATOR);

                BankTransaction bankTransaction = new BankTransaction(
                        lineParts[9],
                        LocalDateTime.parse(lineParts[0].trim() + " 00:00:00"
                                , DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        parseDecimal(lineParts[6]),
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

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleanValue = value.trim();

        cleanValue = cleanValue.replaceAll("\\s+", "");

        cleanValue = cleanValue.replaceAll("[R$€£¥]", "");

        try {
            if (cleanValue.matches("\\d{1,3}(?:\\.\\d{3})*,\\d{2}")) {
                cleanValue = cleanValue.replaceAll("\\.", "").replace(",", ".");
                return new BigDecimal(cleanValue);
            } else if (cleanValue.matches("\\d{1,3}(?:,\\d{3})*\\.\\d{2}")) {
                // Formato: 1,234,567.89 (americano)
                // Remove vírgulas de milhar
                cleanValue = cleanValue.replaceAll(",", "");
                return new BigDecimal(cleanValue);
            } else if (cleanValue.matches("\\d+,\\d{1,2}")) {
                // Formato: 1234,56
                cleanValue = cleanValue.replace(",", ".");
                return new BigDecimal(cleanValue);
            } else if (cleanValue.matches("\\d+\\.\\d{1,2}")) {
                // Formato: 1234.56
                return new BigDecimal(cleanValue);
            } else if (cleanValue.matches("\\d+")) {
                return new BigDecimal(cleanValue);
            } else if (cleanValue.matches("\\d+[,.]")) {
                cleanValue = cleanValue.replaceAll("[,.]$", "");
                return new BigDecimal(cleanValue);
            } else {

                cleanValue = cleanValue.replaceAll("[^\\d,.]", "");

                if (cleanValue.contains(",") && cleanValue.contains(".")) {
                    int lastComma = cleanValue.lastIndexOf(",");
                    int lastDot = cleanValue.lastIndexOf(".");

                    if (lastComma > lastDot) {
                        cleanValue = cleanValue.replaceAll("\\.", "").replace(",", ".");
                    } else {
                        cleanValue = cleanValue.replaceAll(",", "");
                    }
                } else if (cleanValue.contains(",") && !cleanValue.contains(".")) {
                    cleanValue = cleanValue.replace(",", ".");
                }

                return new BigDecimal(cleanValue);
            }

        } catch (NumberFormatException e) {
            // Log para debug
            System.err.println("Erro ao converter valor: '" + value + "' -> '" + cleanValue + "'");
            throw new RuntimeException("Não foi possível converter o valor '" + value + "' para decimal", e);
        }

    }

    private String removeBOM(String content) {
        return content.replaceFirst("^\uFEFF", "");
    }
}