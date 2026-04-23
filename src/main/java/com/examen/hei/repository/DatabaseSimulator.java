package com.examen.hei.repository;

import com.examen.hei.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class DatabaseSimulator {
    public Map<String, Collectivity> collectivities = new HashMap<>();
    public Map<String, Member> members = new HashMap<>();
    public Map<String, MembershipFee> membershipFees = new HashMap<>();
    public Map<String, MemberPayment> memberPayments = new HashMap<>();
    public Map<String, CollectivityTransaction> transactions = new HashMap<>();
    public Map<String, FinancialAccount> financialAccounts = new HashMap<>();

    public AtomicInteger collectivityIdGen = new AtomicInteger(1);
    public AtomicInteger memberIdGen = new AtomicInteger(1);
    public AtomicInteger officialNumberSeq = new AtomicInteger(1);
    public AtomicInteger membershipFeeIdGen = new AtomicInteger(1);
    public AtomicInteger paymentIdGen = new AtomicInteger(1);
    public AtomicInteger transactionIdGen = new AtomicInteger(1);
    public AtomicInteger accountIdGen = new AtomicInteger(1);

    // Génération d'IDs
    public String generateCollectivityId() { return "COL-" + collectivityIdGen.getAndIncrement(); }
    public String generateMemberId() { return "MEM-" + memberIdGen.getAndIncrement(); }
    public String generateMembershipFeeId() { return "FEE-" + membershipFeeIdGen.getAndIncrement(); }
    public String generatePaymentId() { return "PAY-" + paymentIdGen.getAndIncrement(); }
    public String generateTransactionId() { return "TXN-" + transactionIdGen.getAndIncrement(); }
    public String generateAccountId() { return "ACC-" + accountIdGen.getAndIncrement(); }

    @PostConstruct
    public void initData() {
        // Créer un unique compte caisse
        CashAccount cashAccount = new CashAccount();
        cashAccount.setId(generateAccountId());
        cashAccount.setAmount(1000000.0);
        financialAccounts.put(cashAccount.getId(), cashAccount);

        System.out.println("=== Compte caisse créé ===");
        System.out.println("  - " + cashAccount.getId() + " : Caisse (solde: " + cashAccount.getAmount() + " MGA)");
    }

    // Méthodes existantes
    public Optional<Member> findMemberById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    public Optional<Collectivity> findCollectivityById(String id) {
        return Optional.ofNullable(collectivities.get(id));
    }

    public Member saveMember(Member member) {
        members.put(member.getId().getId(), member);
        return member;
    }

    public Collectivity saveCollectivity(Collectivity collectivity) {
        collectivities.put(collectivity.getId(), collectivity);
        return collectivity;
    }

    public List<Member> findMembersByCollectivityId(String collectivityId) {
        return members.values().stream().toList();
    }

    // Méthodes pour les identifiants officiels
    public boolean existsByOfficialNumber(String number) {
        if (number == null) return false;
        return collectivities.values().stream()
                .anyMatch(c -> number.equals(c.getOfficialNumber()));
    }

    public boolean existsByOfficialName(String name) {
        if (name == null) return false;
        return collectivities.values().stream()
                .anyMatch(c -> name.equals(c.getOfficialName()));
    }

    public boolean hasOfficialNumber(String collectivityId) {
        Collectivity c = collectivities.get(collectivityId);
        return c != null && c.getOfficialNumber() != null;
    }

    public boolean hasOfficialName(String collectivityId) {
        Collectivity c = collectivities.get(collectivityId);
        return c != null && c.getOfficialName() != null;
    }

    public String generateNextOfficialNumber() {
        int nextSeq = officialNumberSeq.getAndIncrement();
        return String.format("F-2026-%03d", nextSeq);
    }

    public String generateUniqueNameFromLocation(String collectivityId) {
        Collectivity c = collectivities.get(collectivityId);
        if (c == null) return null;
        String baseName = c.getLocation().toUpperCase().replaceAll("[^A-Za-z0-9]", "_");
        String candidate = baseName;
        int counter = 1;
        while (existsByOfficialName(candidate)) {
            candidate = baseName + "_" + counter;
            counter++;
        }
        return candidate;
    }

    public void updateOfficialIdentifiers(String collectivityId, String officialNumber, String officialName) {
        Collectivity c = collectivities.get(collectivityId);
        if (c != null) {
            if (officialNumber != null) c.setOfficialNumber(officialNumber);
            if (officialName != null) c.setOfficialName(officialName);
        }
    }

    // Méthodes pour les cotisations et paiements
    public Optional<MembershipFee> findMembershipFeeById(String id) {
        return Optional.ofNullable(membershipFees.get(id));
    }

    public List<MembershipFee> findMembershipFeesByCollectivityId(String collectivityId) {
        return membershipFees.values().stream().toList();
    }

    public MembershipFee saveMembershipFee(MembershipFee fee) {
        membershipFees.put(fee.getId(), fee);
        return fee;
    }

    public MemberPayment saveMemberPayment(MemberPayment payment) {
        memberPayments.put(payment.getId(), payment);
        return payment;
    }

    public CollectivityTransaction saveTransaction(CollectivityTransaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public Optional<FinancialAccount> findFinancialAccountById(String id) {
        return Optional.ofNullable(financialAccounts.get(id));
    }

    public List<CollectivityTransaction> findTransactionsByCollectivityIdAndDateRange(
            String collectivityId, java.time.LocalDate from, java.time.LocalDate to) {
        return transactions.values().stream()
                .filter(txn -> txn.getCreationDate() != null &&
                        !txn.getCreationDate().isBefore(from) &&
                        !txn.getCreationDate().isAfter(to))
                .toList();
    }
}