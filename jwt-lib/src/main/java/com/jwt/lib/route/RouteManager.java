package com.jwt.lib.route;

import java.util.List;

public interface RouteManager {
    boolean isPublicEndpoint(String path);
    
    /**
     * Retorna a lista de roles que têm permissão para acessar o endpoint
     * @param path o caminho do endpoint
     * @return lista de roles permitidas para o endpoint
     */
    List<String> getEndpointRoles(String path);
} 