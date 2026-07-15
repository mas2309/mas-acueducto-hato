package com.mas.co.web.action;

import com.mas.co.dto.InformeDto;
import com.mas.co.service.impl.InformeExcelService;
import com.mas.co.service.impl.InformeService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("informeAction")
@Scope("prototype")
public class InformeAction extends BaseAction {

    @Autowired
    private InformeService informeService;

    @Autowired
    private InformeExcelService informeExcelService;

    private InformeDto informe;
    private List<Integer> aniosDisponibles;
    private Integer anio;
    private String mes;

    // Excel export
    private InputStream excelStream;
    private String excelFileName;

    public String ver() {
        aniosDisponibles = informeService.obtenerAniosDisponibles();

        if (anio == null) {
            anio = LocalDate.now().getYear();
        }

        if (mes != null && !mes.isBlank()) {
            informe = informeService.generarInformeMensual(mes, anio);
        } else {
            informe = informeService.generarInformeAnual(anio);
        }

        return SUCCESS;
    }

    public String exportarExcel() {
        try {
            if (anio == null) {
                anio = LocalDate.now().getYear();
            }

            if (mes != null && !mes.isBlank()) {
                informe = informeService.generarInformeMensual(mes, anio);
                excelFileName = "Informe_" + mes + "_" + anio + ".xlsx";
            } else {
                informe = informeService.generarInformeAnual(anio);
                excelFileName = "Informe_Anual_" + anio + ".xlsx";
            }

            byte[] excel = informeExcelService.generarExcel(informe, anio, mes);
            excelStream = new ByteArrayInputStream(excel);
        } catch (Exception e) {
            flashError("Error al generar Excel: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    // Getters y Setters

    public InformeDto getInforme() {
        return informe;
    }

    public List<Integer> getAniosDisponibles() {
        return aniosDisponibles;
    }

    public Integer getAnio() {
        return anio;
    }

    @StrutsParameter
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    @StrutsParameter
    public void setMes(String mes) {
        this.mes = mes;
    }

    public InputStream getExcelStream() {
        return excelStream;
    }

    public String getExcelFileName() {
        return excelFileName;
    }
}
