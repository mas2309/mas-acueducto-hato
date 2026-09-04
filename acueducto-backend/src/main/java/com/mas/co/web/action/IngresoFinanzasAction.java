package com.mas.co.web.action;

import com.mas.co.dto.IngresoDto;
import com.mas.co.entity.enums.CategoriaIngreso;
import com.mas.co.service.IngresoService;
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
import org.springframework.stereotype.Component;

@Slf4j
@Component("ingresoFinanzasAction")
public class IngresoFinanzasAction extends BaseAction {

    @Autowired
    private IngresoService ingresoService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FinanzasExcelService finanzasExcelService;

    private List<IngresoDto> ingresos;
    private IngresoDto ingreso;
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
        Page<IngresoDto> resultado = ingresoService.listar(PageRequest.of(page, size));
        ingresos = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        if (id != null) {
            ingreso = ingresoService.obtener(id);
        } else {
            ingreso = new IngresoDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            String soporteUrl = handleFileUpload();
            if (soporteUrl != null) {
                ingreso.setSoporteUrl(soporteUrl);
                ingreso.setSoporteNombre(soporteFileFileName);
            }

            if (ingreso.getId() != null) {
                ingresoService.actualizar(ingreso.getId(), ingreso, null);
                flashExito("Ingreso actualizado exitosamente.");
            } else {
                ingresoService.crear(ingreso, null);
                flashExito("Ingreso creado exitosamente.");
            }
        } catch (Exception e) {
            flashError(e.getMessage());
            return INPUT;
        }
        return "redirect";
    }

    public String eliminar() {
        try {
            ingresoService.eliminar(id);
            flashExito("Ingreso eliminado exitosamente.");
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
            List<IngresoDto> data = ingresoService.listarPorPeriodo(desde, hasta);
            byte[] excel = finanzasExcelService.generarExcelIngresos(data, anio);
            excelStream = new ByteArrayInputStream(excel);
            excelFileName = "Ingresos_" + anio + ".xlsx";
        } catch (Exception e) {
            flashError("Error al generar Excel: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public CategoriaIngreso[] getCategorias() {
        return CategoriaIngreso.values();
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

            String folder = "ingresos/soportes";
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

    @StrutsParameter(depth = 2)
    public IngresoDto getIngreso() {
        return ingreso;
    }

    @StrutsParameter(depth = 2)
    public void setIngreso(IngresoDto ingreso) {
        this.ingreso = ingreso;
    }

    public List<IngresoDto> getIngresos() {
        return ingresos;
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

    @StrutsParameter
    public Integer getAnio() {
        return anio;
    }

    @StrutsParameter
    public void setAnio(Integer anio) {
        this.anio = anio;
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
