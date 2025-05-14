package com.code.java_ee_workers.domain.dto.request;

import java.util.UUID;

import com.code.java_ee_workers.domain.enumm.ProfessionalType;
import com.code.java_ee_workers.domain.enumm.Specialization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfessionaDTO {
    private UUID id;
    private UUID userId;
    private Boolean active;
    private String registryNumber;
    private Specialization specialization;
    private ProfessionalType professionalType;
}
