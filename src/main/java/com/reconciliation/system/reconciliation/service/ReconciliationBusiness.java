package com.reconciliation.system.reconciliation.service;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReconciliationBusiness implements ReconciliationService {
    private final ReconciliationRepository reconciliationRepository;

    public ReconciliationBusiness(ReconciliationRepository reconciliationRepository) {
        this.reconciliationRepository = reconciliationRepository;
    }

    @Override
    public List<SaleTransaction> reportReconciliation(LocalDate reconciliationDate) throws SQLException {
        return reconciliationRepository.getReconciledSales(reconciliationDate);
    }

    @Override
    public List<Reconciliation> reconcile() throws Exception {
        List<BankTransaction> bankTransactions = reconciliationRepository.getPendingBankTransactions();
        List<SaleTransaction> saleTransactions = reconciliationRepository.getPendingSales();

        List<Reconciliation> reconciliations = new ArrayList<>();
        Map<LocalDate, Boolean> fetchAllDates = new TreeMap<>();

        for (BankTransaction bankTransaction : bankTransactions) {
            fetchAllDates.putIfAbsent(bankTransaction.getPostDate().toLocalDate(), true);
        }

        for (SaleTransaction saleTransaction : saleTransactions) {
            fetchAllDates.putIfAbsent(saleTransaction.getReceivedAt().toLocalDate(), true);
        }

        for (LocalDate date : fetchAllDates.keySet()) {
            Reconciliation reconciliation = new Reconciliation();

            List<BankTransaction> bankTransactionsForDate = bankTransactions
                    .stream()
                    .filter(it -> it.getPostDate().toLocalDate().equals(date))
                    .toList();

            if (bankTransactionsForDate.isEmpty()) {
                saleTransactions
                        .stream()
                        .filter(it -> it.getReceivedAt().toLocalDate().equals(date))
                        .forEach(saleTransaction ->
                            saleTransaction.setReceivedAt(date.plusDays(1).atStartOfDay())
                        );
                continue;
            }

            reconciliation.addAllBankTransaction(bankTransactionsForDate);

            List<SaleTransaction> saleTransactionsForDate = saleTransactions
                    .stream()
                    .filter(it -> it.getReceivedAt().toLocalDate().equals(date))
                    .toList();

            reconciliation.addAllSaleTransaction(saleTransactionsForDate);

            reconciliationRepository.updateReconciliationStatus(saleTransactionsForDate, bankTransactionsForDate, reconciliation.getMatchID());

            reconciliations.add(reconciliation);
            reconciliation.save(reconciliationRepository);
        }

        return reconciliations;
    }
}