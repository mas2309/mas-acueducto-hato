-- Nota informativa que se imprime en los recibos mientras este activa. Se usa un unico registro,
-- igual que en acueducto.valores.
CREATE TABLE IF NOT EXISTS acueducto.notas_informativas (
    id BIGSERIAL PRIMARY KEY,
    texto VARCHAR(150) NOT NULL,
    activa BOOLEAN NOT NULL,
    fecha_actualizacion DATE NOT NULL
);
