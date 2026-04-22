package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            // Note: officialNumber et officialName ne sont pas attribués ici
            // Ils le seront via l'endpoint PUT

            db.saveCollectivity(collectivity);
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

    // ========== NOUVELLE MÉTHODE POUR L'ATTRIBUTION D'IDENTIFIANT OFFICIEL ==========

    public Collectivity assignOfficialIdentifier(String collectivityId, OfficialIdentifierRequest request) {
        // 1. Vérifier que la collectivité existe
        Collectivity collectivity = db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        // 2. Vérifier qu'on ne modifie pas un numéro déjà attribué
        if (request.getNumber() != null && db.hasOfficialNumber(collectivityId)) {
            throw new IllegalStateException("Cannot modify official number. Collectivity already has an official number.");
        }

        // 3. Vérifier qu'on ne modifie pas un nom déjà attribué
        if (request.getName() != null && db.hasOfficialName(collectivityId)) {
            throw new IllegalStateException("Cannot modify official name. Collectivity already has an official name.");
        }

        String officialNumber = request.getNumber();
        String officialName = request.getName();

        // 4. Logique d'attribution
        if (officialNumber == null && officialName == null) {
            throw new IllegalArgumentException("At least one of number or name must be provided");
        }

        // Cas 1: Seulement le nom est fourni -> générer le numéro
        if (officialNumber == null && officialName != null) {
            if (db.existsByOfficialName(officialName)) {
                throw new IllegalArgumentException("Official name already exists: " + officialName);
            }
            officialNumber = db.generateNextOfficialNumber();
        }
        // Cas 2: Seulement le numéro est fourni -> générer le nom
        else if (officialNumber != null && officialName == null) {
            if (db.existsByOfficialNumber(officialNumber)) {
                throw new IllegalArgumentException("Official number already exists: " + officialNumber);
            }
            officialName = db.generateUniqueNameFromLocation(collectivityId);
        }
        // Cas 3: Les deux sont fournis -> vérifier les deux
        else {
            if (db.existsByOfficialNumber(officialNumber)) {
                throw new IllegalArgumentException("Official number already exists: " + officialNumber);
            }
            if (db.existsByOfficialName(officialName)) {
                throw new IllegalArgumentException("Official name already exists: " + officialName);
            }
        }

        // 5. Sauvegarder
        db.updateOfficialIdentifiers(collectivityId, officialNumber, officialName);

        // 6. Retourner la collectivité mise à jour
        return db.findCollectivityById(collectivityId).get();
    }
}