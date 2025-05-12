package com.code.java_ee_auth.domain.dto.response;

import com.code.java_ee_auth.domain.enuns.Gender;

import java.util.List;
import java.util.UUID;

public record UserDataDTO(UUID id, boolean blocked, boolean active, String name, String cpf, String email, Gender gender, List<String> roles) {
}
