package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;
import com.examen.hei.enums.ActivityStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final DatabaseSimulator db;

    public MemberService(DatabaseSimulator db) {
        this.db = db;
    }

    public List<Member> createMembers(List<CreateMember> requests) {
        List<Member> created = new ArrayList<>();

        System.out.println("=== Création de " + requests.size() + " membre(s) ===");
        System.out.println("Nombre de membres existants dans la base: " + db.findAllMembers().size());

        for (CreateMember request : requests) {
            System.out.println("Traitement du membre: " + request.getFirstName() + " " + request.getLastName());
            System.out.println("  - referees: " + (request.getReferees() != null ? request.getReferees().size() : "null"));

            validateMemberCreation(request);

            List<Member> referees = new ArrayList<>();
            if (request.getReferees() != null && !request.getReferees().isEmpty()) {
                for (MemberIdentifier refereeId : request.getReferees()) {
                    Member referee = db.findMemberById(refereeId.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Referee not found: " + refereeId.getId()));
                    referees.add(referee);
                }
            }

            Member member = new Member();
            member.setId(new MemberIdentifier(db.generateMemberId()));
            member.setFirstName(request.getFirstName());
            member.setLastName(request.getLastName());
            member.setBirthDate(request.getBirthDate());
            member.setGender(request.getGender());
            member.setAddress(request.getAddress());
            member.setProfession(request.getProfession());
            member.setPhoneNumber(request.getPhoneNumber());
            member.setEmail(request.getEmail());
            member.setOccupation(request.getOccupation());
            member.setReferees(referees);

            db.saveMember(member);
            created.add(member);
            System.out.println("  -> Créé avec ID: " + member.getId().getId());
        }

        System.out.println("=== Fin création, total membres: " + db.findAllMembers().size() + " ===");
        return created;
    }

    private void validateMemberCreation(CreateMember request) {
        if (request.getRegistrationFeePaid() == null || !request.getRegistrationFeePaid()) {
            throw new IllegalArgumentException("Registration fee (50,000 MGA) must be paid");
        }

        if (request.getMembershipDuesPaid() == null || !request.getMembershipDuesPaid()) {
            throw new IllegalArgumentException("Annual membership dues must be paid in full");
        }

        int existingMembersCount = db.findAllMembers().size();
        System.out.println("  - Membres existants avant validation: " + existingMembersCount);

        boolean isFirstOrSecondMember = existingMembersCount < 2;

        if (!isFirstOrSecondMember) {
            if (request.getReferees() == null || request.getReferees().size() < 2) {
                throw new IllegalArgumentException("At least 2 referees are required. Current referees: " +
                        (request.getReferees() != null ? request.getReferees().size() : 0));
            }
            System.out.println("  - Vérification parrains OK: " + request.getReferees().size() + " parrain(s)");
        } else {
            System.out.println("  - Premier/Deuxième membre: pas de vérification des parrains");
        }

        if (db.findAllMembers().stream().anyMatch(m -> m.getEmail().equals(request.getEmail()))) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (db.findAllMembers().stream().anyMatch(m -> m.getPhoneNumber().equals(request.getPhoneNumber()))) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
        }
    }

    public List<MemberPayment> createPayments(String memberId, List<CreateMemberPayment> requests) {
        List<MemberPayment> created = new ArrayList<>();

        Member member = db.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        String collectivityId = findCollectivityIdByMember(member);

        for (CreateMemberPayment request : requests) {
            MembershipFee fee = db.findMembershipFeeById(request.getMembershipFeeIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Membership fee not found: " + request.getMembershipFeeIdentifier()));

            if (fee.getStatus() != ActivityStatus.ACTIVE) {
                throw new IllegalArgumentException("Membership fee is not active");
            }

            FinancialAccount account = db.findFinancialAccountById(request.getAccountCreditedIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Financial account not found: " + request.getAccountCreditedIdentifier()));

            MemberPayment payment = new MemberPayment();
            payment.setId(db.generatePaymentId());
            payment.setAmount(request.getAmount());
            payment.setPaymentMode(request.getPaymentMode());
            payment.setAccountCredited(account);
            payment.setCreationDate(LocalDate.now());

            db.saveMemberPayment(payment);
            created.add(payment);

            CollectivityTransaction transaction = new CollectivityTransaction();
            transaction.setId(db.generateTransactionId());
            transaction.setCreationDate(LocalDate.now());
            transaction.setAmount(request.getAmount().doubleValue());
            transaction.setPaymentMode(request.getPaymentMode());
            transaction.setAccountCredited(account);
            transaction.setMemberDebited(member);

            db.saveTransaction(transaction);
        }

        return created;
    }

    private String findCollectivityIdByMember(Member member) {
        for (Collectivity c : db.findAllCollectivities()) {
            if (c.getMembers() != null && c.getMembers().stream().anyMatch(m -> m.getId().getId().equals(member.getId().getId()))) {
                return c.getId();
            }
        }
        throw new IllegalArgumentException("Member not associated with any collectivity");
    }
}