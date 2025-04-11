package com.code.java_ee_auth.adapters.rest;

import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.security.jwt.JWTUtils;
import com.code.java_ee_auth.domain.dto.response.LoginDTO;
import com.code.web_java_ee.adapters.security.jwt.PasswordUtils;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.model.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;


@Path("/auth-session")
public class AuthRest {

    @Inject
    private UserDAOImpl userRepository;

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        // Invalida o cookie 'token' definindo Max-Age=0
        String expiredCookie = "token=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax";

        // Em produção, inclua "; Secure" se estiver usando HTTPS
        // expiredCookie += "; Secure";

        // Retorne um JSON contendo uma mensagem de sucesso
        return Response.ok()
                .header("Set-Cookie", expiredCookie)
                .entity("{\"message\": \"Logout realizado com sucesso\"}")  // Retorna um JSON
                .build();
    }
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserInfoDTO credentials) {
        Optional<User> userOpt = userRepository.findByUsername(credentials.getUsername());

        if (userOpt.isEmpty() || !PasswordUtils.verify(credentials.getPassword(), userOpt.get().getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        String token = JWTUtils.generateToken(userOpt.get().getUsername(), userOpt.get().getRole().name());

        // Cria o cabeçalho Set-Cookie manualmente
        String cookieValue = String.format(
                "token=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax",
                token,
                60 * 60
        );

        // Em produção, adicione "; Secure" também:
        // cookieValue += "; Secure";
        LoginDTO dto = new LoginDTO(userOpt.get().getRole());
        return Response.ok(dto)
                .header("Set-Cookie", cookieValue)
                .build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserInfoDTO newUser) {

        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return Response.status(Response.Status.CONFLICT).entity("Usuário já existe").build();
        }
        String hashedPassword = PasswordUtils.hash(newUser.getPassword());

        User user = new User(newUser.getUsername(), hashedPassword, newUser.getRole(), newUser.getSex());
        userRepository.create(user);

        return Response.status(Response.Status.CREATED).entity("Usuário registrado com sucesso").build();
    }
}
