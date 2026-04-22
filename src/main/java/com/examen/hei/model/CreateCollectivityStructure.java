package com.examen.hei.model;

public class CreateCollectivityStructure {
    private MemberIdentifier president;
    private MemberIdentifier vicePresident;
    private MemberIdentifier treasurer;
    private MemberIdentifier secretary;

    // Getters et Setters
    public MemberIdentifier getPresident() { return president; }
    public void setPresident(MemberIdentifier president) { this.president = president; }
    public MemberIdentifier getVicePresident() { return vicePresident; }
    public void setVicePresident(MemberIdentifier vicePresident) { this.vicePresident = vicePresident; }
    public MemberIdentifier getTreasurer() { return treasurer; }
    public void setTreasurer(MemberIdentifier treasurer) { this.treasurer = treasurer; }
    public MemberIdentifier getSecretary() { return secretary; }
    public void setSecretary(MemberIdentifier secretary) { this.secretary = secretary; }
}