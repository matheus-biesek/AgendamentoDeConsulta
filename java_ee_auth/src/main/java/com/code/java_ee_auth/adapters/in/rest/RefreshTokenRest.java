package com.code.java_ee_auth.adapters.in.rest;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import com.code.java_ee_auth.adapters.in.services.token.AuthRefreshTokenService;
import com.code.java_ee_auth.adapters.utils.IpUtils;
import com.code.java_ee_auth.domain.dto.response.LoginResponseDTO;

@Path("/token")
public class RefreshTokenRest {

    @Inject
    private AuthRefreshTokenService authRefreshTokenService;
    @Inject
    private HttpServletRequest request;

    @POST
    @Path("/refresh")
    public Response refreshToken(@CookieParam("refreshToken") String refreshToken) {
        try {
            if (refreshToken == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Token de refresh não encontrado!")
                .build();
            }

            String requesterIp = IpUtils.getClientIp(request);
            String requesterDevice = request.getHeader("User-Agent");

            LoginResponseDTO loginResponseDTO = authRefreshTokenService.validateRefreshTokenAtEndpoint(refreshToken, requesterDevice, requesterIp);

            String newAccessTokenCookie = loginResponseDTO.getAccessToken();
            String newCsrfTokenCookie = loginResponseDTO.getCsrfToken();
            
            return Response.ok()
                .header("Set-Cookie", newAccessTokenCookie)
                .header("Set-Cookie", newCsrfTokenCookie)
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Erro inesperado ao processar o refresh token!")
            .build();
        }
    }
}
