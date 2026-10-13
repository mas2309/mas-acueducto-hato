-- Documentos importantes del acueducto (actas, contratos, resoluciones). El archivo se guarda
-- en el mismo bucket de los soportes de ingresos y gastos, bajo la carpeta "documentos".
CREATE TABLE IF NOT EXISTS acueducto.documentos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    archivo_url VARCHAR(500) NOT NULL,
    archivo_nombre VARCHAR(200) NOT NULL,
    fecha_subida DATE NOT NULL,
    subido_por VARCHAR(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_documento_fecha_subida ON acueducto.documentos(fecha_subida);
