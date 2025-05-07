package com.code.java_ee_auth.domain.port.in;

import java.util.UUID;

public interface AuthUserServicePort {
    void revokeAllUserTokens(UUID userId, String requesterIp, String requesterDevice);
}
