package com.code.java_ee_workers.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Professional {
    private UUID professional_id;
    private UUID user_id;
    private Boolean active;
    private String professional_type;
    private String registry_number;
    private String specialization;
}
