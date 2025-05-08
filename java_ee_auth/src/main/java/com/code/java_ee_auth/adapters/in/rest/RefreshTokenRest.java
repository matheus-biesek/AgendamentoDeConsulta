package com.code.java_ee_auth.adapters.in.rest;

import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import com.code.java_ee_auth.adapters.in.services.session.AuthRefreshTokenService;
import com.code.java_ee_auth.adapters.utils.IpUtils;
import com.code.java_ee_auth.domain.dto.response.RefreshTokenResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint responsável por gerenciar a renovação de tokens de acesso.
 * Utiliza um refresh token armazenado em cookie para gerar um novo token de acesso.
 */
@Path("/token")
public class RefreshTokenRest {
    
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenRest.class);
    
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String CSRF_TOKEN_COOKIE = "csrfToken";
    private static final String COOKIE_PATH = "/";
    private static final int TOKEN_MAX_AGE = 60 * 60 * 6; // 6 horas
    private static final String USER_AGENT_HEADER = "User-Agent";
    
    @Inject
    private AuthRefreshTokenService authRefreshTokenService;
    @Inject
    private HttpServletRequest request;

    /**
     * Renova o token de acesso usando o refresh token armazenado em cookie.
     * Este endpoint é protegido contra CSRF pois o refresh token só pode ser obtido
     * após um login bem-sucedido e é armazenado em um cookie HttpOnly.
     *
     * @param refreshToken O token de refresh obtido do cookie
     * @return Responde com os novos tokens de acesso e CSRF em cookies
     */
    @POST
    @Path("/refresh")
    public Response refreshToken(@CookieParam(REFRESH_TOKEN_COOKIE) String refreshToken) {
        try {
            if (refreshToken == null) {
                logger.warn("Tentativa de refresh sem token");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            String requesterIp = IpUtils.getClientIp(request);
            String requesterDevice = request.getHeader(USER_AGENT_HEADER);

            if (requesterDevice == null) {
                logger.warn("Tentativa de refresh sem User-Agent");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            logger.info("Iniciando refresh de token para IP: {}", requesterIp);
            
            RefreshTokenResultDTO refreshTokenResult = authRefreshTokenService.autheticationRefreshToken(
                refreshToken, 
                requesterDevice, 
                requesterIp
            );

            if (!refreshTokenResult.success()) {
                logger.warn("Falha na autenticação do refresh token: {}", refreshTokenResult.errorMessage());
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            String newAccessToken = refreshTokenResult.accessToken();
            String newCsrfToken = refreshTokenResult.csrfToken();

            String jwtCookie = String.format(
                "%s=%s; Path=%s; Max-Age=%d; HttpOnly; SameSite=Strict",
                ACCESS_TOKEN_COOKIE,
                newAccessToken,
                COOKIE_PATH,
                TOKEN_MAX_AGE
            );

            String csrfCookie = String.format(
                "%s=%s; Path=%s; Max-Age=%d; SameSite=Strict",
                CSRF_TOKEN_COOKIE,
                newCsrfToken,
                COOKIE_PATH,
                TOKEN_MAX_AGE
            );
            
            logger.info("Refresh de token concluído com sucesso para IP: {}", requesterIp);
            
            return Response.ok()
                .header("Set-Cookie", jwtCookie)
                .header("Set-Cookie", csrfCookie)
                .build();
                
        } catch (Exception e) {
            logger.error("Erro ao processar refresh token: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
