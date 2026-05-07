package com.examen.hei.controller;

import com.examen.hei.model.*;
import com.examen.hei.service.CollectivityActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityActivityController {

    private final CollectivityActivityService activityService;

    public CollectivityActivityController(CollectivityActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<CollectivityActivity>> getActivities(@PathVariable String id) {
        List<CollectivityActivity> activities = activityService.getActivities(id);
        return ResponseEntity.ok(activities);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<List<CollectivityActivity>> createActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivity> requests) {  // Enlever @Valid temporairement
        List<CollectivityActivity> created = activityService.createActivities(id, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        List<ActivityMemberAttendance> attendance = activityService.getAttendance(id, activityId);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/{id}/activities/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendance>> createAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendance> requests) {  // Enlever @Valid temporairement
        List<ActivityMemberAttendance> created = activityService.createAttendance(id, activityId, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}