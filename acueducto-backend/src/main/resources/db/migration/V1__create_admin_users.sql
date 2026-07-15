-- Tabla de usuarios administrativos del sistema
CREATE TABLE IF NOT EXISTS acueducto.admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'OPERADOR',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    ultimo_login TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'OPERADOR', 'CONSULTA'))
);

CREATE INDEX idx_admin_users_username ON acueducto.admin_users(username);
CREATE INDEX idx_admin_users_role ON acueducto.admin_users(role);

-- Tabla de refresh tokens
CREATE TABLE IF NOT EXISTS acueducto.refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    admin_user_id BIGINT NOT NULL REFERENCES acueducto.admin_users(id) ON DELETE CASCADE,
    fecha_expiracion TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    revocado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_refresh_tokens_token ON acueducto.refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user ON acueducto.refresh_tokens(admin_user_id);
