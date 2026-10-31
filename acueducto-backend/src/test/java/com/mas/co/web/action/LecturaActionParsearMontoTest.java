package com.mas.co.web.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LecturaActionParsearMontoTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "30000|30000",
        "30.000|30000",
        "$ 30.000|30000",
        "1.250.000|1250000",
        "30.000,50|30000.5",
        "30000,5|30000.5",
        "30000.50|30000.5",
        "0|0"
    })
    void convierteMontosSinDependerDelLocale(String texto, double esperado) {
        assertEquals(esperado, LecturaAction.parsearMonto(texto));
    }

    @Test
    void vacioEsNulo() {
        assertNull(LecturaAction.parsearMonto(null));
        assertNull(LecturaAction.parsearMonto("  "));
    }

    @Test
    void rechazaValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> LecturaAction.parsearMonto("abc"));
        assertThrows(IllegalArgumentException.class, () -> LecturaAction.parsearMonto("-5000"));
    }
}
