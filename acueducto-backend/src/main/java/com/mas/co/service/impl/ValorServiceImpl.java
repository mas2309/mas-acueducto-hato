package com.mas.co.service.impl;

import com.mas.co.entity.Valores;
import com.mas.co.repository.ValorRepository;
import com.mas.co.service.ValorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de Valor.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValorServiceImpl implements ValorService {

  private final ValorRepository valorRepository;

  @Override
  @Cacheable("valores")
  public Valores obtenerValores() {
    return valorRepository.findFirstByOrderByIdDesc().orElseGet(this::obtenerValoresPorDefecto);
  }

  @Override
  public Double calcularValorConsumo(Integer consumo) {
    if (consumo == null || consumo <= 0) {
      return 0.0;
    }

    Valores valores = obtenerValores();

    log.debug("Calculando valor para consumo: {} m³", consumo);
    log.debug(
        "Rangos límite - Uno: {}, Dos: {}, Tres: {}, Cuatro: {}",
        valores.getRangoUno(),
        valores.getRangoDos(),
        valores.getRangoTres(),
        valores.getRangoCuatro());
    log.debug(
        "Tarifas - Uno: {}, Dos: {}, Tres: {}, Cuatro: {}",
        valores.getValorUno(),
        valores.getValorDos(),
        valores.getValorTres(),
        valores.getValorCuatro());

    Double valorTotal = 0.0;

    // Rango 1: de 0 a rango_uno (ej: 0-10)
    if (consumo <= valores.getRangoUno()) {
      valorTotal = (double) (consumo * valores.getValorUno());
      log.debug("Rango 1: {} m³ * {} = {}", consumo, valores.getValorUno(), valorTotal);
      return valorTotal;
    }

    // Acumular rango 1 completo
    valorTotal += valores.getRangoUno() * valores.getValorUno();
    log.debug(
        "Rango 1 completo: {} m³ * {} = {}",
        valores.getRangoUno(),
        valores.getValorUno(),
        valores.getRangoUno() * valores.getValorUno());

    // Rango 2: de rango_uno+1 a rango_dos (ej: 11-20)
    if (consumo <= valores.getRangoDos()) {
      Integer consumoRango2 = consumo - valores.getRangoUno().intValue();
      Double valorRango2 = (double) (consumoRango2 * valores.getValorDos());
      valorTotal += valorRango2;
      log.debug("Rango 2: {} m³ * {} = {}", consumoRango2, valores.getValorDos(), valorRango2);
      return valorTotal;
    }

    // Acumular rango 2 completo
    Integer rangoDosMts = valores.getRangoDos().intValue() - valores.getRangoUno().intValue();
    Double valorRango2Completo = (double) (rangoDosMts * valores.getValorDos());
    valorTotal += valorRango2Completo;
    log.debug(
        "Rango 2 completo: {} m³ * {} = {}",
        rangoDosMts,
        valores.getValorDos(),
        valorRango2Completo);

    // Rango 3: de rango_dos+1 a rango_tres (ej: 21-30)
    if (consumo <= valores.getRangoTres()) {
      Integer consumoRango3 = consumo - valores.getRangoDos().intValue();
      Double valorRango3 = (double) (consumoRango3 * valores.getValorTres());
      valorTotal += valorRango3;
      log.debug("Rango 3: {} m³ * {} = {}", consumoRango3, valores.getValorTres(), valorRango3);
      return valorTotal;
    }

    // Acumular rango 3 completo
    Integer rangoTresMts = valores.getRangoTres().intValue() - valores.getRangoDos().intValue();
    Double valorRango3Completo = (double) (rangoTresMts * valores.getValorTres());
    valorTotal += valorRango3Completo;
    log.debug(
        "Rango 3 completo: {} m³ * {} = {}",
        rangoTresMts,
        valores.getValorTres(),
        valorRango3Completo);

    // Rango 4: de rango_tres+1 en adelante (ej: 31+)
    Integer consumoRango4 = consumo - valores.getRangoTres().intValue();
    Double valorRango4 = (double) (consumoRango4 * valores.getValorCuatro());
    valorTotal += valorRango4;
    log.debug("Rango 4: {} m³ * {} = {}", consumoRango4, valores.getValorCuatro(), valorRango4);

    log.debug("Valor total calculado: {}", valorTotal);
    return valorTotal;
  }

  /** Obtiene valores por defecto cuando no están configurados en BD. */
  private Valores obtenerValoresPorDefecto() {
    log.warn("No se encontraron valores en BD, usando valores por defecto");

    Valores valores = new Valores();
    valores.setCargoFijo(3000.0);
    valores.setRangoUno(10.0); // Hasta 10 m³
    valores.setValorUno(600); // $600 por m³
    valores.setRangoDos(20.0); // Hasta 20 m³
    valores.setValorDos(1000); // $1000 por m³
    valores.setRangoTres(30.0); // Hasta 30 m³
    valores.setValorTres(1500); // $1500 por m³
    valores.setRangoCuatro(31.0); // Más de 30 m³
    valores.setValorCuatro(3500); // $3500 por m³
    valores.setNoPago(2000.0);

    return valores;
  }
}
