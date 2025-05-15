-- ========================
-- 🔐 Auth Service (Schema)
-- ========================

-- criar trigle que aceiona função no momento em que uma role e adicionada ao usuario, este triggle basicamente so confere se a role ja nao existe para o usuario, caso exista, nao adiciona
CREATE OR REPLACE FUNCTION auth_service.check_role_exists()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM auth_service.user_roles WHERE user_id = NEW.user_id AND role_name = NEW.role_name) THEN
        RAISE EXCEPTION 'A função já existe para este usuário!';


CREATE SCHEMA auth_service;

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- Necessário para usar gen_random_uuid()

CREATE TABLE auth_service.users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT true,
    blocked BOOLEAN NOT NULL DEFAULT false,
    name VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    password VARCHAR(255) NOT NULL
);

CREATE TABLE auth_service.roles (
    role_name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE auth_service.user_roles (
    user_id UUID NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES auth_service.users(user_id),
    FOREIGN KEY (role_name) REFERENCES auth_service.roles(role_name)
);

CREATE TABLE auth_service.user_audit (
    user_audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('CREATED', 'DELETE', 'UPDATE')),
    changed_by UUID NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES auth_service.users(user_id),
    CONSTRAINT fk_changed_by FOREIGN KEY (changed_by) REFERENCES auth_service.users(user_id)
);

CREATE TABLE auth_service.refresh_tokens (
    refresh_token_id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    user_ip_address VARCHAR(45) NOT NULL,
    user_device_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES auth_service.users(user_id)
);

CREATE TABLE auth_service.refresh_token_audit (
    refresh_token_audit_id BIGSERIAL PRIMARY KEY,
    refresh_token_id UUID NOT NULL,
    requester_ip_address VARCHAR(45) NOT NULL,
    requester_device_name VARCHAR(255) NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('CREATED', 'REFRESH', 'REVOKED', 'EXPIRED', 'TOKEN_BINDING_MISMATCH', 'INACTIVE')),
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token FOREIGN KEY (refresh_token_id) REFERENCES auth_service.refresh_tokens(refresh_token_id)
);

-- INDEXES
-- Indice para consulta sql ao buscar refresh tokens ativos de um determinado usuario.
CREATE INDEX idx_refresh_tokens_user_active 
ON auth_service.refresh_tokens (user_id, active);

-- indice para consulta sql para validação se o user_id condiz com o refresh_token_id
CREATE INDEX idx_refresh_tokens_refresh_token_id_user_id
ON auth_service.refresh_tokens (refresh_token_id, user_id);

-- 1. Função PL/pgSQL para validar o CPF
CREATE OR REPLACE FUNCTION auth_service.validar_cpf(cpf_input TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    clean_cpf TEXT;
    i INTEGER;
    soma INTEGER;
    resto INTEGER;
    digito1 INTEGER;
    digito2 INTEGER;
BEGIN
    -- Remove pontuação (pode ser ajustado conforme sua entrada)
    clean_cpf := regexp_replace(cpf_input, '[^0-9]', '', 'g');

    -- Verifica se tem 11 dígitos
    IF length(clean_cpf) != 11 THEN
        RETURN FALSE;
    END IF;

    -- Verifica CPFs inválidos conhecidos (todos iguais)
    IF clean_cpf ~ '^(.)\1{10}$' THEN
        RETURN FALSE;
    END IF;

    -- Calcula o primeiro dígito verificador
    soma := 0;
    FOR i IN 1..9 LOOP
        soma := soma + (CAST(substr(clean_cpf, i, 1) AS INTEGER) * (11 - i));
    END LOOP;
    resto := (soma * 10) % 11;
    IF resto = 10 OR resto = 11 THEN
        resto := 0;
    END IF;
    digito1 := resto;

    -- Verifica primeiro dígito
    IF digito1 != CAST(substr(clean_cpf, 10, 1) AS INTEGER) THEN
        RETURN FALSE;
    END IF;

    -- Calcula o segundo dígito verificador
    soma := 0;
    FOR i IN 1..10 LOOP
        soma := soma + (CAST(substr(clean_cpf, i, 1) AS INTEGER) * (12 - i));
    END LOOP;
    resto := (soma * 10) % 11;
    IF resto = 10 OR resto = 11 THEN
        resto := 0;
    END IF;
    digito2 := resto;

    -- Verifica segundo dígito
    IF digito2 != CAST(substr(clean_cpf, 11, 1) AS INTEGER) THEN
        RETURN FALSE;
    END IF;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 2. Criar a função de trigger
CREATE OR REPLACE FUNCTION auth_service.trigger_validar_cpf()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT auth_service.validar_cpf(NEW.cpf) THEN
        RAISE EXCEPTION 'CPF inválido: %', NEW.cpf;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criar o trigger na tabela users
CREATE TRIGGER before_insert_validar_cpf
BEFORE INSERT ON auth_service.users
FOR EACH ROW
EXECUTE FUNCTION auth_service.trigger_validar_cpf();
