package com.reconciliation.system.reconciliation.service;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<LocalDate, Boolean> fetchAllDates = new HashMap<>();

        for (BankTransaction bankTransaction : bankTransactions) {
            System.out.println("Bank transaction " + bankTransaction.toString());
            fetchAllDates.putIfAbsent(bankTransaction.getPostDate().toLocalDate(), true);
        }

        for (SaleTransaction saleTransaction : saleTransactions) {
            System.out.println("Sale transaction " + saleTransaction.toString());
            fetchAllDates.putIfAbsent(saleTransaction.getDate().toLocalDate(), true);
        }

        for (LocalDate date : fetchAllDates.keySet()) {
            System.out.println("Date " + date);
            Reconciliation reconciliation = new Reconciliation();

            List<BankTransaction> bankTransactionsForDate = bankTransactions
                    .stream()
                    .filter(it -> it.getPostDate().toLocalDate().equals(date))
                    .toList();

            reconciliation.addAllBankTransaction(bankTransactionsForDate);

            System.out.println("Bank transactions " + bankTransactionsForDate.size());

            List<SaleTransaction> saleTransactionsForDate = saleTransactions
                    .stream()
                    .filter(it -> it.getDate().toLocalDate().equals(date))
                    .toList();

            reconciliation.addAllSaleTransaction(saleTransactionsForDate);

            System.out.println("Sale transactions " + saleTransactionsForDate.size());

            reconciliations.add(reconciliation);

            reconciliation.save(reconciliationRepository);
        }

        return reconciliations;
    }
}