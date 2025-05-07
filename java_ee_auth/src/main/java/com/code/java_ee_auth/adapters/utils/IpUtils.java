package com.code.java_ee_auth.adapters.utils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {

    /**
     * Obtém o IP real do cliente, priorizando o X-Forwarded-For, com fallback para RemoteAddr.
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }

        return normalizeIp(ip);
    }

    /**
     * Versão para filtros que usam ContainerRequestContext.
     */
    public static String getClientIp(ContainerRequestContext ctx) {
        String ip = ctx.getHeaderString("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = ctx.getHeaderString("Remote-Addr");
        }

        return normalizeIp(ip);
    }

    /**
     * Normaliza IPs no formato IPv6 mapeando IPv4 (::ffff:xxx.xxx.xxx.xxx).
     */
    private static String normalizeIp(String ip) {
        if (ip != null && ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        return ip;
    }
}
