package com.code.java_ee_workers.domain.enumm;

import lombok.Getter;

@Getter
public enum Specialization {
    CARDIOLOGY("cardiology"),
    DERMATOLOGY("dermatology"),
    NEUROLOGY("neurolo"),
    ORTHOPEDICS("orthopedics"),
    PEDIATRICS("pediatrics"),
    PSYCHIATRY("psychology"),
    GYNECOLOGY("gynecology"),
    RADIOLOGY("radiology"),
    ONCOLOGY("oncology"),
    UROLOGY("urology"),
    GASTROENTEROLOGY("gastroenterology"),
    OPHTHALMOLOGY("ophthalmology"),
    GENERAL_SURGERY("general_surgery");

    private final String displayName;

    Specialization(String displayName) {
        this.displayName = displayName;
    }
}
