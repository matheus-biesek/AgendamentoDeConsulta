package com.code.java_ee_auth.adapters.in.rest;

import com.code.java_ee_auth.adapters.in.services.session.SessionServiceImpl;
import com.code.java_ee_auth.adapters.utils.IpUtils;
import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import com.code.java_ee_auth.domain.dto.response.LoginResponseDTO;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth-session")
public class SessionRest {

    @Inject
    private HttpServletRequest request;
    
    @Inject
    private SessionServiceImpl sessionService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials) {
        
        String requesterDevice = request.getHeader("User-Agent");
        String requesterIp = IpUtils.getClientIp(request);

        try {
            LoginResponseDTO response = sessionService.login(credentials, requesterDevice, requesterIp);
            return Response.ok(response.getRoles())
                .header("Set-Cookie", response.getAccessToken())
                .header("Set-Cookie", response.getCsrfToken())
                .header("Set-Cookie", response.getRefreshToken())
                .build();

        } catch (RuntimeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED)
            .entity(e.getMessage())
            .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro inesperado ao fazer login!")
                .build();
        }
    }
    
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("refreshToken") String tokenString) {
        
        String requesterIp = IpUtils.getClientIp(request);
        String requesterDevice = request.getHeader("User-Agent");

        try {
            LoginResponseDTO response = sessionService.logout(tokenString, requesterDevice, requesterIp);
            return Response.ok(response.getRoles())
                .header("Set-Cookie", response.getAccessToken())
                .header("Set-Cookie", response.getCsrfToken())
                .header("Set-Cookie", response.getRefreshToken())
                .build();
        
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erro inesperado ao fazer logout!")
                .build();
        }
    }
}
