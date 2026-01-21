package com.reconciliation.system.reconciliation.service;

import com.reconciliation.system.banktransaction.BankTransaction;
import com.reconciliation.system.reconciliation.Reconciliation;
import com.reconciliation.system.reconciliation.ReconciliationRepository;
import com.reconciliation.system.reconciliation.ReconciliationService;
import com.reconciliation.system.saletransaction.SaleTransaction;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
    public void reconcile() throws SQLException {
        List<BankTransaction> bankTransactions = reconciliationRepository.getPendingBankTransactions();
        List<SaleTransaction> saleTransactions = reconciliationRepository.getPendingSales();

        Reconciliation reconciliation = new Reconciliation();

        for (BankTransaction bankTransaction : bankTransactions) {
            String bankDirection = bankTransaction.getDirection();
            String bankDescription = bankTransaction.getDescription();

            if (bankDirection.equals("DEBIT") || saleTransactions.isEmpty()) {
                return;
            }

            if (bankDescription.contains("PIX")) {

                for (SaleTransaction saleTransaction : saleTransactions) {
                    if (saleTransaction.getGrossAmount().compareTo(bankTransaction.getAmount()) == 0) {
                        reconciliation.addSaleTransaction(saleTransaction);
                        reconciliation.addBankTransaction(bankTransaction);
                        reconciliationRepository.updateReconciliationStatus(saleTransaction.getSaleId(), bankTransaction.getTransactionId());
                    }
                }

            }
        }
        reconciliationRepository.save(reconciliation);
    }
}