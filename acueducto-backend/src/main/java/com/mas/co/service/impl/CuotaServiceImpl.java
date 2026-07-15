package com.mas.co.service.impl;

import com.mas.co.dto.CuotaDto;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.CuotaMapper;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.CuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de Cuota.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuotaServiceImpl implements CuotaService {

    private final CuotaRepository cuotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuotaMapper cuotaMapper;

    @Override
    @Transactional
    public CuotaDto crearCuota(CuotaDto cuotaDto) {
        log.debug("Creando cuota: {}", cuotaDto.getDescripcion());
        
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(cuotaDto.getUsuarioId())
                .orElseThrow(() -> BusinessException.userNotFound(cuotaDto.getUsuarioId()));
        
        Cuota cuota = cuotaMapper.toEntity(cuotaDto);
        cuota.setUsuario(usuario);
        
        // Calcular valor de cuota automáticamente
        calcularValorCuota(cuota);
        
        Cuota cuotaGuardada = cuotaRepository.save(cuota);
        
        log.info("Cuota creada exitosamente con ID: {} (valor cuota: {})", 
                cuotaGuardada.getId(), cuotaGuardada.getValorCuota());
        return cuotaMapper.toDto(cuotaGuardada);
    }

    @Override
    public CuotaDto obtenerCuota(Long id) {
        log.debug("Obteniendo cuota con ID: {}", id);
        
        Cuota cuota = cuotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.quotaNotFound(id));
        
        return cuotaMapper.toDto(cuota);
    }

    @Override
    @Transactional
    public CuotaDto actualizarCuota(Long id, CuotaDto cuotaDto) {
        log.debug("Actualizando cuota con ID: {}", id);
        
        Cuota cuota = cuotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.quotaNotFound(id));
        
        // Validar usuario si cambió
        if (cuotaDto.getUsuarioId() != null && !cuotaDto.getUsuarioId().equals(cuota.getUsuario().getId())) {
            Usuario usuario = usuarioRepository.findByIdAndActivoTrue(cuotaDto.getUsuarioId())
                    .orElseThrow(() -> BusinessException.userNotFound(cuotaDto.getUsuarioId()));
            cuota.setUsuario(usuario);
        }
        
        cuotaMapper.updateEntity(cuotaDto, cuota);
        
        // Recalcular valor de cuota si cambió el total o número de cuotas
        calcularValorCuota(cuota);
        
        Cuota cuotaActualizada = cuotaRepository.save(cuota);
        
        log.info("Cuota actualizada exitosamente: {} (nuevo valor cuota: {})", 
                id, cuotaActualizada.getValorCuota());
        return cuotaMapper.toDto(cuotaActualizada);
    }

    @Override
    @Transactional
    public CuotaDto procesarPagoCuota(Long id) {
        log.debug("Procesando pago de cuota con ID: {}", id);
        
        Cuota cuota = cuotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.quotaNotFound(id));
        
        // Verificar que la cuota esté activa
        if (!cuota.isActiva()) {
            throw BusinessException.quotaNotActive(id);
        }
        
        // Incrementar cuota actual
        Integer cuotaActualAnterior = cuota.getCuotaActual();
        cuota.setCuotaActual(cuotaActualAnterior + 1);
        
        // Verificar si se completaron todas las cuotas
        if (cuota.getCuotaActual() >= cuota.getNumeroCuota()) {
            cuota.setActivo(false);
            log.info("Cuota completada y desactivada automáticamente: {}", id);
        }
        
        Cuota cuotaActualizada = cuotaRepository.save(cuota);
        
        log.info("Pago de cuota procesado exitosamente: {} (cuota {}/{})", 
                id, cuota.getCuotaActual(), cuota.getNumeroCuota());
        
        return cuotaMapper.toDto(cuotaActualizada);
    }

    @Override
    @Transactional
    public void desactivarCuota(Long id) {
        log.debug("Desactivando cuota con ID: {}", id);
        
        Cuota cuota = cuotaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> BusinessException.quotaNotFound(id));
        
        cuota.setActivo(false);
        cuotaRepository.save(cuota);
        
        log.info("Cuota desactivada exitosamente: {}", id);
    }

    @Override
    @Transactional
    public void eliminarCuota(Long id) {
        log.debug("Eliminando cuota con ID: {}", id);
        
        if (!cuotaRepository.existsById(id)) {
            throw BusinessException.quotaNotFound(id);
        }
        
        cuotaRepository.deleteById(id);
        
        log.info("Cuota eliminada exitosamente: {}", id);
    }

    @Override
    public Page<CuotaDto> obtenerCuotas(Pageable pageable) {
        log.debug("Obteniendo cuotas paginadas: página {}, tamaño {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Cuota> cuotas = cuotaRepository.findByActivoTrue(pageable);
        return cuotas.map(cuotaMapper::toDto);
    }

    @Override
    public Page<CuotaDto> obtenerCuotasPorUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Obteniendo cuotas del usuario: {}", usuarioId);
        
        // Validar que el usuario existe
        if (!usuarioRepository.existsById(usuarioId)) {
            throw BusinessException.userNotFound(usuarioId);
        }
        
        Page<Cuota> cuotas = cuotaRepository.findByUsuarioIdAndActivoTrue(usuarioId, pageable);
        return cuotas.map(cuotaMapper::toDto);
    }

    @Override
    public Page<CuotaDto> buscarCuotas(String termino, Pageable pageable) {
        log.debug("Buscando cuotas con término: {}", termino);
        
        Page<Cuota> cuotas = cuotaRepository.findByDescripcionContainingIgnoreCase(termino, pageable);
        return cuotas.map(cuotaMapper::toDto);
    }

    @Override
    public List<CuotaDto> obtenerCuotasActivas() {
        log.debug("Obteniendo todas las cuotas activas");
        
        List<Cuota> cuotas = cuotaRepository.findByActivoTrueOrderByFechaInsertDesc();
        return cuotaMapper.toDtoList(cuotas);
    }

    @Override
    public List<CuotaDto> obtenerCuotasActivasPorUsuario(Long usuarioId) {
        log.debug("Obteniendo cuotas activas del usuario: {}", usuarioId);
        
        // Validar que el usuario existe
        if (!usuarioRepository.existsById(usuarioId)) {
            throw BusinessException.userNotFound(usuarioId);
        }
        
        List<Cuota> cuotas = cuotaRepository.findByUsuarioIdAndActivoTrueOrderByFechaInsertDesc(usuarioId);
        return cuotaMapper.toDtoList(cuotas);
    }

    /**
     * Calcula el valor de cada cuota dividiendo el valor total entre el número de cuotas.
     * 
     * @param cuota la cuota a calcular
     */
    private void calcularValorCuota(Cuota cuota) {
        if (cuota.getValorTotal() != null && cuota.getNumeroCuota() != null && cuota.getNumeroCuota() > 0) {
            double valorCuota = cuota.getValorTotal() / cuota.getNumeroCuota();
            cuota.setValorCuota(valorCuota);
            
            log.debug("Valor cuota calculado: {} (total: {} / cuotas: {})", 
                    valorCuota, cuota.getValorTotal(), cuota.getNumeroCuota());
        } else {
            log.warn("No se pudo calcular el valor de cuota: valorTotal={}, numeroCuota={}", 
                    cuota.getValorTotal(), cuota.getNumeroCuota());
        }
    }
}