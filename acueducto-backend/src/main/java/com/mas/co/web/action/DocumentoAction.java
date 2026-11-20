package com.mas.co.web.action;

import com.mas.co.dto.DocumentoDto;
import com.mas.co.service.DocumentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ActionContext;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("documentoAction")
@Scope("prototype")
public class DocumentoAction extends BaseAction {

    private static final String ARCHIVO_PART = "archivo";

    @Autowired
    private DocumentoService documentoService;

    private List<DocumentoDto> documentos;
    private DocumentoDto documento;
    private Long id;

    // Paginación
    private int page = 0;
    private int size = 15;
    private int totalPages;
    private long totalElements;

    // Búsqueda
    private String q;

    public String listar() {
        Page<DocumentoDto> resultado = documentoService.listar(q, PageRequest.of(page, size));
        documentos = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        if (documento == null) {
            documento = new DocumentoDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        File archivoTemporal = null;
        try {
            HttpServletRequest request = ActionContext.getContext().getServletRequest();
            String contentType = request.getContentType();
            Part archivoPart = contentType != null && contentType.toLowerCase().contains("multipart")
                    ? request.getPart(ARCHIVO_PART) : null;

            String nombreArchivo = null;
            if (archivoPart != null && archivoPart.getSize() > 0) {
                nombreArchivo = archivoPart.getSubmittedFileName();
                archivoTemporal = File.createTempFile("documento_", ".tmp");
                archivoPart.write(archivoTemporal.getAbsolutePath());
            }

            documentoService.crear(documento, archivoTemporal, nombreArchivo, getAuthenticatedUsername());
            flashExito("Documento guardado exitosamente.");
        } catch (Exception e) {
            log.error("Error al guardar documento: {}", e.getMessage(), e);
            flashError(e.getMessage());
            return INPUT;
        } finally {
            if (archivoTemporal != null && !archivoTemporal.delete()) {
                log.warn("No se pudo eliminar archivo temporal: {}", archivoTemporal.getAbsolutePath());
            }
        }
        return SUCCESS;
    }

    public String eliminar() {
        try {
            documentoService.eliminar(id);
            flashExito("Documento eliminado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public DocumentoDto getDocumento() {
        return documento;
    }

    @StrutsParameter(depth = 1)
    public void setDocumento(DocumentoDto documento) {
        this.documento = documento;
    }

    public List<DocumentoDto> getDocumentos() {
        return documentos;
    }

    public Long getId() {
        return id;
    }

    @StrutsParameter
    public void setId(Long id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    @StrutsParameter
    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    @StrutsParameter
    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public String getQ() {
        return q;
    }

    @StrutsParameter
    public void setQ(String q) {
        this.q = q;
    }
}
