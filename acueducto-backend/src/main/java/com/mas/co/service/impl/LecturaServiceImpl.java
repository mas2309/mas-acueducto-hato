package com.mas.co.service.impl;

import com.mas.co.constants.BusinessConstants;
import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Factura;
import com.mas.co.entity.FacturaCuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.LecturaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.FacturaRepository;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.CuotaService;
import com.mas.co.service.LecturaService;
import com.mas.co.usecase.CalcularValoresFacturaUseCase;
import com.mas.co.usecase.ObtenerDeudaAnteriorUseCase;
import com.mas.co.usecase.ObtenerLecturaAnteriorUseCase;
import com.mas.co.usecase.ValidarPeriodoConsecutivoUseCase;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de Lectura.
 * Este servicio actúa como un orquestador, delegando la lógica de negocio a casos de uso específicos.
 *
 * @author MAS Development Team
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LecturaServiceImpl implements LecturaService {

  private final FacturaRepository facturaRepository;
  private final UsuarioRepository usuarioRepository;
  private final CuotaRepository cuotaRepository;
  private final LecturaMapper lecturaMapper;
  private final CuotaService cuotaService;


  // Casos de Uso
  private final ValidarPeriodoConsecutivoUseCase validarPeriodoConsecutivoUseCase;
  private final ObtenerLecturaAnteriorUseCase obtenerLecturaAnteriorUseCase;
  private final ObtenerDeudaAnteriorUseCase obtenerDeudaAnteriorUseCase;
  private final CalcularValoresFacturaUseCase calcularValoresFacturaUseCase;

  @Override
  public LecturaDto obtenerFactura(Long facturaId) {
    log.debug("Obteniendo factura con ID: {}", facturaId);
    Factura factura = facturaRepository.findById(facturaId)
        .orElseThrow(() -> BusinessException.invoiceNotFound(facturaId));
    return lecturaMapper.toDto(factura);
  }

  @Override
  public Page<LecturaDto> obtenerFacturasPorAnio(Integer anio, Pageable pageable) {
    log.debug("Obteniendo facturas del año: {}", anio);
    return facturaRepository.findByAnioOrderByIdDesc(anio, pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> obtenerFacturasVencidas(Pageable pageable) {
    log.debug("Obteniendo facturas vencidas");
    java.time.LocalDate primerDiaMesActual = java.time.LocalDate.now().withDayOfMonth(1);
    return facturaRepository.findFacturasVencidas(primerDiaMesActual, pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> obtenerTodasFacturas(Pageable pageable) {
    log.debug("Obteniendo todas las facturas paginadas");
    return facturaRepository.findAllByOrderByIdDesc(pageable).map(lecturaMapper::toDto);
  }

  @Override
  @Transactional
  public LecturaDto ingresarLectura(LecturaDto lecturaDto) {
    log.debug("Iniciando flujo de ingreso de lectura para usuario: {}", lecturaDto.getUsuarioId());

    // 1. Validar Usuario
    Usuario usuario = usuarioRepository.findByIdAndActivoTrue(lecturaDto.getUsuarioId())
        .orElseThrow(() -> BusinessException.userNotFound(lecturaDto.getUsuarioId()));

    // 2. Validar Período Consecutivo
    validarPeriodoConsecutivoUseCase.execute(lecturaDto);

    // 3. Validar Duplicados
    if (facturaRepository.existsByUsuarioIdAndMesAndAnio(lecturaDto.getUsuarioId(), lecturaDto.getMes(), lecturaDto.getAnio())) {
      throw BusinessException.invoiceAlreadyExists(lecturaDto.getMes(), lecturaDto.getAnio(), lecturaDto.getUsuarioId());
    }

    // 4. Obtener Lectura Anterior y Calcular Consumo
    Integer lecturaAnterior = obtenerLecturaAnteriorUseCase.execute(lecturaDto.getUsuarioId());
    lecturaDto.setLecturaAnterior(lecturaAnterior);
    if (lecturaDto.getLecturaActual() < lecturaAnterior) {
      throw BusinessException.invalidReading(lecturaDto.getLecturaActual(), lecturaAnterior);
    }
    lecturaDto.setConsumo(lecturaDto.getLecturaActual() - lecturaAnterior);

    // 5. Obtener Deuda Anterior
    lecturaDto.setDeudaAnterior(obtenerDeudaAnteriorUseCase.execute(lecturaDto.getUsuarioId()));

    // 6. Procesar Cuotas Activas
    List<FacturaCuota> cuotasCobradas = cuotaRepository.findByUsuarioIdAndActivoTrueOrderByIdAsc(lecturaDto.getUsuarioId())
        .stream().map(this::cobrarCuota).toList();
    log.debug("Cuotas activas cobradas: {}", cuotasCobradas.size());
    lecturaDto.setValorCuota(cuotasCobradas.stream().mapToDouble(FacturaCuota::getValor).sum());

    // 7. Calcular todos los valores de la factura
    calcularValoresFacturaUseCase.execute(lecturaDto);

    // 8. Crear y Guardar Factura
    Factura factura = lecturaMapper.toEntity(lecturaDto);
    factura.setUsuario(usuario);
    cuotasCobradas.forEach(factura::agregarCuota);

    Factura facturaGuardada = facturaRepository.save(factura);
    log.info("Lectura ingresada exitosamente - Factura ID: {} para usuario: {}", facturaGuardada.getId(), usuario.getNombreCompleto());
    return lecturaMapper.toDto(facturaGuardada);
  }

  @Override
  @Transactional
  public LecturaDto registrarPago(Long facturaId, PagoDto pagoDto) {
    log.debug("Registrando pago para factura ID: {} con método: {}", facturaId, pagoDto.getMetodoPago());
    
    Factura factura = facturaRepository.findById(facturaId)
        .orElseThrow(() -> BusinessException.invoiceNotFound(facturaId));
    
    if (factura.isPagada()) {
      throw BusinessException.invoiceAlreadyPaid(facturaId);
    }
    
    String metodoPago = pagoDto.getMetodoPago().toUpperCase();
    switch (metodoPago) {
      case BusinessConstants.PAYMENT_TYPE_CASH:
        factura.setPago(true);
        break;
      case BusinessConstants.PAYMENT_TYPE_BANK:
        factura.setPagoBanco(true);
        break;
      default:
        throw BusinessException.invalidPaymentMethod(pagoDto.getMetodoPago());
    }
    
    factura.setFechaPago(LocalDate.now());
    Factura facturaPagada = facturaRepository.save(factura);
    recalcularFacturasSiguientes(facturaPagada);

    log.info("Pago registrado exitosamente para factura ID: {} - Método: {}", facturaId, metodoPago);
    return lecturaMapper.toDto(facturaPagada);
  }

  @Override
  public LecturaDto obtenerUltimaLectura(Long usuarioId) {
    log.debug("Obteniendo última lectura del usuario: {}", usuarioId);
    return facturaRepository.findUltimaLecturaPorUsuario(usuarioId).stream().findFirst().map(lecturaMapper::toDto).orElse(LecturaDto.builder().usuarioId(usuarioId).lecturaAnterior(0).build());
  }

  @Override
  public List<LecturaDto> obtenerUltimasLecturas() {
    log.debug("Obteniendo última lectura de todos los usuarios");
    return facturaRepository.findUltimasFacturasPorUsuario().stream()
        .map(lecturaMapper::toDto)
        .toList();
  }

  @Override
  public Page<LecturaDto> obtenerFacturasPorUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Obteniendo facturas del usuario: {}", usuarioId);
    return facturaRepository.findByUsuarioIdOrderByAnioDescMesDesc(usuarioId, pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> obtenerFacturasPorPeriodo(String mes, Integer anio, Pageable pageable) {
    log.debug("Obteniendo facturas del período: {}/{}", mes, anio);
    return facturaRepository.findByMesAndAnioOrderByFechaIngresoDesc(mes, anio, pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> obtenerFacturasPendientes(Pageable pageable) {
    log.debug("Obteniendo facturas pendientes de pago");
    return facturaRepository.findFacturasPendientes(pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> obtenerFacturasPagadas(Pageable pageable) {
    log.debug("Obteniendo facturas pagadas");
    return facturaRepository.findFacturasPagadas(pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Page<LecturaDto> buscarFacturas(String termino, Pageable pageable) {
    log.debug("Buscando facturas con término: {}", termino);
    return facturaRepository.findByUsuarioNombreContaining(termino, pageable).map(lecturaMapper::toDto);
  }

  @Override
  public Double obtenerDeudaPendiente(Long usuarioId) {
    log.debug("Obteniendo deuda pendiente del usuario: {}", usuarioId);
    return facturaRepository.findDeudaPendientePorUsuario(usuarioId);
  }

  @Override
  @Transactional
  public LecturaDto actualizarFactura(Long facturaId, LecturaDto lecturaDto) {
    log.debug("Actualizando factura con ID: {}", facturaId);
    
    Factura facturaExistente = facturaRepository.findById(facturaId)
        .orElseThrow(() -> BusinessException.invoiceNotFound(facturaId));
    
    validarActualizacionFactura(facturaExistente, lecturaDto);
    lecturaDto.setConsumo(lecturaDto.getLecturaActual() - lecturaDto.getLecturaAnterior());
    
    procesarCambioCuotas(facturaExistente, lecturaDto);
    boolean anteriorPagada = aplicarSaldoAnterior(facturaExistente, lecturaDto);
    calcularValoresFacturaUseCase.execute(lecturaDto, anteriorPagada);
    ajustarValoresSiPagada(facturaExistente, lecturaDto);
    actualizarEntidadFactura(facturaExistente, lecturaDto);

    Factura facturaActualizada = facturaRepository.save(facturaExistente);
    recalcularFacturasSiguientes(facturaActualizada);
    log.info("Factura actualizada exitosamente - ID: {}", facturaId);
    return lecturaMapper.toDto(facturaActualizada);
  }

  /**
   * Define la deuda anterior de la factura a partir de la factura del período previo, sin confiar en
   * el valor enviado por el cliente. Una factura ya pagada conserva la deuda con la que se cobró, y
   * también la que no tiene factura previa con la cual recalcularla.
   *
   * @return {@code true} si no corresponde aplicar el recargo por no pago
   */
  private boolean aplicarSaldoAnterior(Factura factura, LecturaDto lecturaDto) {
    Optional<Factura> anterior = buscarFacturaAnterior(lecturaDto);
    if (factura.isPagada() || anterior.isEmpty()) {
      lecturaDto.setDeudaAnterior(factura.getDeudaAnterior());
      return true;
    }
    boolean anteriorPagada = anterior.get().isPagada();
    lecturaDto.setDeudaAnterior(anteriorPagada ? 0.0 : valorOCero(anterior.get().getValorTotal()));
    return anteriorPagada;
  }

  private Optional<Factura> buscarFacturaAnterior(LecturaDto lecturaDto) {
    Integer mes = BusinessConstants.MONTH_TO_INT.get(lecturaDto.getMes());
    if (mes == null) {
      return Optional.empty();
    }
    return facturaRepository.findFacturaAnteriorAlPeriodo(
        lecturaDto.getUsuarioId(), lecturaDto.getAnio() * 100 + mes);
  }

  /**
   * Traslada el saldo de la factura a las siguientes que sigan sin pagar: cada una arrastra el total
   * de la previa. Se detiene en la primera pagada, porque el histórico cobrado no se modifica.
   */
  private void recalcularFacturasSiguientes(Factura factura) {
    Factura anterior = factura;
    Optional<Factura> siguiente = buscarFacturaSiguiente(anterior);
    while (siguiente.isPresent() && !siguiente.get().isPagada()) {
      actualizarSaldoAnterior(siguiente.get(), anterior);
      anterior = facturaRepository.save(siguiente.get());
      siguiente = buscarFacturaSiguiente(anterior);
    }
  }

  private Optional<Factura> buscarFacturaSiguiente(Factura factura) {
    Integer mes = BusinessConstants.MONTH_TO_INT.get(factura.getMes());
    if (mes == null) {
      return Optional.empty();
    }
    return facturaRepository.findFacturaSiguienteAlPeriodo(
        factura.getUsuario().getId(), factura.getAnio() * 100 + mes);
  }

  /**
   * Cambia solo la deuda anterior y el recargo por no pago; el consumo y los demás cobros de la
   * factura se conservan con las tarifas con las que se generó.
   */
  private void actualizarSaldoAnterior(Factura factura, Factura anterior) {
    boolean anteriorPagada = anterior.isPagada();
    double deudaNueva = anteriorPagada ? 0.0 : valorOCero(anterior.getValorTotal());
    double noPagoNuevo = anteriorPagada ? 0.0 : valorOCero(factura.getNoPago());
    double totalSinSaldo = valorOCero(factura.getValorTotal())
        - valorOCero(factura.getDeudaAnterior())
        - valorOCero(factura.getNoPago());

    factura.setDeudaAnterior(deudaNueva);
    factura.setNoPago(noPagoNuevo);
    factura.setValorTotal(totalSinSaldo + deudaNueva + noPagoNuevo);
    factura.setFechaActualizacion(LocalDate.now());
  }

  private double valorOCero(Double valor) {
    return valor == null ? 0.0 : valor;
  }

  private void validarActualizacionFactura(Factura facturaExistente, LecturaDto lecturaDto) {
    if (!facturaExistente.getUsuario().getId().equals(lecturaDto.getUsuarioId())) {
      throw new BusinessException("INVALID_UPDATE", "No se puede cambiar el usuario de una factura");
    }
    
    if (!facturaExistente.getMes().equals(lecturaDto.getMes()) || 
        !facturaExistente.getAnio().equals(lecturaDto.getAnio())) {
      if (facturaRepository.existsByUsuarioIdAndMesAndAnio(
          lecturaDto.getUsuarioId(), lecturaDto.getMes(), lecturaDto.getAnio())) {
        throw BusinessException.invoiceAlreadyExists(
            lecturaDto.getMes(), lecturaDto.getAnio(), lecturaDto.getUsuarioId());
      }
    }
    
    if (lecturaDto.getLecturaActual() < lecturaDto.getLecturaAnterior()) {
      throw BusinessException.invalidReading(
          lecturaDto.getLecturaActual(), lecturaDto.getLecturaAnterior());
    }
  }

  private FacturaCuota cobrarCuota(Cuota cuota) {
    CuotaDto cuotaCobrada = cuotaService.procesarPagoCuota(cuota.getId());
    return FacturaCuota.builder()
        .cuota(cuota)
        .descripcion(cuotaCobrada.getDescripcion())
        .numeroCuota(cuotaCobrada.getCuotaActual())
        .totalCuotas(cuotaCobrada.getNumeroCuota())
        .valor(cuotaCobrada.getValorCuota())
        .build();
  }

  private void procesarCambioCuotas(Factura factura, LecturaDto lecturaDto) {
    List<Long> cuotaIds = resolverCuotaIds(factura, lecturaDto);

    List<Cuota> cuotasNuevas = cuotaIds.stream()
        .filter(cuotaId -> !obtenerCuotaIds(factura).contains(cuotaId))
        .map(cuotaId -> cuotaRepository.findById(cuotaId)
            .orElseThrow(() -> BusinessException.quotaNotFound(cuotaId)))
        .toList();

    List<FacturaCuota> cuotasRetiradas = factura.getCuotas().stream()
        .filter(facturaCuota -> !cuotaIds.contains(facturaCuota.getCuota().getId()))
        .toList();
    cuotasRetiradas.forEach(facturaCuota -> revertirCuota(facturaCuota.getCuota()));
    factura.getCuotas().removeAll(cuotasRetiradas);

    cuotasNuevas.forEach(cuota -> factura.agregarCuota(cobrarCuota(cuota)));

    lecturaDto.setValorCuota(factura.sumarCuotas());
  }

  private List<Long> resolverCuotaIds(Factura factura, LecturaDto lecturaDto) {
    if (lecturaDto.getCuotaIds() != null) {
      return lecturaDto.getCuotaIds();
    }
    if (lecturaDto.getCuotaId() == null) {
      return List.of();
    }
    // Un cliente que solo maneja cuotaId reenvía la primera cuota de la factura: en ese caso se
    // conservan todas para no quitar las demás.
    List<Long> cuotaIdsActuales = obtenerCuotaIds(factura);
    return cuotaIdsActuales.contains(lecturaDto.getCuotaId())
        ? cuotaIdsActuales
        : List.of(lecturaDto.getCuotaId());
  }

  private List<Long> obtenerCuotaIds(Factura factura) {
    return factura.getCuotas().stream()
        .map(facturaCuota -> facturaCuota.getCuota().getId())
        .toList();
  }

  private void revertirCuota(Cuota cuota) {
    if (cuota != null && cuota.getCuotaActual() > 0) {
      cuota.setCuotaActual(cuota.getCuotaActual() - 1);
      // Una cuota pagada por anticipado no debe volver a cobrarse en la factura
      if (!Boolean.TRUE.equals(cuota.getPagoAnticipado())) {
        cuota.setActivo(true);
      }
      cuotaRepository.save(cuota);
      log.debug("Cuota {} revertida", cuota.getId());
    }
  }

  private void ajustarValoresSiPagada(Factura facturaExistente, LecturaDto lecturaDto) {
    if (facturaExistente.isPagada()) {
      lecturaDto.setPago(facturaExistente.getPago());
      lecturaDto.setPagoBanco(facturaExistente.getPagoBanco());
      lecturaDto.setNoPago(0.0);
      lecturaDto.setValorTotal(lecturaDto.getValorConsumo() + lecturaDto.getCargoFijo() + 
          lecturaDto.getOtrosCobros() + lecturaDto.getDeudaAnterior() + lecturaDto.getValorCuota());
    }
  }

  private void actualizarEntidadFactura(Factura factura, LecturaDto dto) {
    factura.setMes(dto.getMes());
    factura.setAnio(dto.getAnio());
    factura.setLecturaActual(dto.getLecturaActual());
    factura.setLecturaAnterior(dto.getLecturaAnterior());
    factura.setConsumo(dto.getConsumo());
    factura.setValorConsumo(dto.getValorConsumo());
    factura.setCargoFijo(dto.getCargoFijo());
    factura.setOtrosCobros(dto.getOtrosCobros());
    factura.setOtrosCobrosDescripcion(dto.getOtrosCobrosDescripcion());
    factura.setDeudaAnterior(dto.getDeudaAnterior());
    factura.setValorCuota(dto.getValorCuota());
    factura.setNoPago(dto.getNoPago());
    factura.setValorTotal(dto.getValorTotal());
    factura.setFechaActualizacion(LocalDate.now());
  }

  @Override
  @Transactional
  public void eliminarFactura(Long facturaId) {
    log.debug("Eliminando factura con ID: {}", facturaId);
    
    Factura factura = facturaRepository.findById(facturaId)
        .orElseThrow(() -> BusinessException.invoiceNotFound(facturaId));
    
    factura.getCuotas().forEach(facturaCuota -> revertirCuota(facturaCuota.getCuota()));
    facturaRepository.deleteById(facturaId);
    log.info("Factura eliminada exitosamente - ID: {}", facturaId);
  }
}
