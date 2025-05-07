package com.code.java_ee_auth.domain.port.in;

import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import jakarta.ws.rs.core.Response;

public interface SessionServicePort {
    Response login(LoginDTO credentials, String requesterDevice, String requesterIp);
    Response logout(String tokenString, String requesterDevice, String requesterIp);
} 