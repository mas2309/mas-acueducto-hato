package com.mas.co.web.action;

import com.mas.co.dto.IngresoDto;
import com.mas.co.entity.enums.CategoriaIngreso;
import com.mas.co.service.IngresoService;
import com.mas.co.service.impl.FinanzasExcelService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component("ingresoFinanzasAction")
public class IngresoFinanzasAction extends BaseAction {

    @Autowired
    private IngresoService ingresoService;

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
            if (ingreso.getId() != null) {
                ingresoService.actualizar(ingreso.getId(), ingreso);
                flashExito("Ingreso actualizado exitosamente.");
            } else {
                ingresoService.crear(ingreso);
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
}
