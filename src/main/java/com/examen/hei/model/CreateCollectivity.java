package com.examen.hei.model;

import java.util.List;

public class CreateCollectivity {
    private String location;
    private List<MemberIdentifier> members;
    private Boolean federationApproval;
    private CreateCollectivityStructure structure;

    // Getters et Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<MemberIdentifier> getMembers() { return members; }
    public void setMembers(List<MemberIdentifier> members) { this.members = members; }
    public Boolean getFederationApproval() { return federationApproval; }
    public void setFederationApproval(Boolean federationApproval) { this.federationApproval = federationApproval; }
    public CreateCollectivityStructure getStructure() { return structure; }
    public void setStructure(CreateCollectivityStructure structure) { this.structure = structure; }
}