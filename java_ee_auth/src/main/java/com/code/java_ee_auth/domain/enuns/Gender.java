package com.code.java_ee_auth.domain.enuns;

public enum Gender {
    MALE("male"),
    FEMALE("famale");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
}
