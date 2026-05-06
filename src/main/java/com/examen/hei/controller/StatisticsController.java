package com.examen.hei.controller;

import com.examen.hei.model.CollectivityLocalStatistics;
import com.examen.hei.model.CollectivityOverallStatistics;
import com.examen.hei.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/collectivites/{id}/statistics")
    public ResponseEntity<List<CollectivityLocalStatistics>> getLocalStatistics(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<CollectivityLocalStatistics> statistics = statisticsService.getLocalStatistics(id, from, to);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/collectivites/statistics")
    public ResponseEntity<List<CollectivityOverallStatistics>> getOverallStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<CollectivityOverallStatistics> statistics = statisticsService.getOverallStatistics(from, to);
        return ResponseEntity.ok(statistics);
    }
}