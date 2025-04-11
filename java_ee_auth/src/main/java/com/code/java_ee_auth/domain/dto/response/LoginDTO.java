package com.code.java_ee_auth.domain.dto.response;

import com.code.java_ee_auth.domain.enuns.UserRole;

public record LoginDTO(UserRole role) {
}
