-- Cobros globales (cuotas extraordinarias para todos los usuarios) y soporte de varias cuotas
-- por factura.

-- Cabecera del cobro global: agrupa las cuotas generadas a cada usuario.
CREATE TABLE IF NOT EXISTS acueducto.cobros_globales (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(20) NOT NULL,
    valor_total DOUBLE PRECISION NOT NULL,
    numero_cuotas INTEGER NOT NULL,
    fecha_creacion DATE NOT NULL,
    activo BOOLEAN NOT NULL
);

ALTER TABLE acueducto.cuotas
    ADD COLUMN IF NOT EXISTS cobro_global_id BIGINT REFERENCES acueducto.cobros_globales(id);
ALTER TABLE acueducto.cuotas
    ADD COLUMN IF NOT EXISTS pago_anticipado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_cuota_cobro_global ON acueducto.cuotas(cobro_global_id);

-- Detalle de las cuotas cobradas en cada factura. Guarda una copia de la descripcion, el numero de
-- cuota y el valor para que la factura no cambie si la cuota se edita despues.
CREATE TABLE IF NOT EXISTS acueducto.factura_cuotas (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL REFERENCES acueducto.facturas(id) ON DELETE CASCADE,
    cuota_id BIGINT NOT NULL REFERENCES acueducto.cuotas(id),
    descripcion VARCHAR(20) NOT NULL,
    numero_cuota INTEGER,
    total_cuotas INTEGER NOT NULL,
    valor DOUBLE PRECISION NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_factura_cuota_factura ON acueducto.factura_cuotas(factura_id);
CREATE INDEX IF NOT EXISTS idx_factura_cuota_cuota ON acueducto.factura_cuotas(cuota_id);

-- Facturas existentes: su unica cuota (facturas.cuota_id) pasa al detalle. numero_cuota queda en
-- NULL porque no se guardo que cuota se cobro en cada factura; la columna facturas.cuota_id se
-- conserva sin uso.
INSERT INTO acueducto.factura_cuotas (factura_id, cuota_id, descripcion, numero_cuota, total_cuotas, valor)
SELECT f.id, c.id, c.descripcion, NULL, c.numero_cuota, COALESCE(f.valor_cuota, 0)
FROM acueducto.facturas f
JOIN acueducto.cuotas c ON c.id = f.cuota_id
WHERE NOT EXISTS (
    SELECT 1 FROM acueducto.factura_cuotas fc WHERE fc.factura_id = f.id
);
