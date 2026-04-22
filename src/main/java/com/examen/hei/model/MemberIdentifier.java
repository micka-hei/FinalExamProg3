package com.examen.hei.model;

import com.fasterxml.jackson.annotation.JsonValue;

public class MemberIdentifier {
    private String id;

    public MemberIdentifier() {}

    public MemberIdentifier(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}