package com.code.java_ee_auth.adapters.out.rest;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import com.code.java_ee_auth.adapters.in.services.security.AuthRefreshTokenService;
import com.code.java_ee_auth.adapters.utils.IpUtils;
import com.code.java_ee_auth.domain.dto.response.RefreshTokenResultDTO;

@Path("/token")
public class RefreshTokenRest {
    
    @Inject
    private AuthRefreshTokenService authRefreshTokenService;
    @Inject
    private HttpServletRequest request;

    // VERIFICA CSRF TOKEN SÃO EU GARANTO QUE SEJA DE UM USUARIO QUE RECEBEU APOS O LOGIN
    @POST
    @Path("/refresh")
    public Response refreshToken(@CookieParam("token") String accessToken, @CookieParam("refreshToken") String refreshToken) {

        String requesterIp = IpUtils.getClientIp(request);
        String requesterDevice = request.getHeader("User-Agent");

        RefreshTokenResultDTO refreshTokenResult = authRefreshTokenService.autheticationRefreshToken(refreshToken, requesterDevice, requesterIp);

        if (!refreshTokenResult.success()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String newAccessToken = refreshTokenResult.accessToken();
        String newCsrfToken = refreshTokenResult.csrfToken();

        // Em produção, inclua "; Secure" se estiver a usar HTTPS
        String jwtCookie = String.format(
            "token=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax",
            newAccessToken,
            60 * 60 * 6
        );

        String csrfCookie = String.format(
            "csrf=%s; Path=/; Max-Age=%d; SameSite=Lax",
            newCsrfToken,
            60 * 60 * 6
        );
        
        return Response.ok()
            .header("Set-Cookie", jwtCookie)
            .header("Set-Cookie", csrfCookie)
            .build();
    }
}
