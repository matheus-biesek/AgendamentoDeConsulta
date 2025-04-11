package com.code.java_ee_auth.domain.enuns;

public enum SexRole {
    MALE("male"),
    FEMALE("famale");

    private final String sex;

    SexRole(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }
}
