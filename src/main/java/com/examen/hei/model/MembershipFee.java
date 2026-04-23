package com.examen.hei.model;

import com.examen.hei.enums.ActivityStatus;

public class MembershipFee extends CreateMembershipFee {
    private String id;
    private ActivityStatus status;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
}