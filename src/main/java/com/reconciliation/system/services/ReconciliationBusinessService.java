package com.reconciliation.system.services;

import com.reconciliation.system.domains.ReconciliationRepository;
import com.reconciliation.system.domains.ReconciliationService;
import com.reconciliation.system.domains.models.BankTransaction;
import com.reconciliation.system.domains.models.Reconciliation;
import com.reconciliation.system.domains.models.SaleTransaction;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ReconciliationBusinessService implements ReconciliationService {
    private final ReconciliationRepository reconciliationRepository;

    public ReconciliationBusinessService(ReconciliationRepository reconciliationRepository) {
        this.reconciliationRepository = reconciliationRepository;
    }

    private static void swapSaleTransactionForNextDay(LocalDate date, List<SaleTransaction> saleTransactions) {
        saleTransactions
                .stream()
                .filter(it -> it.getReceivedAt().toLocalDate().equals(date))
                .forEach(saleTransaction ->
                        {
                            LocalDateTime nextDay = date.plusDays(1).atStartOfDay();

                            saleTransaction.setReceivedAt(nextDay);
                        }
                );
    }

    private static Map<LocalDate, Boolean> fetchAllDatesFromTransactions(
            List<BankTransaction> bankTransactions,
            List<SaleTransaction> saleTransactions
    ) {
        Map<LocalDate, Boolean> fetchAllDates = new TreeMap<>();

        for (BankTransaction bankTransaction : bankTransactions) {
            fetchAllDates.putIfAbsent(bankTransaction.getPostDate().toLocalDate(), true);
        }

        for (SaleTransaction saleTransaction : saleTransactions) {
            fetchAllDates.putIfAbsent(saleTransaction.getReceivedAt().toLocalDate(), true);
        }
        return fetchAllDates;
    }

    private static List<BankTransaction> retrieveBankTransactionsByDate(
            LocalDate date,
            List<BankTransaction> bankTransactions
    ) {
        return bankTransactions
                .stream()
                .filter(it -> it.getPostDate().toLocalDate().equals(date))
                .toList();
    }

    @Override
    public List<SaleTransaction> reportReconciliation(LocalDate reconciliationDate) throws SQLException {
        return reconciliationRepository.getReconciledSales(reconciliationDate);
    }

    @Override
    public List<Reconciliation> reconcile() throws Exception {
        List<BankTransaction> bankTransactions = reconciliationRepository.getPendingBankTransactions();
        List<SaleTransaction> saleTransactions = reconciliationRepository.getPendingSales();

        Map<LocalDate, Boolean> fetchAllDates = fetchAllDatesFromTransactions(bankTransactions, saleTransactions);

        List<Reconciliation> reconciliations = new ArrayList<>();

        for (LocalDate date : fetchAllDates.keySet()) {
            Reconciliation reconciliation = new Reconciliation();

            List<BankTransaction> bankTransactionsForDate = retrieveBankTransactionsByDate(date, bankTransactions);

            boolean hasDayBeenHoliday = bankTransactionsForDate.isEmpty();

            if (hasDayBeenHoliday) {
                swapSaleTransactionForNextDay(date, saleTransactions);
                continue;
            }

            reconciliation.addAllBankTransaction(bankTransactionsForDate);
            reconciliation.setBankTransactionDate(date);

            List<SaleTransaction> saleTransactionsForDate = retrieveSaleTransactionByDate(date, saleTransactions);

            reconciliation.addAllSaleTransaction(saleTransactionsForDate);

            reconciliations.add(reconciliation);
            reconciliation.save(reconciliationRepository);

            reconciliationRepository.updateReconciliationStatus(saleTransactionsForDate, bankTransactionsForDate, reconciliation.getMatchID());
        }

        generateFile(reconciliations);
        return reconciliations;
    }

    private static List<SaleTransaction> retrieveSaleTransactionByDate(LocalDate date, List<SaleTransaction> saleTransactions) {
        return saleTransactions
                .stream()
                .filter(it -> it.getReceivedAt().toLocalDate().equals(date))
                .toList();
    }

    private void generateFile(List<Reconciliation> reconciliations) {
        String csvFile = "reconciliation_report.csv";


        try (java.io.FileWriter fileWriter = new java.io.FileWriter(csvFile)) {
            String header = "MatchID," +
                    "Total Recebido," +
                    "Total Vendas," +
                    "Diferenca conciliacao," +
                    "Data Recebimento," +
                    "SaleID," +
                    "Data Venda," +
                    "Nome Cliente," +
                    "Metodo Pagamento," +
                    "Valor Total," +
                    "Valor Liquido\n";

            fileWriter.append(header);

            for (Reconciliation reconciliation : reconciliations) {
                for (BankTransaction bankTransaction : reconciliation.getBankTransactions()) {
                    for (SaleTransaction saleTransaction : reconciliation.getSaleTransactions()){
                        fileWriter.append(reconciliation.getMatchID().toString()).append(",");
                        fileWriter.append(reconciliation.getTotalBankTransactionAmount().toString()).append(",");
                        fileWriter.append(reconciliation.getTotalSaleTransactionAmount().toString()).append(",");
                        fileWriter.append(reconciliation.getTotalReconciliationAmount().toString()).append(",");
                        fileWriter.append(bankTransaction.getPostDate().toString()).append(",");
                        fileWriter.append(saleTransaction.getSaleId()).append(",");
                        fileWriter.append(saleTransaction.getReceivedAt().toString()).append(",");
                        fileWriter.append(saleTransaction.getClientName()).append(",");
                        fileWriter.append(saleTransaction.getPaymentMethod()).append(",");
                        fileWriter.append(saleTransaction.getGrossAmount().toString()).append(",");
                        fileWriter.append(saleTransaction.getNetAmount().toString()).append("\n");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("file updated");

    }


}