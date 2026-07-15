package com.mas.co.web.action;

import com.mas.co.dto.GastoDto;
import com.mas.co.entity.enums.CategoriaGasto;
import com.mas.co.service.GastoService;
import com.mas.co.service.StorageService;
import com.mas.co.service.impl.FinanzasExcelService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("gastoFinanzasAction")
public class GastoFinanzasAction extends BaseAction {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FinanzasExcelService finanzasExcelService;

    private List<GastoDto> gastos;
    private GastoDto gasto;
    private Long id;
    private int page = 0;
    private int size = 15;
    private int totalPages;
    private long totalElements;
    private Integer anio;

    // Upload - Convención Struts: campo + ContentType + FileName
    private File soporteFile;
    private String soporteFileContentType;
    private String soporteFileFileName;

    // Excel
    private InputStream excelStream;
    private String excelFileName;

    public String listar() {
        Page<GastoDto> resultado = gastoService.listar(PageRequest.of(page, size));
        gastos = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        if (id != null) {
            gasto = gastoService.obtener(id);
        } else {
            gasto = new GastoDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            String username = getAuthenticatedUsername();
            String soporteUrl = handleFileUpload();

            if (gasto.getId() != null) {
                if (soporteUrl != null) {
                    gasto.setSoporteUrl(soporteUrl);
                    gasto.setSoporteNombre(soporteFileFileName);
                }
                gastoService.actualizar(gasto.getId(), gasto, null);
                flashExito("Gasto actualizado exitosamente.");
            } else {
                if (soporteUrl != null) {
                    gasto.setSoporteUrl(soporteUrl);
                    gasto.setSoporteNombre(soporteFileFileName);
                }
                gastoService.crear(gasto, username, null);
                flashExito("Gasto creado exitosamente.");
            }
        } catch (Exception e) {
            flashError(e.getMessage());
            return INPUT;
        }
        return "redirect";
    }

    public String eliminar() {
        try {
            gastoService.eliminar(id);
            flashExito("Gasto eliminado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return "redirect";
    }

    public String pagar() {
        try {
            gastoService.marcarPagado(id);
            flashExito("Gasto marcado como pagado.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return "redirect";
    }

    public String exportarExcel() {
        try {
            if (anio == null) {
                anio = LocalDate.now().getYear();
            }
            LocalDate desde = LocalDate.of(anio, 1, 1);
            LocalDate hasta = LocalDate.of(anio, 12, 31);
            List<GastoDto> data = gastoService.listarPorPeriodo(desde, hasta);
            byte[] excel = finanzasExcelService.generarExcelGastos(data, anio);
            excelStream = new ByteArrayInputStream(excel);
            excelFileName = "Gastos_" + anio + ".xlsx";
        } catch (Exception e) {
            flashError("Error al generar Excel: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public CategoriaGasto[] getCategorias() {
        return CategoriaGasto.values();
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    private String handleFileUpload() {
        try {
            jakarta.servlet.http.HttpServletRequest request =
                    org.apache.struts2.ActionContext.getContext().getServletRequest();
            String contentType = request.getContentType();
            if (contentType == null || !contentType.toLowerCase().contains("multipart")) {
                log.debug("Request no es multipart. ContentType: {}", contentType);
                return null;
            }

            jakarta.servlet.http.Part filePart = request.getPart("soporteFile");
            if (filePart == null || filePart.getSize() == 0) {
                log.debug("No se recibió archivo de soporte en el Part.");
                return null;
            }

            soporteFileFileName = filePart.getSubmittedFileName();
            soporteFileContentType = filePart.getContentType();
            log.info("Archivo recibido via Part: '{}' ({}, {} bytes)",
                    soporteFileFileName, soporteFileContentType, filePart.getSize());

            // Guardar en archivo temporal
            java.io.File tempFile = java.io.File.createTempFile("soporte_", getExtension(soporteFileFileName));
            filePart.write(tempFile.getAbsolutePath());

            String folder = "gastos/soportes";
            String objectKey = folder + "/" + LocalDate.now().getYear()
                    + "/" + LocalDate.now().getMonthValue()
                    + "/" + java.util.UUID.randomUUID() + getExtension(soporteFileFileName);
            String url = storageService.uploadFile(tempFile, objectKey);
            log.info("Soporte subido exitosamente. URL: {}", url);

            if (!tempFile.delete()) {
                log.warn("No se pudo eliminar archivo temporal: {}", tempFile.getAbsolutePath());
            }
            return url;
        } catch (Exception e) {
            log.error("Error al procesar/subir soporte: {}", e.getMessage(), e);
            return null;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    // --- Getters y Setters ---

    @StrutsParameter(depth = 2)
    public GastoDto getGasto() {
        return gasto;
    }

    @StrutsParameter(depth = 2)
    public void setGasto(GastoDto gasto) {
        this.gasto = gasto;
    }

    public List<GastoDto> getGastos() {
        return gastos;
    }

    @StrutsParameter
    public Long getId() {
        return id;
    }

    @StrutsParameter
    public void setId(Long id) {
        this.id = id;
    }

    @StrutsParameter
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

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    @StrutsParameter
    public Integer getAnio() {
        return anio;
    }

    @StrutsParameter
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public InputStream getExcelStream() {
        return excelStream;
    }

    public String getExcelFileName() {
        return excelFileName;
    }

    @StrutsParameter
    public File getSoporteFile() {
        return soporteFile;
    }

    @StrutsParameter
    public void setSoporteFile(File soporteFile) {
        this.soporteFile = soporteFile;
    }

    @StrutsParameter
    public String getSoporteFileContentType() {
        return soporteFileContentType;
    }

    @StrutsParameter
    public void setSoporteFileContentType(String soporteFileContentType) {
        this.soporteFileContentType = soporteFileContentType;
    }

    @StrutsParameter
    public String getSoporteFileFileName() {
        return soporteFileFileName;
    }

    @StrutsParameter
    public void setSoporteFileFileName(String soporteFileFileName) {
        this.soporteFileFileName = soporteFileFileName;
    }
}
