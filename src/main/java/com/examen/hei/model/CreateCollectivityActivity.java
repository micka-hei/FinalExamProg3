package com.examen.hei.model;

import java.time.LocalDate;
import java.util.List;

public class CreateCollectivityActivity {
    private String label;
    private String activityType;
    private List<MemberOccupation> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public List<MemberOccupation> getMemberOccupationConcerned() {
        return memberOccupationConcerned;
    }

    public void setMemberOccupationConcerned(List<MemberOccupation> memberOccupationConcerned) {
        this.memberOccupationConcerned = memberOccupationConcerned;
    }

    public MonthlyRecurrenceRule getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(MonthlyRecurrenceRule recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public LocalDate getExecutiveDate() {
        return executiveDate;
    }

    public void setExecutiveDate(LocalDate executiveDate) {
        this.executiveDate = executiveDate;
    }
}