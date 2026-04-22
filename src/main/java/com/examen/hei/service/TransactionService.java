package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionService {

    private final DatabaseSimulator db;

    public TransactionService(DatabaseSimulator db) {
        this.db = db;
    }

    public CollectivityTransaction createTransactionFromPayment(
            Member member,
            MemberPayment payment,
            String collectivityId) {

        CollectivityTransaction transaction = new CollectivityTransaction();
        transaction.setId(db.generateTransactionId());
        transaction.setCreationDate(LocalDate.now());
        transaction.setAmount(payment.getAmount().doubleValue());
        transaction.setPaymentMode(payment.getPaymentMode());
        transaction.setAccountCredited(payment.getAccountCredited());
        transaction.setMemberDebited(member);

        return db.saveTransaction(transaction);
    }
}