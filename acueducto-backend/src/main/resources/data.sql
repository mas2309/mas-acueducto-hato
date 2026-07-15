-- Insertar configuración de valores según datos reales
INSERT INTO acueducto.valores (cargo_fijo,
                               rango_uno, rango_dos, rango_tres, rango_cuatro,
                               valor_uno, valor_dos, valor_tres, valor_cuatro,
                               no_pago)
VALUES (3000.0, -- Cargo fijo mensual
        10.0, -- Límite rango 1 (hasta 10 m³)
        20.0, -- Límite rango 2 (hasta 20 m³)
        30.0, -- Límite rango 3 (hasta 30 m³)
        31.0, -- Tarifa rango 4 (31+ m³)
        600, -- Tarifa rango 1 ($600 por m³)
        1000, -- Tarifa rango 2 ($1000 por m³)
        1500, -- Tarifa rango 3 ($1500 por m³)
        3500, -- Tarifa rango 4 ($3500 por m³)
        2000.0 -- Valor no pago
       );