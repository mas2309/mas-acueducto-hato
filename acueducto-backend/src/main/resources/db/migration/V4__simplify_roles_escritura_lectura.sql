-- Simplifica el modelo de roles a dos valores: ESCRITURA (control total) y LECTURA (solo lectura,
-- con excepcion de Ingresos y Gastos donde tambien tiene escritura).
-- ADMIN y OPERADOR ya tenian escritura amplia -> pasan a ESCRITURA.
-- CONSULTA ya era solo lectura -> pasa a LECTURA.
UPDATE acueducto.admin_users SET role = 'ESCRITURA' WHERE role IN ('ADMIN', 'OPERADOR');
UPDATE acueducto.admin_users SET role = 'LECTURA' WHERE role = 'CONSULTA';

-- El nombre del constraint varia segun el entorno: V1 lo creaba como "chk_role", pero en entornos
-- donde el esquema se origino via Hibernate ddl-auto (antes de adoptar Flyway, ver baseline-version
-- en application.yml) el nombre real es "admin_users_role_check". Se eliminan ambos si existen.
ALTER TABLE acueducto.admin_users DROP CONSTRAINT IF EXISTS chk_role;
ALTER TABLE acueducto.admin_users DROP CONSTRAINT IF EXISTS admin_users_role_check;
ALTER TABLE acueducto.admin_users ADD CONSTRAINT chk_role CHECK (role IN ('ESCRITURA', 'LECTURA'));
ALTER TABLE acueducto.admin_users ALTER COLUMN role SET DEFAULT 'LECTURA';
