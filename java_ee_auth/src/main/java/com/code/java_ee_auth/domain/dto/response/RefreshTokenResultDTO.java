package com.code.java_ee_auth.domain.dto.response;

public record RefreshTokenResultDTO(boolean success, String accessToken, String csrfToken, String errorMessage) {
    
}
