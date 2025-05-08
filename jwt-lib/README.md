# JWT Library

Biblioteca para gerenciamento de autenticação JWT em aplicações Java.

## Requisitos

- Java 11 ou superior
- Maven
- Jakarta EE 8 ou superior

## Como usar

### 1. Implementação do TokenService

Crie uma classe que estenda `AbstractTokenService`:

```java
@ApplicationScoped
public class CustomTokenService extends AbstractTokenService {
    
    @Override
    public String getSecretKey() {
        return "sua-chave-secreta-base64";
    }

    @Override
    public long getTokenExpirationMs() {
        return 3600000; // 1 hora em milissegundos
    }

    @Override
    public String getIssuer() {
        return "seu-issuer";
    }

    @Override
    public String getAudience() {
        return "seu-audience";
    }
}
```

### 2. Implementação do RouteManager

Crie uma classe que estenda `AbstractRouteManager`:

```java
@ApplicationScoped
public class CustomRouteManager extends AbstractRouteManager {
    
    @Override
    protected Set<String> getPublicEndpoints() {
        return Set.of(
            "/auth/login",
            "/auth/register",
            "/health"
        );
    }

    @Override
    protected Map<String, String> getProtectedEndpoints() {
        return Map.of(
            "/admin", "ADMIN",
            "/user", "USER"
        );
    }
}
```

### 3. Implementação do Filtro de Autenticação

Crie uma classe que estenda `JWTAuthFilter`:

```java
@Provider
@Priority(Priorities.AUTHENTICATION)
public class CustomJWTAuthFilter extends JWTAuthFilter {
    
    @Inject
    private CustomTokenService tokenService;
    
    @Inject
    private CustomRouteManager routeManager;

    @Override
    protected TokenService getTokenService() {
        return tokenService;
    }

    @Override
    protected RouteManager getRouteManager() {
        return routeManager;
    }

    @Override
    protected void setupSecurityContext(ContainerRequestContext ctx, Claims claims) {
        String subject = claims.getSubject();
        String role = claims.get("role", String.class);
        SecurityContext securityContext = new CustomSecurityContext(subject, role);
        ctx.setSecurityContext(securityContext);
    }
}
```

O filtro `JWTAuthFilter` é abstrato e requer que você implemente:
- `getTokenService()`: Retorna a instância do seu serviço de token
- `getRouteManager()`: Retorna a instância do seu gerenciador de rotas
- `setupSecurityContext()`: Configura o contexto de segurança (opcional)

## Exemplo de Uso

```java
@Path("/api")
public class YourResource {
    
    @Inject
    private CustomTokenService tokenService;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        // Sua lógica de autenticação
        Map<String, Object> claims = Map.of("role", "USER");
        String token = tokenService.generateToken(request.getUsername(), claims);
        return Response.ok().entity(token).build();
    }
}
```

## Configuração de Segurança

Para configurar o contexto de segurança, você deve implementar o método `setupSecurityContext` na sua classe que estende `JWTAuthFilter`:

```java
@Override
protected void setupSecurityContext(ContainerRequestContext ctx, Claims claims) {
    String subject = claims.getSubject();
    String role = claims.get("role", String.class);
    SecurityContext securityContext = new CustomSecurityContext(subject, role);
    ctx.setSecurityContext(securityContext);
}
```