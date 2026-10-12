package com.mas.co.controller;

import com.mas.co.constants.ApiConstants;
import com.mas.co.dto.ApiResponse;
import com.mas.co.dto.NotaInformativaDto;
import com.mas.co.service.NotaInformativaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de la nota informativa de los recibos. La nota se edita desde el panel web.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@RestController
@RequestMapping(ApiConstants.RECEIPT_NOTE_PATH)
@RequiredArgsConstructor
@Tag(name = "Nota informativa", description = "Nota que se imprime en los recibos")
public class NotaInformativaController {

    private final NotaInformativaService notaInformativaService;

    @GetMapping
    @Operation(summary = "Obtener nota", description = "Obtiene la nota informativa y si está activa")
    public ResponseEntity<ApiResponse<NotaInformativaDto>> obtenerNota() {

        NotaInformativaDto nota = notaInformativaService.obtenerNota();
        return ResponseEntity.ok(ApiResponse.success(nota));
    }
}
