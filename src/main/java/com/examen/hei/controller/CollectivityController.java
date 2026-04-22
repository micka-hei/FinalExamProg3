package com.examen.hei.controller;

import com.examen.hei.model.*;
import com.examen.hei.service.CollectivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService collectivityService;

    public CollectivityController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping
    public ResponseEntity<List<Collectivity>> createCollectivities(
            @Valid @RequestBody List<CreateCollectivity> requests) {
        List<Collectivity> created = collectivityService.createCollectivities(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ========== NOUVEL ENDPOINT ==========
    @PutMapping("/{collectivityId}/official-identifier")
    public ResponseEntity<Collectivity> assignOfficialIdentifier(
            @PathVariable String collectivityId,
            @Valid @RequestBody OfficialIdentifierRequest request) {
        Collectivity updated = collectivityService.assignOfficialIdentifier(collectivityId, request);
        return ResponseEntity.ok(updated);
    }

    // Ajoutez ces endpoints dans CollectivityController.java

    @GetMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> getMembershipFees(@PathVariable String id) {
        List<MembershipFee> fees = collectivityService.getMembershipFees(id);
        return ResponseEntity.ok(fees);
    }

    @PostMapping("/{id}/membershipFees")
    public ResponseEntity<List<MembershipFee>> createMembershipFees(
            @PathVariable String id,
            @Valid @RequestBody List<CreateMembershipFee> requests) {
        List<MembershipFee> created = collectivityService.createMembershipFees(id, requests);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<CollectivityTransaction>> getTransactions(
            @PathVariable String id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        List<CollectivityTransaction> transactions = collectivityService.getTransactions(id, from, to);
        return ResponseEntity.ok(transactions);
    }
}