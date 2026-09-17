-- Soporte de archivo (imagen o PDF) para ingresos, igual que en gastos
ALTER TABLE acueducto.ingresos ADD COLUMN IF NOT EXISTS soporte_url VARCHAR(500);
ALTER TABLE acueducto.ingresos ADD COLUMN IF NOT EXISTS soporte_nombre VARCHAR(200);
