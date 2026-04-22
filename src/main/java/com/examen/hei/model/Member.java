package com.examen.hei.model;

import java.util.List;

public class Member extends MemberInformation {
    private MemberIdentifier id;
    private List<Member> referees;

    public MemberIdentifier getId() { return id; }
    public void setId(MemberIdentifier id) { this.id = id; }
    public List<Member> getReferees() { return referees; }
    public void setReferees(List<Member> referees) { this.referees = referees; }
}