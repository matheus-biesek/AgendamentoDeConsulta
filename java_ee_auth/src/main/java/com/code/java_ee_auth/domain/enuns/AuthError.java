package com.code.java_ee_auth.domain.enuns;

public enum AuthError {
    TOKEN_MISSING("TOKEN_MISSING", "Token de acesso não encontrado."),
    TOKEN_INVALID("TOKEN_INVALID", "Token de acesso inválido."),
    USER_BLOCKED("USER_BLOCKED", "Usuário bloqueado."),
    PERMISSION_DENIED("PERMISSION_DENIED", "Permissão negada."),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token de acesso expirado."),
    USER_NOT_ACTIVE("USER_NOT_ACTIVE", "Usuário não está ativo."),
    CSRF_TOKEN_INVALID("CSRF_TOKEN_INVALID", "Token CSRF inválido."),
    USER_AGENT_MISSING("USER_AGENT_MISSING", "User-Agent não informado."),
    X_FORWARDED_FOR_MISSING("X_FORWARDED_FOR_MISSING", "X-Forwarded-For não informado."),
    USER_NOT_FOUND("USER_NOT_FOUND", "Usuário não encontrado."),
    CREDENTIALS_INVALID("CREDENTIALS_INVALID", "Credenciais inválidas.");

    private final String code;
    private final String message;

    AuthError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }
}

