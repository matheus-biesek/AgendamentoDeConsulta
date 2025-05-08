package com.code.java_ee_auth.adapters.in.rest;

import com.code.java_ee_auth.adapters.in.services.session.SessionServiceImpl;
import com.code.java_ee_auth.adapters.utils.IpUtils;
import com.code.java_ee_auth.domain.dto.request.LoginDTO;
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
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("accessToken") String tokenString) {
        String requesterIp = IpUtils.getClientIp(request);
        String requesterDevice = request.getHeader("User-Agent");

        return sessionService.logout(tokenString, requesterDevice, requesterIp);
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials) {
        String requesterDevice = request.getHeader("User-Agent");
        String requesterIp = IpUtils.getClientIp(request);

        return sessionService.login(credentials, requesterDevice, requesterIp);
    }
}
