-- Tabla de usuarios del sistema de acueducto (clientes del servicio)
CREATE TABLE IF NOT EXISTS acueducto.usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(20) NOT NULL,
    apellidos VARCHAR(20) NOT NULL,
    fecha_insert DATE NOT NULL,
    activo BOOLEAN NOT NULL
);

-- Tabla de configuración de tarifas
CREATE TABLE IF NOT EXISTS acueducto.valores (
    id BIGSERIAL PRIMARY KEY,
    cargo_fijo DOUBLE PRECISION,
    rango_uno DOUBLE PRECISION,
    rango_dos DOUBLE PRECISION,
    rango_tres DOUBLE PRECISION,
    rango_cuatro DOUBLE PRECISION,
    valor_uno INTEGER,
    valor_dos INTEGER,
    valor_tres INTEGER,
    valor_cuatro INTEGER,
    no_pago DOUBLE PRECISION
);

-- Tabla de cuotas (planes de pago a plazos)
CREATE TABLE IF NOT EXISTS acueducto.cuotas (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(20) NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    valor_total DOUBLE PRECISION NOT NULL,
    fecha_insert DATE NOT NULL,
    numero_cuota INTEGER NOT NULL,
    cuota_actual INTEGER NOT NULL,
    activo BOOLEAN NOT NULL,
    usuario_id BIGINT NOT NULL REFERENCES acueducto.usuarios(id)
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS acueducto.facturas (
    id BIGSERIAL PRIMARY KEY,
    fecha_ingreso DATE NOT NULL,
    fecha_pago DATE,
    fecha_actualizacion DATE,
    mes VARCHAR(20) NOT NULL,
    anio INTEGER NOT NULL,
    lectura_actual INTEGER NOT NULL,
    lectura_anterior INTEGER NOT NULL,
    consumo INTEGER NOT NULL,
    valor_consumo DOUBLE PRECISION NOT NULL,
    valor_total DOUBLE PRECISION NOT NULL,
    otros_cobros DOUBLE PRECISION,
    otros_cobros_descripcion VARCHAR(100),
    deuda_anterior DOUBLE PRECISION,
    valor_cuota DOUBLE PRECISION,
    cargo_fijo DOUBLE PRECISION,
    pago BOOLEAN,
    pago_banco BOOLEAN,
    no_pago DOUBLE PRECISION,
    usuario_id BIGINT NOT NULL REFERENCES acueducto.usuarios(id),
    cuota_id BIGINT REFERENCES acueducto.cuotas(id)
);

CREATE INDEX idx_factura_usuario_mes_anio ON acueducto.facturas(usuario_id, mes, anio);
CREATE INDEX idx_factura_fecha_ingreso ON acueducto.facturas(fecha_ingreso);
CREATE INDEX idx_factura_pago ON acueducto.facturas(pago, pago_banco);

-- Tabla de gastos administrativos
CREATE TABLE IF NOT EXISTS acueducto.gastos (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    monto DOUBLE PRECISION NOT NULL,
    fecha DATE NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    pagado BOOLEAN NOT NULL,
    fecha_pago DATE,
    soporte_url VARCHAR(500),
    soporte_nombre VARCHAR(200),
    registrado_por BIGINT NOT NULL REFERENCES acueducto.admin_users(id),
    responsable VARCHAR(100),
    fecha_registro DATE NOT NULL
);

CREATE INDEX idx_gasto_fecha ON acueducto.gastos(fecha);
CREATE INDEX idx_gasto_categoria ON acueducto.gastos(categoria);
CREATE INDEX idx_gasto_pagado ON acueducto.gastos(pagado);

-- Tabla de ingresos
CREATE TABLE IF NOT EXISTS acueducto.ingresos (
    id BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(200) NOT NULL,
    monto DOUBLE PRECISION NOT NULL,
    fecha DATE NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    factura_id BIGINT REFERENCES acueducto.facturas(id),
    fecha_registro DATE NOT NULL
);

CREATE INDEX idx_ingreso_fecha ON acueducto.ingresos(fecha);
CREATE INDEX idx_ingreso_categoria ON acueducto.ingresos(categoria);
