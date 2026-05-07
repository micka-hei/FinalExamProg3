package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.enums.ActivityStatus;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectivityService {

    private final DatabaseSimulator db;
    private final MemberService memberService;

    public CollectivityService(DatabaseSimulator db, MemberService memberService) {
        this.db = db;
        this.memberService = memberService;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivity> requests) {
        List<Collectivity> created = new ArrayList<>();

        for (CreateCollectivity request : requests) {
            validateCollectivityCreation(request);

            List<Member> members = new ArrayList<>();
            for (MemberIdentifier memberId : request.getMembers()) {
                Member member = db.findMemberById(memberId.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId.getId()));
                members.add(member);
            }

            Member president = db.findMemberById(request.getStructure().getPresident().getId())
                    .orElseThrow(() -> new IllegalArgumentException("President not found"));
            Member vicePresident = db.findMemberById(request.getStructure().getVicePresident().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Vice president not found"));
            Member treasurer = db.findMemberById(request.getStructure().getTreasurer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Treasurer not found"));
            Member secretary = db.findMemberById(request.getStructure().getSecretary().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Secretary not found"));

            CollectivityStructure structure = new CollectivityStructure();
            structure.setPresident(president);
            structure.setVicePresident(vicePresident);
            structure.setTreasurer(treasurer);
            structure.setSecretary(secretary);

            Collectivity collectivity = new Collectivity();
            collectivity.setId(db.generateCollectivityId());
            collectivity.setLocation(request.getLocation());
            collectivity.setMembers(members);
            collectivity.setStructure(structure);

            db.saveCollectivity(collectivity);
            db.createCashAccountForCollectivity(collectivity.getId());
            created.add(collectivity);
        }

        return created;
    }

    private void validateCollectivityCreation(CreateCollectivity request) {
        if (request.getFederationApproval() == null || !request.getFederationApproval()) {
            throw new IllegalArgumentException("Federation approval is required");
        }

        if (request.getMembers() == null || request.getMembers().size() < 10) {
            throw new IllegalArgumentException("At least 10 members are required");
        }

        if (request.getStructure() == null ||
                request.getStructure().getPresident() == null ||
                request.getStructure().getVicePresident() == null ||
                request.getStructure().getTreasurer() == null ||
                request.getStructure().getSecretary() == null) {
            throw new IllegalArgumentException("Complete structure (president, vice-president, treasurer, secretary) is required");
        }
    }

    public Collectivity assignOfficialIdentifier(String collectivityId, OfficialIdentifierRequest request) {
        Collectivity collectivity = db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        if (request.getNumber() != null && db.hasOfficialNumber(collectivityId)) {
            throw new IllegalStateException("Cannot modify official number. Collectivity already has an official number.");
        }

        if (request.getName() != null && db.hasOfficialName(collectivityId)) {
            throw new IllegalStateException("Cannot modify official name. Collectivity already has an official name.");
        }

        String officialNumber = request.getNumber();
        String officialName = request.getName();

        if (officialNumber == null && officialName == null) {
            throw new IllegalArgumentException("At least one of number or name must be provided");
        }

        if (officialNumber == null && officialName != null) {
            if (db.existsByOfficialName(officialName)) {
                throw new IllegalArgumentException("Official name already exists: " + officialName);
            }
            officialNumber = db.generateNextOfficialNumber();
        }
        else if (officialNumber != null && officialName == null) {
            if (db.existsByOfficialNumber(officialNumber)) {
                throw new IllegalArgumentException("Official number already exists: " + officialNumber);
            }
            officialName = db.generateUniqueNameFromLocation(collectivityId);
        }
        else {
            if (db.existsByOfficialNumber(officialNumber)) {
                throw new IllegalArgumentException("Official number already exists: " + officialNumber);
            }
            if (db.existsByOfficialName(officialName)) {
                throw new IllegalArgumentException("Official name already exists: " + officialName);
            }
        }

        db.updateOfficialIdentifiers(collectivityId, officialNumber, officialName);
        return db.findCollectivityById(collectivityId).get();
    }

    public Optional<Collectivity> getCollectivityById(String id) {
        return db.findCollectivityById(id);
    }

    public List<MembershipFee> getMembershipFees(String collectivityId) {
        if (!db.findCollectivityById(collectivityId).isPresent()) {
            throw new IllegalArgumentException("Collectivity not found: " + collectivityId);
        }
        return db.findMembershipFeesByCollectivityId(collectivityId);
    }

    public List<MembershipFee> createMembershipFees(String collectivityId, List<CreateMembershipFee> requests) {
        if (!db.findCollectivityById(collectivityId).isPresent()) {
            throw new IllegalArgumentException("Collectivity not found: " + collectivityId);
        }

        List<MembershipFee> created = new ArrayList<>();

        for (CreateMembershipFee request : requests) {
            if (request.getAmount() == null || request.getAmount() <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0");
            }
            if (request.getFrequency() == null) {
                throw new IllegalArgumentException("Frequency is required");
            }

            MembershipFee fee = new MembershipFee();
            fee.setId(db.generateMembershipFeeId());
            fee.setEligibleFrom(request.getEligibleFrom());
            fee.setFrequency(request.getFrequency());
            fee.setAmount(request.getAmount());
            fee.setLabel(request.getLabel());
            fee.setStatus(ActivityStatus.ACTIVE);

            db.saveMembershipFee(fee);
            created.add(fee);
        }

        return created;
    }

    public List<CollectivityTransaction> getTransactions(String collectivityId, LocalDate from, LocalDate to) {
        if (!db.findCollectivityById(collectivityId).isPresent()) {
            throw new IllegalArgumentException("Collectivity not found: " + collectivityId);
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates are required");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }

        return db.findTransactionsByCollectivityIdAndDateRange(collectivityId, from, to);
    }

    public List<FinancialAccount> getFinancialAccounts(String collectivityId, LocalDate at) {
        if (!db.findCollectivityById(collectivityId).isPresent()) {
            throw new IllegalArgumentException("Collectivity not found: " + collectivityId);
        }
        return db.findFinancialAccountsByCollectivityId(collectivityId);
    }
}