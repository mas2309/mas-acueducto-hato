package com.mas.co.service.impl;

import com.mas.co.dto.IngresoDto;
import com.mas.co.entity.Ingreso;
import com.mas.co.entity.enums.CategoriaIngreso;
import com.mas.co.exception.BusinessException;
import com.mas.co.mapper.IngresoMapper;
import com.mas.co.repository.IngresoRepository;
import com.mas.co.service.IngresoService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngresoServiceImpl implements IngresoService {

    private final IngresoRepository ingresoRepository;
    private final IngresoMapper ingresoMapper;

    @Override
    @Transactional
    public IngresoDto crear(IngresoDto dto) {
        log.debug("Creando ingreso: {}", dto.getDescripcion());
        Ingreso ingreso = ingresoMapper.toEntity(dto);
        Ingreso guardado = ingresoRepository.save(ingreso);
        log.info("Ingreso creado con ID: {}", guardado.getId());
        return ingresoMapper.toDto(guardado);
    }

    @Override
    public IngresoDto obtener(Long id) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ingreso no encontrado con ID: " + id, "INGRESO_NOT_FOUND"));
        return ingresoMapper.toDto(ingreso);
    }

    @Override
    @Transactional
    public IngresoDto actualizar(Long id, IngresoDto dto) {
        Ingreso ingreso = ingresoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ingreso no encontrado con ID: " + id, "INGRESO_NOT_FOUND"));

        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setCategoria(dto.getCategoria());

        Ingreso actualizado = ingresoRepository.save(ingreso);
        log.info("Ingreso actualizado: {}", id);
        return ingresoMapper.toDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!ingresoRepository.existsById(id)) {
            throw new BusinessException("Ingreso no encontrado con ID: " + id, "INGRESO_NOT_FOUND");
        }
        ingresoRepository.deleteById(id);
        log.info("Ingreso eliminado: {}", id);
    }

    @Override
    public Page<IngresoDto> listar(Pageable pageable) {
        return ingresoRepository.findByOrderByFechaDesc(pageable).map(ingresoMapper::toDto);
    }

    @Override
    public Page<IngresoDto> listarPorCategoria(CategoriaIngreso categoria, Pageable pageable) {
        return ingresoRepository.findByCategoriaOrderByFechaDesc(categoria, pageable).map(ingresoMapper::toDto);
    }

    @Override
    public List<IngresoDto> listarPorPeriodo(LocalDate desde, LocalDate hasta) {
        return ingresoMapper.toDtoList(ingresoRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta));
    }

    @Override
    public Double totalPorPeriodo(LocalDate desde, LocalDate hasta) {
        return ingresoRepository.sumMontoByFechaBetween(desde, hasta);
    }
}
