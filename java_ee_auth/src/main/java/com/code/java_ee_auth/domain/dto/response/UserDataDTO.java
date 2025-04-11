package com.code.java_ee_auth.domain.dto.response;

import com.code.java_ee_auth.domain.enuns.SexRole;
import com.code.java_ee_auth.domain.enuns.UserRole;

public record UserDataDTO(Long id, String username, UserRole role, SexRole sex) {
}
