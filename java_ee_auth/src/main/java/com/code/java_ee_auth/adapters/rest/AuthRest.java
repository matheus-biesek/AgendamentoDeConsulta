package com.code.java_ee_auth.adapters.rest;

import com.code.java_ee_auth.adapters.persistence.RefreshTokenDaoImpl;
import com.code.java_ee_auth.adapters.persistence.UserDAOImpl;
import com.code.java_ee_auth.adapters.rest.exeception.UserAlreadyExistsException;
import com.code.java_ee_auth.adapters.rest.exeception.UserNotFoundException;
import com.code.java_ee_auth.application.service.UserServiceImpl;
import com.code.java_ee_auth.application.service.security.AccessJWTService;
import com.code.java_ee_auth.application.service.security.PasswordService;
import com.code.java_ee_auth.application.service.security.RefreshJWTService;
import com.code.java_ee_auth.domain.dto.request.LoginDTO;
import com.code.java_ee_auth.domain.dto.request.UpdateRoleDTO;
import com.code.java_ee_auth.domain.dto.request.UserInfoDTO;
import com.code.java_ee_auth.domain.dto.request.ChangeDataUserDTO;
import com.code.java_ee_auth.domain.dto.request.CpfDTO;
import com.code.java_ee_auth.domain.dto.response.RoleDTO;
import com.code.java_ee_auth.domain.dto.response.UserDataDTO;
import com.code.java_ee_auth.domain.enuns.UserRole;
import com.code.java_ee_auth.domain.model.RefreshToken;
import com.code.java_ee_auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
 
@Path("/auth-session")
public class AuthRest {

    @Inject
    private UserDAOImpl userRepository;
    @Inject
    private AccessJWTService accessJWTService;
    @Inject
    private RefreshJWTService refreshJWTService;
    @Inject
    private PasswordService passwordService;
    @Inject
    private UserServiceImpl userService;
    @Inject
    private HttpServletRequest request;
    @Inject
    private RefreshTokenDaoImpl refreshTokenDao;
    
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@CookieParam("token") String tokenString) {

        // Em produção, inclua "; Secure" se estiver a usar HTTPS
        String expiredCookieToken = "token=; Path=/; Max-Age=0; HttpOnly; SameSite=Strict";
        String expiredCookieCsrfToken = "csrf=; Path=/; Max-Age=0; SameSite=Strict";
        String expiredCookieRefreshToken = "refreshToken=; Path=/rest-auth; Max-Age=0; HttpOnly; SameSite=Strict";
        

        try {
            Claims calims = accessJWTService.parseToken(tokenString);

            UUID userId = UUID.fromString(calims.getSubject());
            
            List<RefreshToken> refreshTokens = refreshTokenDao.findRefreshTokensByUserId(userId, true);
     
            for (RefreshToken token : refreshTokens) {
                token.setActive(false);
                refreshTokenDao.updateRefreshTokenStatus(token, "REVOKED", request.getRemoteAddr(), request.getHeader("User-Agent"));
            }
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado");

        }

        return Response.ok()
                .header("Set-Cookie", expiredCookieToken)
                .header("Set-Cookie", expiredCookieCsrfToken)
                .header("Set-Cookie", expiredCookieRefreshToken)
                .entity("{\"message\": \"Logout realizado com sucesso\"}")
                .build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO credentials, @HeaderParam("User-Agent") String userAgent) {
        if (userAgent == null || request.getRemoteAddr() == null) {
            System.out.println("Usuário está tentando acessar o sistema sem informar o User-Agent ou o IP do sistema!");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }

        Optional<User> userOpt = userRepository.findByCpf(credentials.getCpf());

        // verificação de autentificação e autorização
        if (userOpt.isEmpty() || !passwordService.verify(credentials.getPassword(), userOpt.get().getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        }
        if (!userOpt.get().isActive()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Usuário não está ativo")
                    .build();
        }
        if (userOpt.get().isBlocked()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Usuário está bloqueado")
                    .build();
        }

        List<RefreshToken> refreshTokens = refreshTokenDao.findRefreshTokensByUserId(userOpt.get().getId(), true);
        
        if (refreshTokens.isEmpty()){
            System.out.println("Nenhum token de atualização ativo encontrado para o usuário");
        }

        if (refreshTokens.size() >= 2) {
            if (refreshTokens.size() > 2){
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Contate o administrador do sistema, pois o usuário possui mais de 2 tokens de atualização ativos no sistema!")
                    .build();
            }

            if (refreshTokens.size() == 2){
                System.out.println("2 rokens ativos");
            }

            // Encontra o token mais antigo
            RefreshToken oldestToken = refreshTokens.stream()
                .min((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .get();
            
            // Marca o token mais antigo como inativo
            oldestToken.setActive(false);
            refreshTokenDao.updateRefreshTokenStatus(oldestToken, "REVOKED", request.getRemoteAddr() , request.getHeader("User-Agent"));
        }

        //Gerar o token de refresh e salvar no banco de dados
        RefreshToken refreshTokenEntity = new RefreshToken(
            UUID.randomUUID(),
            userOpt.get().getId(), 
            request.getRemoteAddr(),  
            userAgent, 
            LocalDateTime.now().plusHours(7));
        String refreshToken = refreshJWTService.generateToken(refreshTokenEntity.getUserId(), refreshTokenEntity.getId());
        refreshTokenDao.saveRefreshToken(refreshTokenEntity);

    
        String csrfToken = UUID.randomUUID().toString();
        String token = accessJWTService.generateToken(userOpt.get().getId(), userOpt.get().getRole().name(), csrfToken);
       
        // Em produção, inclua "; Secure" se estiver a usar HTTPS
        String jwtCookie = String.format(
                "token=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict",
                token,
                60 * 60 * 6
        );

        String csrfCookie = String.format(
                "csrf=%s; Path=/; Max-Age=%d; SameSite=Strict",
                csrfToken,
                60 * 60 * 6
        );

        String refreshTokenCookie = String.format(
                "refreshToken=%s; Path=/rest-auth; Max-Age=%d; HttpOnly; SameSite=Strict",
                refreshToken,
                60 * 60 * 6
        );

        RoleDTO dto = new RoleDTO(userOpt.get().getRole());
        return Response.ok(dto)
                .header("Set-Cookie", jwtCookie)
                .header("Set-Cookie", csrfCookie)
                .header("Set-Cookie", refreshTokenCookie)
                .build();
    }


    // Criar um endpoint para gerar um novo token de refresh, lógica para verificar o token refresh fica aqui.
    // Conferir se é necessario verificar o token csrf, pois o token csrf poderá ser usado como claims do refresh token, ou criar um novo token csrf para o refresh token, porém serão 4 tokens jwt.
    // Verificar se é necessario criar um novo token csrf para o refresh token, pois o token csrf poderá ser usado como claims do refresh token, ou criar um novo token csrf para o refresh token, porém serão 4 tokens jwt.
    // É melhor este endpoint ter sua prapria regra de segurança, conversar depois com a IA para ver ser e melhor deixar a autententificação em algum filtro de requisições, ou por ser apenas está API REST é desnecessário.


    @POST
    @Path("/register-secretary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSecretary(UserInfoDTO newUser) {
        
        Optional<User> userExist = userRepository.findByCpf(newUser.getCpf());
        if (userExist.isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String hashedPassword = passwordService.hash(newUser.getPassword());

        User user = new User(newUser.getName(), newUser.getCpf(), newUser.getEmail(), hashedPassword, UserRole.PATIENT, newUser.getGender());
        userRepository.create(user);

        return Response.status(Response.Status.CREATED).entity("Usuário registrado com sucesso").build();
    }

    @POST
    @Path("/register-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerAdmin(UserInfoDTO newUser) {
        
        Optional<User> userExist = userRepository.findByCpf(newUser.getCpf());
        if (userExist.isPresent()) {
            throw new UserAlreadyExistsException("Usuário já existe");
        }

        String hashedPassword = passwordService.hash(newUser.getPassword());

        User user = new User(newUser.getName(), newUser.getCpf(), newUser.getEmail(), hashedPassword, newUser.getRole(), newUser.getGender());
        userRepository.create(user);

        return Response.status(Response.Status.CREATED).entity("Usuário registrado com sucesso").build();
    }

    @POST
    @Path("/search-user-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public UserDataDTO searchData(@Valid CpfDTO cpfDTO) {
        return userService.getUserData(cpfDTO.getCpf());
    }

    @POST
    @Path("/delete-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (!userExist.get().isActive()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário não está ativo").build();
            }
        }
        return Response.status(Response.Status.OK).entity(userService.deleteUser(cpfDTO.getCpf())).build();
    }

    @POST
    @Path("/activate-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response activateUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (userExist.get().isActive()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário já está ativo").build();
            }
            userExist.get().setActive(true);
            userRepository.updateActive(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário ativado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }

    @POST
    @Path("/update-user-secretary")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserSecretary(@Valid ChangeDataUserDTO changeDataUserDTO) {
        return Response.status(Response.Status.OK).entity(userService.updateUserSecretary(changeDataUserDTO)).build();
    }

    @POST
    @Path("/update-user-admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserAdmin(@Valid UpdateRoleDTO updateRoleDTO) {
        return Response.status(Response.Status.OK).entity(userService.updateUserAdmin(updateRoleDTO)).build();
    }


    @POST
    @Path("/block-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response blockUser(@Valid CpfDTO cpfDTO) {
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (userExist.get().isBlocked()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário já está bloqueado").build();
            }
            userExist.get().setBlocked(true);
            userExist.get().setActive(false);
            userRepository.updateBlocked(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário bloqueado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }

    @POST
    @Path("/unblock-user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unblockUser(@Valid CpfDTO cpfDTO) { 
        Optional<User> userExist = userRepository.findByCpf(cpfDTO.getCpf());
        if (userExist.isPresent()) {
            if (!userExist.get().isBlocked()) {
                return Response.status(Response.Status.CONFLICT).entity("Usuário não está bloqueado").build();
            }
            userExist.get().setBlocked(false);
            userRepository.updateBlocked(userExist.get());
            return Response.status(Response.Status.OK).entity("Usuário desbloqueado com sucesso").build();
        }
        throw new UserNotFoundException("Usuário não encontrado");
    }
}
