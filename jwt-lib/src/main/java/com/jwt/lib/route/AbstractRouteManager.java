package com.jwt.lib.route;

import java.util.Set;
import java.util.Map;
import java.util.List;

public abstract class AbstractRouteManager implements RouteManager {
    
    @Override
    public boolean isPublicEndpoint(String path) {
        return getPublicEndpoints().contains(path);
    }

    @Override
    public List<String> getEndpointRoles(String path) {
        // Primeiro verifica se é um endpoint público
        if (isPublicEndpoint(path)) {
            return List.of(); // Endpoints públicos não precisam de roles
        }

        // Obtém as roles do endpoint
        String roles = getProtectedEndpoints().get(path);
        if (roles == null) {
            return List.of(); // Endpoint não encontrado
        }

        // Se a string de roles contiver vírgula, significa que são múltiplas roles
        if (roles.contains(",")) {
            return List.of(roles.split(","));
        }

        // Caso contrário, retorna uma lista com uma única role
        return List.of(roles);
    }

    /**
     * Retorna o conjunto de endpoints públicos
     */
    protected abstract Set<String> getPublicEndpoints();

    /**
     * Retorna o mapa de endpoints protegidos e suas roles
     * A chave é o path do endpoint
     * O valor é uma string com as roles permitidas, separadas por vírgula
     */
    protected abstract Map<String, String> getProtectedEndpoints();
} 