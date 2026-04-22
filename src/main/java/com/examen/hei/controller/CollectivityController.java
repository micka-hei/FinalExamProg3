package com.examen.hei.controller;

import com.examen.hei.model.Collectivity;
import com.examen.hei.model.CreateCollectivity;
import com.examen.hei.model.OfficialIdentifierRequest;  // NOUVEAU
import com.examen.hei.service.CollectivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}