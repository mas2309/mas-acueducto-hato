package com.mas.co.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mas.co.entity.Factura;
import com.mas.co.entity.Usuario;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:periodo;MODE=PostgreSQL;INIT=CREATE SCHEMA IF NOT EXISTS acueducto",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.flyway.enabled=false",
      "spring.sql.init.mode=never"
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("FacturaRepository - Factura anterior y siguiente por período")
class FacturaRepositoryPeriodoTest {

  @Autowired private FacturaRepository facturaRepository;
  @Autowired private TestEntityManager entityManager;

  private Usuario usuario;

  @BeforeEach
  void setUp() {
    usuario = persistirUsuario();
    persistirFactura(usuario, "Noviembre", 2023);
    persistirFactura(usuario, "Enero", 2024);
    persistirFactura(usuario, "Octubre", 2024);
    persistirFactura(persistirUsuario(), "Septiembre", 2024);
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("La anterior compara el mes como número y cruza el cambio de año")
  void facturaAnterior() {
    Factura deOctubre = facturaRepository.findFacturaAnteriorAlPeriodo(usuario.getId(), 202410).orElseThrow();
    assertEquals("Enero", deOctubre.getMes());

    Factura deEnero = facturaRepository.findFacturaAnteriorAlPeriodo(usuario.getId(), 202401).orElseThrow();
    assertEquals("Noviembre", deEnero.getMes());
    assertEquals(2023, deEnero.getAnio());
  }

  @Test
  @DisplayName("La siguiente toma la más próxima, cruza el año y no mezcla usuarios")
  void facturaSiguiente() {
    Factura deNoviembre = facturaRepository.findFacturaSiguienteAlPeriodo(usuario.getId(), 202311).orElseThrow();
    assertEquals("Enero", deNoviembre.getMes());
    assertEquals(2024, deNoviembre.getAnio());

    Factura deEnero = facturaRepository.findFacturaSiguienteAlPeriodo(usuario.getId(), 202401).orElseThrow();
    assertEquals("Octubre", deEnero.getMes());
  }

  @Test
  @DisplayName("Sin factura en esa dirección devuelve vacío")
  void sinFactura() {
    assertTrue(facturaRepository.findFacturaAnteriorAlPeriodo(usuario.getId(), 202311).isEmpty());
    assertTrue(facturaRepository.findFacturaSiguienteAlPeriodo(usuario.getId(), 202410).isEmpty());
  }

  private Usuario persistirUsuario() {
    Usuario nuevo = new Usuario();
    nuevo.setNombre("Test");
    nuevo.setApellidos("Usuario");
    nuevo.setFechaInsert(LocalDate.of(2023, 1, 1));
    nuevo.setActivo(true);
    return entityManager.persist(nuevo);
  }

  private void persistirFactura(Usuario dueno, String mes, int anio) {
    Factura factura = new Factura();
    factura.setUsuario(dueno);
    factura.setMes(mes);
    factura.setAnio(anio);
    factura.setFechaIngreso(LocalDate.of(anio, 1, 1));
    factura.setLecturaActual(10);
    factura.setLecturaAnterior(0);
    entityManager.persist(factura);
  }
}
