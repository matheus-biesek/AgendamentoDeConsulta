package com.code.java_ee_schedule.domain.enumm;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    private String value;

    DayOfWeek(String value) {
        this.value = value;
    }

}
