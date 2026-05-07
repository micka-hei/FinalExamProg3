package com.examen.hei.service;

import com.examen.hei.model.*;
import com.examen.hei.repository.DatabaseSimulator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectivityActivityService {

    private final DatabaseSimulator db;

    public CollectivityActivityService(DatabaseSimulator db) {
        this.db = db;
    }

    public List<CollectivityActivity> getActivities(String collectivityId) {
        db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));
        return db.findActivitiesByCollectivityId(collectivityId);
    }

    public List<CollectivityActivity> createActivities(String collectivityId, List<CreateCollectivityActivity> requests) {
        db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        List<CollectivityActivity> created = new ArrayList<>();

        for (CreateCollectivityActivity request : requests) {
            if (request.getRecurrenceRule() != null && request.getExecutiveDate() != null) {
                throw new IllegalArgumentException("Both recurrence rule and executive date cannot be provided at the same time");
            }

            if (request.getRecurrenceRule() == null && request.getExecutiveDate() == null) {
                throw new IllegalArgumentException("Either recurrence rule or executive date must be provided");
            }

            CollectivityActivity activity = new CollectivityActivity();
            activity.setId(db.generateActivityId());
            activity.setCollectivityId(collectivityId);
            activity.setLabel(request.getLabel());
            activity.setActivityType(request.getActivityType());
            activity.setMemberOccupationConcerned(request.getMemberOccupationConcerned());
            activity.setRecurrenceRule(request.getRecurrenceRule());
            activity.setExecutiveDate(request.getExecutiveDate());

            db.saveActivity(activity);
            created.add(activity);
        }

        return created;
    }

    public List<ActivityMemberAttendance> getAttendance(String collectivityId, String activityId) {
        db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        CollectivityActivity activity = db.findActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        if (!activity.getCollectivityId().equals(collectivityId)) {
            throw new IllegalArgumentException("Activity does not belong to this collectivity");
        }

        return db.findAttendanceByActivityId(activityId);
    }

    public List<ActivityMemberAttendance> createAttendance(String collectivityId, String activityId,
                                                           List<CreateActivityMemberAttendance> requests) {
        Collectivity collectivity = db.findCollectivityById(collectivityId)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found: " + collectivityId));

        CollectivityActivity activity = db.findActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + activityId));

        if (!activity.getCollectivityId().equals(collectivityId)) {
            throw new IllegalArgumentException("Activity does not belong to this collectivity");
        }

        List<ActivityMemberAttendance> created = new ArrayList<>();
        List<String> collectivityMemberIds = collectivity.getMembers().stream()
                .map(m -> m.getId().getId())
                .collect(Collectors.toList());

        for (CreateActivityMemberAttendance request : requests) {
            Member member = db.findMemberById(request.getMemberIdentifier())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + request.getMemberIdentifier()));

            ActivityMemberAttendance existing = db.findAttendanceByActivityIdAndMemberId(activityId, request.getMemberIdentifier());

            if (existing != null && !"UNDEFINED".equals(existing.getAttendanceStatus())) {
                throw new IllegalArgumentException("Attendance already confirmed for member: " + request.getMemberIdentifier());
            }

            ActivityMemberAttendance attendance = new ActivityMemberAttendance();
            attendance.setId(db.generateAttendanceId());
            attendance.setActivityId(activityId);
            attendance.setMemberId(member.getId().getId());
            attendance.setAttendanceStatus(request.getAttendanceStatus());

            MemberDescription memberDesc = new MemberDescription();
            memberDesc.setId(member.getId().getId());
            memberDesc.setFirstName(member.getFirstName());
            memberDesc.setLastName(member.getLastName());
            memberDesc.setEmail(member.getEmail());
            memberDesc.setOccupation(member.getOccupation() != null ? member.getOccupation().name() : null);
            attendance.setMemberDescription(memberDesc);

            db.saveAttendance(attendance);
            created.add(attendance);
        }

        return created;
    }
}