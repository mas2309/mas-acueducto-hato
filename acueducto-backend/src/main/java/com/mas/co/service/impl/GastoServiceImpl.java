package com.mas.co.service.impl;

import com.mas.co.dto.GastoDto;
import com.mas.co.entity.Gasto;
import com.mas.co.entity.enums.CategoriaGasto;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.GastoMapper;
import com.mas.co.repository.GastoRepository;
import com.mas.co.security.AdminUser;
import com.mas.co.security.AdminUserRepository;
import com.mas.co.service.GastoService;
import com.mas.co.service.StorageService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GastoServiceImpl implements GastoService {

  private static final String OBS_FOLDER = "gastos/soportes";

  private final GastoRepository gastoRepository;
  private final GastoMapper gastoMapper;
  private final AdminUserRepository adminUserRepository;
  private final StorageService storageService;

  @Override
  @Transactional
  public GastoDto crear(GastoDto dto, String username, MultipartFile soporte) {
    log.debug("Creando gasto: {} por usuario: {}", dto.getDescripcion(), username);

    AdminUser registradoPor =
        adminUserRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new BusinessException("Usuario no encontrado: " + username, "USER_NOT_FOUND"));

    Gasto gasto = gastoMapper.toEntity(dto, registradoPor);

    if (dto.getSoporteUrl() != null && !dto.getSoporteUrl().isBlank()) {
      gasto.setSoporteUrl(dto.getSoporteUrl());
      gasto.setSoporteNombre(dto.getSoporteNombre());
    }

    if (soporte != null && !soporte.isEmpty()) {
      String url = storageService.uploadFile(soporte, OBS_FOLDER);
      gasto.setSoporteUrl(url);
      gasto.setSoporteNombre(soporte.getOriginalFilename());
    }

    Gasto guardado = gastoRepository.save(gasto);
    log.info("Gasto creado con el ID: {}", guardado.getId());
    return gastoMapper.toDto(guardado);
  }

  @Override
  public GastoDto obtener(Long id) {
    Gasto gasto =
        gastoRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException("Gasto no encontrado con ID: " + id, "GASTO_NOT_FOUND"));
    return gastoMapper.toDto(gasto);
  }

  @Override
  @Transactional
  public GastoDto actualizar(Long id, GastoDto dto, MultipartFile soporte) {
    Gasto gasto =
        gastoRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException("Gasto no encontrado con ID: " + id, "GASTO_NOT_FOUND"));

    gasto.setDescripcion(dto.getDescripcion());
    gasto.setMonto(dto.getMonto());
    gasto.setFecha(dto.getFecha());
    gasto.setCategoria(dto.getCategoria());
    gasto.setResponsable(dto.getResponsable());

    if (dto.getSoporteUrl() != null && !dto.getSoporteUrl().isBlank()) {
      gasto.setSoporteUrl(dto.getSoporteUrl());
      gasto.setSoporteNombre(dto.getSoporteNombre());
    }

    if (soporte != null && !soporte.isEmpty()) {
      if (gasto.getSoporteUrl() != null) {
        storageService.deleteFile(gasto.getSoporteUrl());
      }
      String url = storageService.uploadFile(soporte, OBS_FOLDER);
      gasto.setSoporteUrl(url);
      gasto.setSoporteNombre(soporte.getOriginalFilename());
    }

    Gasto actualizado = gastoRepository.save(gasto);
    log.info("Gasto actualizado: {}", id);
    return gastoMapper.toDto(actualizado);
  }

  @Override
  @Transactional
  public void eliminar(Long id) {
    Gasto gasto =
        gastoRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException("Gasto no encontrado con ID: " + id, "GASTO_NOT_FOUND"));

    if (gasto.getSoporteUrl() != null) {
      storageService.deleteFile(gasto.getSoporteUrl());
    }

    gastoRepository.delete(gasto);
    log.info("Gasto eliminado: {}", id);
  }

  @Override
  @Transactional
  public GastoDto marcarPagado(Long id) {
    Gasto gasto =
        gastoRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new BusinessException("Gasto no encontrado con ID: " + id, "GASTO_NOT_FOUND"));

    gasto.setPagado(true);
    gasto.setFechaPago(LocalDate.now());

    Gasto actualizado = gastoRepository.save(gasto);
    log.info("Gasto marcado como pagado: {}", id);
    return gastoMapper.toDto(actualizado);
  }

  @Override
  public Page<GastoDto> listar(Pageable pageable) {
    return gastoRepository.findByOrderByFechaDesc(pageable).map(gastoMapper::toDto);
  }

  @Override
  public Page<GastoDto> listarPorCategoria(CategoriaGasto categoria, Pageable pageable) {
    return gastoRepository
        .findByCategoriaOrderByFechaDesc(categoria, pageable)
        .map(gastoMapper::toDto);
  }

  @Override
  public Page<GastoDto> listarPendientes(Pageable pageable) {
    return gastoRepository.findByPagadoOrderByFechaDesc(false, pageable).map(gastoMapper::toDto);
  }

  @Override
  public List<GastoDto> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
    return gastoMapper.toDtoList(gastoRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta));
  }

  @Override
  public Double totalPorPeriodo(LocalDate desde, LocalDate hasta) {
    return gastoRepository.sumMontoByFechaBetween(desde, hasta);
  }

  @Override
  public Double totalPendientes() {
    return gastoRepository.sumGastosPendientes();
  }
}
