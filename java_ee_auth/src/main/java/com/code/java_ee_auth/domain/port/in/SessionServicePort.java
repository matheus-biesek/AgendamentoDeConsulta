package com.code.java_ee_auth.domain.port.in;

import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import com.code.java_ee_auth.domain.dto.response.LoginResponseDTO;

public interface SessionServicePort {
    LoginResponseDTO login(LoginDTO credentials, String requesterDevice, String requesterIp);
    LoginResponseDTO logout(String tokenString, String requesterDevice, String requesterIp);
} 