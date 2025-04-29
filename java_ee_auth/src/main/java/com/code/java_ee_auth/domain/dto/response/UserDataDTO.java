package com.code.java_ee_auth.domain.dto.response;

import com.code.java_ee_auth.domain.enuns.Gender;
import com.code.java_ee_auth.domain.enuns.UserRole;
import java.util.UUID;

public record UserDataDTO(UUID id, boolean blocked, boolean active, String name, String cpf, String email, UserRole role, Gender gender) {
}
