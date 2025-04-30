-- ========================
-- 🔐 Auth Service (Schema)
-- ========================
CREATE SCHEMA auth_service;

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- Necessário para usar gen_random_uuid()

CREATE TABLE auth_service.users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT true,
    name VARCHAR(100) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    blocked BOOLEAN NOT NULL DEFAULT false,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'SECRETARY', 'DOCTOR', 'PATIENT', 'TECHNICIAN', 'NURSE')),
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

CREATE TABLE auth_service.user_audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('CREATED', 'DELETE', 'UPDATE')),
    changed_by UUID NOT NULL,
    change_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES auth_service.users(user_id),
    CONSTRAINT fk_changed_by FOREIGN KEY (changed_by) REFERENCES auth_service.users(user_id)
);

CREATE TABLE auth_service.refresh_tokens (
    refresh_token_id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL,
    user_ip_address VARCHAR(45) NOT NULL,
    user_device_name VARCHAR(255),
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES auth_service.users(user_id)
);

CREATE TABLE auth_service.refresh_token_audit (
    id BIGSERIAL PRIMARY KEY,
    refresh_token_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('CREATED', 'REFRESH', 'REVOKED', 'EXPIRED')),
    requester_ip_address VARCHAR(45) NOT NULL,
    requester_device_name VARCHAR(255) NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token FOREIGN KEY (refresh_token_id) REFERENCES auth_service.refresh_tokens(refresh_token_id)
);

-- INDEXES
-- Indice para consulta sql ao buscar refresh tokens ativos de um determinado usuario.
CREATE INDEX idx_refresh_tokens_user_status 
ON auth_service.refresh_tokens (user_id, status);

-- indice para consulta sql para validação se o user_id condiz com o refresh_token_id
CREATE INDEX idx_refresh_tokens_refresh_token_id_user_id
ON auth_service.refresh_tokens (refresh_token_id, user_id);

-- Função para criar a entidade refresh_token e seu audit:
CREATE OR REPLACE FUNCTION auth_service.create_refresh_token(
    p_refresh_token_id UUID,
    p_user_id UUID,
    p_user_ip_address VARCHAR(45),
    p_device_name VARCHAR(255),
    p_expiry_date TIMESTAMP
)
RETURNS BOOLEAN AS $$
DECLARE
    v_error_message TEXT;
BEGIN
    RAISE NOTICE 'Iniciando criação do refresh token';
    RAISE NOTICE 'Parâmetros recebidos:';
    RAISE NOTICE 'refresh_token_id: %', p_refresh_token_id;
    RAISE NOTICE 'user_id: %', p_user_id;
    RAISE NOTICE 'user_ip_address: %', p_user_ip_address;
    RAISE NOTICE 'device_name: %', p_device_name;
    RAISE NOTICE 'expiry_date: %', p_expiry_date;

    -- Insere o refresh_token
    BEGIN
        INSERT INTO auth_service.refresh_tokens (
            refresh_token_id,
            user_id,
            user_ip_address,
            user_device_name,  -- Aqui está a correção
            expiry_date,
            active,
            created_at,
            updated_at
        ) VALUES (
            p_refresh_token_id,
            p_user_id,
            p_user_ip_address,
            p_device_name,
            p_expiry_date,
            true,
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
        );
        RAISE NOTICE 'Refresh token inserido com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao inserir refresh token: %', v_error_message;
        RAISE EXCEPTION 'Erro na inserção do refresh token';
    END;

    -- Insere o log no audit
    BEGIN
        INSERT INTO auth_service.refresh_token_audit (
            refresh_token_id,
            action_type,
            requester_ip_address,
            requester_device_name,
            event_timestamp
        ) VALUES (
            p_refresh_token_id,
            'CREATED',
            p_user_ip_address,
            p_device_name,
            CURRENT_TIMESTAMP
        );
        RAISE NOTICE 'Log de audit inserido com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao inserir log de audit: %', v_error_message;
        RAISE EXCEPTION 'Erro na inserção do log de audit';
    END;

    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro geral: %', v_error_message;
        RAISE NOTICE 'SQLSTATE: %', SQLSTATE;
        RAISE NOTICE 'SQLERRM: %', SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Função para alterar o status do refresh_token

CREATE OR REPLACE FUNCTION auth_service.update_refresh_token_status(
    p_active boolean,
    p_status VARCHAR(50),
    p_refresh_token_id UUID,
    p_requester_ip_address VARCHAR(45),
    p_requester_device_name VARCHAR(255)
)
RETURNS BOOLEAN AS $$
DECLARE
    v_error_message TEXT;
BEGIN
    -- Validação do status
    IF p_status NOT IN ('CREATED', 'REFRESH', 'REVOKED', 'EXPIRED') THEN
        RAISE EXCEPTION 'Status inválido. Valores permitidos: CREATED, REFRESH, REVOKED, EXPIRED';
    END IF;

    RAISE NOTICE 'Iniciando atualização do status do refresh token';
    RAISE NOTICE 'Parâmetros recebidos:';
    RAISE NOTICE 'active: %', p_active;
    RAISE NOTICE 'status: %', p_status;
    RAISE NOTICE 'refresh_token_id: %', p_refresh_token_id;
    RAISE NOTICE 'requester_ip_address: %', p_requester_ip_address;
    RAISE NOTICE 'requester_device_name: %', p_requester_device_name;

    -- Atualiza o refresh_token
    BEGIN
        UPDATE auth_service.refresh_tokens
        SET 
            active = p_active,
            updated_at = CURRENT_TIMESTAMP
        WHERE refresh_token_id = p_refresh_token_id;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Refresh token não encontrado';
        END IF;

        RAISE NOTICE 'Status do refresh token atualizado com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao atualizar status do refresh token: %', v_error_message;
        RAISE EXCEPTION 'Erro na atualização do status do refresh token';
    END;

    -- Insere o log no audit
    BEGIN
        INSERT INTO auth_service.refresh_token_audit (
            refresh_token_id,
            action_type,
            requester_ip_address,
            requester_device_name,
            event_timestamp
        ) VALUES (
            p_refresh_token_id,
            p_status,
            p_requester_ip_address,
            p_requester_device_name,
            CURRENT_TIMESTAMP
        );
        RAISE NOTICE 'Log de audit inserido com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao inserir log de audit: %', v_error_message;
        RAISE EXCEPTION 'Erro na inserção do log de audit';
    END;

    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro geral: %', v_error_message;
        RAISE NOTICE 'SQLSTATE: %', SQLSTATE;
        RAISE NOTICE 'SQLERRM: %', SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Criar função para registrar o refresh dos token.
-- Função para atualizar o refresh token e registrar no audit
CREATE OR REPLACE FUNCTION auth_service.update_refresh_token_for_api_refresh(
    p_refresh_token_id UUID,
    p_action_type VARCHAR(50),
    p_requester_ip_address VARCHAR(45),
    p_requester_device_name VARCHAR(255)
)
RETURNS BOOLEAN AS $$
DECLARE
    v_error_message TEXT;
BEGIN
    -- Validação do action_type
    IF p_action_type NOT IN ('REFRESH', 'EXPIRED') THEN
        RAISE EXCEPTION 'Tipo de ação inválido. Valores permitidos: REFRESH, EXPIRED';
    END IF;

    RAISE NOTICE 'Iniciando atualização do refresh token e registro no audit';
    RAISE NOTICE 'Parâmetros recebidos:';
    RAISE NOTICE 'refresh_token_id: %', p_refresh_token_id;
    RAISE NOTICE 'action_type: %', p_action_type;
    RAISE NOTICE 'requester_ip_address: %', p_requester_ip_address;
    RAISE NOTICE 'requester_device_name: %', p_requester_device_name;

    -- Atualiza o refresh_token
    BEGIN
        UPDATE auth_service.refresh_tokens
        SET 
            updated_at = CURRENT_TIMESTAMP,
            active = CASE WHEN p_action_type = 'EXPIRED' THEN false ELSE active END
        WHERE refresh_token_id = p_refresh_token_id;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Refresh token não encontrado';
        END IF;

        RAISE NOTICE 'Refresh token atualizado com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao atualizar refresh token: %', v_error_message;
        RAISE EXCEPTION 'Erro na atualização do refresh token';
    END;

    -- Insere o log no audit
    BEGIN
        INSERT INTO auth_service.refresh_token_audit (
            refresh_token_id,
            action_type,
            requester_ip_address,
            requester_device_name,
            event_timestamp
        ) VALUES (
            p_refresh_token_id,
            p_action_type,
            p_requester_ip_address,
            p_requester_device_name,
            CURRENT_TIMESTAMP
        );
        RAISE NOTICE 'Log de audit inserido com sucesso';
    EXCEPTION WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro ao inserir log de audit: %', v_error_message;
        RAISE EXCEPTION 'Erro na inserção do log de audit';
    END;

    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        GET STACKED DIAGNOSTICS v_error_message = MESSAGE_TEXT;
        RAISE NOTICE 'Erro geral: %', v_error_message;
        RAISE NOTICE 'SQLSTATE: %', SQLSTATE;
        RAISE NOTICE 'SQLERRM: %', SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;