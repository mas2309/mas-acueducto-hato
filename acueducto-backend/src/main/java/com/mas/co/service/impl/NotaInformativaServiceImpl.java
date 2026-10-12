package com.mas.co.service.impl;

import com.mas.co.dto.NotaInformativaDto;
import com.mas.co.entity.NotaInformativa;
import com.mas.co.exception.ValidationException;
import com.mas.co.repository.NotaInformativaRepository;
import com.mas.co.service.NotaInformativaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementación del servicio de la nota informativa de los recibos.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotaInformativaServiceImpl implements NotaInformativaService {

    private final NotaInformativaRepository notaInformativaRepository;

    @Override
    public NotaInformativaDto obtenerNota() {
        return notaInformativaRepository.findFirstByOrderByIdDesc()
                .map(this::toDto)
                .orElseGet(() -> NotaInformativaDto.builder().texto("").activa(false).build());
    }

    @Override
    public String obtenerTextoVigente() {
        NotaInformativaDto nota = obtenerNota();
        return Boolean.TRUE.equals(nota.getActiva()) ? nota.getTexto() : "";
    }

    @Override
    @Transactional
    public NotaInformativaDto actualizarNota(NotaInformativaDto notaDto) {
        // El recibo no tiene saltos de línea: la nota se imprime como un solo párrafo
        String texto = notaDto.getTexto() == null ? "" : notaDto.getTexto().trim().replaceAll("\\s+", " ");
        boolean activa = Boolean.TRUE.equals(notaDto.getActiva());

        if (texto.length() > NotaInformativa.MAX_LONGITUD) {
            throw new ValidationException(
                    "La nota no puede superar los " + NotaInformativa.MAX_LONGITUD + " caracteres");
        }
        if (activa && texto.isEmpty()) {
            throw new ValidationException("Escriba el texto de la nota para poder activarla");
        }

        NotaInformativa nota = notaInformativaRepository.findFirstByOrderByIdDesc()
                .orElseGet(NotaInformativa::new);
        nota.setTexto(texto);
        nota.setActiva(activa);
        nota.setFechaActualizacion(LocalDate.now());

        NotaInformativa notaActualizada = notaInformativaRepository.save(nota);
        log.info("Nota informativa actualizada (activa: {}): {}", activa, texto);
        return toDto(notaActualizada);
    }

    private NotaInformativaDto toDto(NotaInformativa nota) {
        return NotaInformativaDto.builder()
                .texto(nota.getTexto())
                .activa(nota.getActiva())
                .fechaActualizacion(nota.getFechaActualizacion())
                .build();
    }
}
