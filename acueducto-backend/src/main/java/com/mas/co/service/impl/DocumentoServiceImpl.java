package com.mas.co.service.impl;

import com.mas.co.dto.DocumentoDto;
import com.mas.co.entity.Documento;
import com.mas.co.exception.BusinessException;
import com.mas.co.exception.ValidationException;
import com.mas.co.repository.DocumentoRepository;
import com.mas.co.service.DocumentoService;
import com.mas.co.service.StorageService;
import java.io.File;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de documentos importantes.
 *
 * @author MAS Development Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoServiceImpl implements DocumentoService {

    private static final String FOLDER = "documentos";
    private static final int MAX_TITULO = 100;
    private static final int MAX_DESCRIPCION = 300;
    private static final int MAX_NOMBRE_ARCHIVO = 200;
    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx", ".xls", ".xlsx");

    private final DocumentoRepository documentoRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public DocumentoDto crear(DocumentoDto documentoDto, File archivo, String nombreArchivo, String username) {
        String titulo = documentoDto.getTitulo() == null ? "" : documentoDto.getTitulo().trim();
        String descripcion = documentoDto.getDescripcion() == null ? "" : documentoDto.getDescripcion().trim();
        if (titulo.isEmpty() || titulo.length() > MAX_TITULO) {
            throw new ValidationException("El título es obligatorio y no puede superar los 100 caracteres");
        }
        if (descripcion.length() > MAX_DESCRIPCION) {
            throw new ValidationException("La descripción no puede superar los 300 caracteres");
        }
        if (archivo == null || nombreArchivo == null || archivo.length() == 0) {
            throw new ValidationException("Seleccione el archivo del documento");
        }
        String extension = obtenerExtension(nombreArchivo);
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new ValidationException("Tipo de archivo no permitido. Use PDF, imagen (JPG, PNG), Word o Excel");
        }

        LocalDate hoy = LocalDate.now();
        String objectKey = FOLDER + "/" + hoy.getYear() + "/" + hoy.getMonthValue()
                + "/" + UUID.randomUUID() + extension;
        String url = storageService.uploadFile(archivo, objectKey);

        Documento documento;
        try {
            documento = documentoRepository.save(Documento.builder()
                    .titulo(titulo)
                    .descripcion(descripcion.isEmpty() ? null : descripcion)
                    .archivoUrl(url)
                    .archivoNombre(recortar(nombreArchivo, MAX_NOMBRE_ARCHIVO))
                    .subidoPor(username)
                    .build());
        } catch (RuntimeException e) {
            // Si no se pudo registrar, no se deja el archivo huérfano en el almacenamiento
            storageService.deleteFile(url);
            throw e;
        }

        log.info("Documento creado con ID: {} ({}) por {}", documento.getId(), titulo, username);
        return toDto(documento);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("DOCUMENT_NOT_FOUND",
                        "Documento no encontrado con ID: " + id));

        storageService.deleteFile(documento.getArchivoUrl());
        documentoRepository.delete(documento);
        log.info("Documento eliminado: {} ({})", id, documento.getTitulo());
    }

    @Override
    public Page<DocumentoDto> listar(String termino, Pageable pageable) {
        Page<Documento> documentos = termino == null || termino.isBlank()
                ? documentoRepository.findAllByOrderByFechaSubidaDescIdDesc(pageable)
                : documentoRepository.findByTituloContainingIgnoreCaseOrderByFechaSubidaDescIdDesc(
                        termino.trim(), pageable);
        return documentos.map(this::toDto);
    }

    private String obtenerExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        return punto < 0 ? "" : nombreArchivo.substring(punto).toLowerCase(Locale.ROOT);
    }

    private String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(texto.length() - max);
    }

    private DocumentoDto toDto(Documento documento) {
        return DocumentoDto.builder()
                .id(documento.getId())
                .titulo(documento.getTitulo())
                .descripcion(documento.getDescripcion())
                .archivoUrl(documento.getArchivoUrl())
                .archivoNombre(documento.getArchivoNombre())
                .fechaSubida(documento.getFechaSubida())
                .subidoPor(documento.getSubidoPor())
                .build();
    }
}
