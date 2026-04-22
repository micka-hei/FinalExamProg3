package com.examen.hei.model;

import jakarta.validation.constraints.Pattern;

public class OfficialIdentifierRequest {

    @Pattern(regexp = "^F-\\d{4}-\\d{3}$", message = "Number must follow format: F-YYYY-XXX")
    private String number;

    private String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}