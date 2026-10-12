package com.mas.co.service.impl;

import com.mas.co.constants.BusinessConstants;
import com.mas.co.dto.CobroGlobalDto;
import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.entity.CobroGlobal;
import com.mas.co.entity.Cuota;
import com.mas.co.entity.Usuario;
import com.mas.co.exception.BusinessException;
import com.mas.co.exception.ValidationException;
import com.mas.co.mapper.CobroGlobalMapper;
import com.mas.co.mapper.CuotaMapper;
import com.mas.co.mapper.UsuarioMapper;
import com.mas.co.repository.CobroGlobalRepository;
import com.mas.co.repository.CuotaRepository;
import com.mas.co.repository.UsuarioRepository;
import com.mas.co.service.CobroGlobalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de CobroGlobal.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CobroGlobalServiceImpl implements CobroGlobalService {

    private final CobroGlobalRepository cobroGlobalRepository;
    private final CuotaRepository cuotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CobroGlobalMapper cobroGlobalMapper;
    private final CuotaMapper cuotaMapper;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public CobroGlobalDto crearCobroGlobal(CobroGlobalDto cobroGlobalDto) {
        log.debug("Creando cobro global: {}", cobroGlobalDto.getDescripcion());

        validarCobroGlobal(cobroGlobalDto);

        List<Usuario> usuarios = usuarioRepository.findByActivoTrueOrderByNombreAsc();
        if (usuarios.isEmpty()) {
            throw BusinessException.globalChargeWithoutUsers();
        }

        CobroGlobal cobroGlobal = cobroGlobalRepository.save(cobroGlobalMapper.toEntity(cobroGlobalDto));

        List<Cuota> cuotas = cuotaRepository.saveAll(
                usuarios.stream().map(usuario -> crearCuota(cobroGlobal, usuario)).toList());

        log.info("Cobro global creado exitosamente con ID: {} ({} usuarios)",
                cobroGlobal.getId(), cuotas.size());
        return cobroGlobalMapper.toDto(cobroGlobal, cuotas);
    }

    @Override
    public CobroGlobalDto obtenerCobroGlobal(Long id) {
        log.debug("Obteniendo cobro global con ID: {}", id);

        return toDto(buscarCobroGlobal(id));
    }

    @Override
    public Page<CobroGlobalDto> obtenerCobrosGlobales(Pageable pageable) {
        log.debug("Obteniendo cobros globales paginados: página {}, tamaño {}",
                 pageable.getPageNumber(), pageable.getPageSize());

        return cobroGlobalRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<CuotaDto> obtenerCuotas(Long id) {
        log.debug("Obteniendo cuotas del cobro global: {}", id);

        buscarCobroGlobal(id);
        return cuotaMapper.toDtoList(cuotaRepository.findByCobroGlobalId(id));
    }

    @Override
    public List<UsuarioDto> obtenerUsuariosSinCobro(Long id) {
        log.debug("Obteniendo usuarios sin cuota en el cobro global: {}", id);

        Set<Long> usuariosConCobro = cuotaRepository.findByCobroGlobalId(id).stream()
                .map(cuota -> cuota.getUsuario().getId())
                .collect(Collectors.toSet());

        return usuarioRepository.findByActivoTrueOrderByNombreAsc().stream()
                .filter(usuario -> !usuariosConCobro.contains(usuario.getId()))
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CuotaDto agregarUsuario(Long id, Long usuarioId) {
        log.debug("Agregando usuario {} al cobro global: {}", usuarioId, id);

        CobroGlobal cobroGlobal = buscarCobroGlobal(id);
        if (!Boolean.TRUE.equals(cobroGlobal.getActivo())) {
            throw BusinessException.globalChargeNotActive(id);
        }

        Usuario usuario = usuarioRepository.findByIdAndActivoTrue(usuarioId)
                .orElseThrow(() -> BusinessException.userNotFound(usuarioId));

        if (cuotaRepository.existsByCobroGlobalIdAndUsuarioId(id, usuarioId)) {
            throw BusinessException.userAlreadyInGlobalCharge(usuarioId, id);
        }

        Cuota cuota = cuotaRepository.save(crearCuota(cobroGlobal, usuario));

        log.info("Usuario {} agregado al cobro global: {}", usuarioId, id);
        return cuotaMapper.toDto(cuota);
    }

    @Override
    @Transactional
    public void cancelarCobroGlobal(Long id) {
        log.debug("Cancelando cobro global con ID: {}", id);

        CobroGlobal cobroGlobal = buscarCobroGlobal(id);

        List<Cuota> cuotasActivas = cuotaRepository.findByCobroGlobalId(id).stream()
                .filter(Cuota::isActiva)
                .toList();
        cuotasActivas.forEach(cuota -> cuota.setActivo(false));
        cuotaRepository.saveAll(cuotasActivas);

        cobroGlobal.setActivo(false);
        cobroGlobalRepository.save(cobroGlobal);

        log.info("Cobro global cancelado exitosamente: {} ({} cuotas desactivadas)",
                id, cuotasActivas.size());
    }

    @Override
    @Transactional
    public void eliminarCobroGlobal(Long id) {
        log.debug("Eliminando cobro global con ID: {}", id);

        CobroGlobal cobroGlobal = buscarCobroGlobal(id);

        List<Cuota> cuotas = cuotaRepository.findByCobroGlobalId(id);
        if (cuotas.stream().anyMatch(cuota -> cuota.getCuotaActual() > 0
                || Boolean.TRUE.equals(cuota.getPagoAnticipado()))) {
            throw BusinessException.globalChargeAlreadyInvoiced(id);
        }

        cuotaRepository.deleteAll(cuotas);
        cobroGlobalRepository.delete(cobroGlobal);

        log.info("Cobro global eliminado exitosamente: {}", id);
    }

    private CobroGlobal buscarCobroGlobal(Long id) {
        return cobroGlobalRepository.findById(id)
                .orElseThrow(() -> BusinessException.globalChargeNotFound(id));
    }

    private CobroGlobalDto toDto(CobroGlobal cobroGlobal) {
        return cobroGlobalMapper.toDto(cobroGlobal, cuotaRepository.findByCobroGlobalId(cobroGlobal.getId()));
    }

    private Cuota crearCuota(CobroGlobal cobroGlobal, Usuario usuario) {
        return Cuota.builder()
                .descripcion(cobroGlobal.getDescripcion())
                .valorTotal(cobroGlobal.getValorTotal())
                .valorCuota(cobroGlobal.getValorTotal() / cobroGlobal.getNumeroCuotas())
                .numeroCuota(cobroGlobal.getNumeroCuotas())
                .cuotaActual(0)
                .usuario(usuario)
                .cobroGlobal(cobroGlobal)
                .build();
    }

    private void validarCobroGlobal(CobroGlobalDto dto) {
        String descripcion = dto.getDescripcion() == null ? "" : dto.getDescripcion().trim();
        if (descripcion.length() < BusinessConstants.MIN_NAME_LENGTH
                || descripcion.length() > BusinessConstants.MAX_NAME_LENGTH) {
            throw new ValidationException("La descripción debe tener entre 4 y 20 caracteres");
        }
        if (dto.getValorTotal() == null || dto.getValorTotal() <= 0) {
            throw new ValidationException("El valor total debe ser positivo");
        }
        if (dto.getNumeroCuotas() == null || dto.getNumeroCuotas() <= 0) {
            throw new ValidationException("El número de cuotas debe ser positivo");
        }
        dto.setDescripcion(descripcion);
    }
}
