package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

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
        System.out.println("Nombre de membres existants dans la base: " + db.members.size());

        for (CreateMember request : requests) {
            System.out.println("Traitement du membre: " + request.getFirstName() + " " + request.getLastName());
            System.out.println("  - referees: " + (request.getReferees() != null ? request.getReferees().size() : "null"));

            validateMemberCreation(request);

            // Récupérer les parrains
            List<Member> referees = new ArrayList<>();
            if (request.getReferees() != null && !request.getReferees().isEmpty()) {
                for (MemberIdentifier refereeId : request.getReferees()) {
                    Member referee = db.findMemberById(refereeId.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Referee not found: " + refereeId.getId()));
                    referees.add(referee);
                }
            }

            // Créer le membre
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

        System.out.println("=== Fin création, total membres: " + db.members.size() + " ===");
        return created;
    }

    private void validateMemberCreation(CreateMember request) {
        // Vérifier les paiements
        if (request.getRegistrationFeePaid() == null || !request.getRegistrationFeePaid()) {
            throw new IllegalArgumentException("Registration fee (50,000 MGA) must be paid");
        }

        if (request.getMembershipDuesPaid() == null || !request.getMembershipDuesPaid()) {
            throw new IllegalArgumentException("Annual membership dues must be paid in full");
        }

        // Vérification des parrains - AUTORISE LES PREMIERS MEMBRES SANS PARRAINS
        int existingMembersCount = db.members.size();
        System.out.println("  - Membres existants avant validation: " + existingMembersCount);

        // Si c'est le 1er ou 2ème membre, pas besoin de parrains
        boolean isFirstOrSecondMember = existingMembersCount < 2;

        if (!isFirstOrSecondMember) {
            // À partir du 3ème membre, au moins 2 parrains sont requis
            if (request.getReferees() == null || request.getReferees().size() < 2) {
                throw new IllegalArgumentException("At least 2 referees are required. Current referees: " +
                        (request.getReferees() != null ? request.getReferees().size() : 0));
            }
            System.out.println("  - Vérification parrains OK: " + request.getReferees().size() + " parrain(s)");
        } else {
            System.out.println("  - Premier/Deuxième membre: pas de vérification des parrains");
        }

        // Vérifier email unique
        if (db.members.values().stream().anyMatch(m -> m.getEmail().equals(request.getEmail()))) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Vérifier téléphone unique
        if (db.members.values().stream().anyMatch(m -> m.getPhoneNumber().equals(request.getPhoneNumber()))) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
        }
    }
}