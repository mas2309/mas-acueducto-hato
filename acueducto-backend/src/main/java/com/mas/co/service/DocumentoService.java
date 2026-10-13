package com.mas.co.service;

import com.mas.co.dto.DocumentoDto;
import java.io.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz del servicio de documentos importantes.
 *
 * @author MAS Development Team
 * @version 1.0
 */
public interface DocumentoService {

    /**
     * Sube el archivo al almacenamiento y registra el documento.
     *
     * @param documentoDto título y descripción del documento
     * @param archivo archivo a subir
     * @param nombreArchivo nombre original del archivo, con su extensión
     * @param username usuario del panel que lo sube
     * @return documento registrado
     */
    DocumentoDto crear(DocumentoDto documentoDto, File archivo, String nombreArchivo, String username);

    /**
     * Elimina el documento y su archivo del almacenamiento.
     *
     * @param id ID del documento
     */
    void eliminar(Long id);

    /**
     * Obtiene documentos paginados, del más reciente al más antiguo.
     *
     * @param termino texto a buscar en el título; nulo o vacío para todos
     * @param pageable información de paginación
     * @return página de documentos
     */
    Page<DocumentoDto> listar(String termino, Pageable pageable);
}
