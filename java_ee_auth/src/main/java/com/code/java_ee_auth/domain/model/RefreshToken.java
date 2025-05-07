package com.code.java_ee_auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private UUID id;
    private UUID userId;
    private boolean active;
    private String userIp;
    private String userDevice;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RefreshToken(UUID refreshTokenId, UUID userId, String remoteAddr, String device, 
        LocalDateTime expiryDate) {
        this.id = refreshTokenId;
        this.userId = userId;
        this.userIp = remoteAddr;
        this.userDevice = device;
        this.expiryDate = expiryDate;
    }
}
