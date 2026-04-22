package com.examen.hei.model;

import java.util.List;

public class CreateMember extends MemberInformation {
    private String collectivityIdentifier;
    private List<MemberIdentifier> referees;
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;

    public String getCollectivityIdentifier() { return collectivityIdentifier; }
    public void setCollectivityIdentifier(String collectivityIdentifier) { this.collectivityIdentifier = collectivityIdentifier; }
    public List<MemberIdentifier> getReferees() { return referees; }
    public void setReferees(List<MemberIdentifier> referees) { this.referees = referees; }
    public Boolean getRegistrationFeePaid() { return registrationFeePaid; }
    public void setRegistrationFeePaid(Boolean registrationFeePaid) { this.registrationFeePaid = registrationFeePaid; }
    public Boolean getMembershipDuesPaid() { return membershipDuesPaid; }
    public void setMembershipDuesPaid(Boolean membershipDuesPaid) { this.membershipDuesPaid = membershipDuesPaid; }
}