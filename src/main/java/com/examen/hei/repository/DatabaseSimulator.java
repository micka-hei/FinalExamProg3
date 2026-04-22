package com.examen.hei.repository;

import com.examen.hei.model.Collectivity;
import com.examen.hei.model.Member;
import com.examen.hei.model.MemberIdentifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class DatabaseSimulator {
    public Map<String, Collectivity> collectivities = new HashMap<>();
    public Map<String, Member> members = new HashMap<>();

    public AtomicInteger collectivityIdGen = new AtomicInteger(1);
    public AtomicInteger memberIdGen = new AtomicInteger(1);

    // NOUVEAU : Suivi des séquences pour les numéros officiels
    public AtomicInteger officialNumberSeq = new AtomicInteger(1);

    public String generateCollectivityId() {
        return "COL-" + collectivityIdGen.getAndIncrement();
    }

    public String generateMemberId() {
        return "MEM-" + memberIdGen.getAndIncrement();
    }

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

    // ========== NOUVELLES MÉTHODES POUR LES IDENTIFIANTS OFFICIELS ==========

    // Vérifier si un numéro officiel existe déjà
    public boolean existsByOfficialNumber(String number) {
        if (number == null) return false;
        return collectivities.values().stream()
                .anyMatch(c -> number.equals(c.getOfficialNumber()));
    }

    // Vérifier si un nom officiel existe déjà
    public boolean existsByOfficialName(String name) {
        if (name == null) return false;
        return collectivities.values().stream()
                .anyMatch(c -> name.equals(c.getOfficialName()));
    }

    // Vérifier si la collectivité a déjà un numéro officiel
    public boolean hasOfficialNumber(String collectivityId) {
        Collectivity c = collectivities.get(collectivityId);
        return c != null && c.getOfficialNumber() != null;
    }

    // Vérifier si la collectivité a déjà un nom officiel
    public boolean hasOfficialName(String collectivityId) {
        Collectivity c = collectivities.get(collectivityId);
        return c != null && c.getOfficialName() != null;
    }

    // Générer le prochain numéro officiel (format: F-2026-001, F-2026-002, ...)
    public String generateNextOfficialNumber() {
        int nextSeq = officialNumberSeq.getAndIncrement();
        return String.format("F-2026-%03d", nextSeq);
    }

    // Générer un nom unique basé sur la localisation
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

    // Mettre à jour les identifiants officiels d'une collectivité
    public void updateOfficialIdentifiers(String collectivityId, String officialNumber, String officialName) {
        Collectivity c = collectivities.get(collectivityId);
        if (c != null) {
            if (officialNumber != null) c.setOfficialNumber(officialNumber);
            if (officialName != null) c.setOfficialName(officialName);
        }
    }
}