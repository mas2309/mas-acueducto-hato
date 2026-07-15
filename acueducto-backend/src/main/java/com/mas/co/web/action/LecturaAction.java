package com.mas.co.web.action;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.service.LecturaService;
import com.mas.co.service.UsuarioService;
import com.mas.co.service.impl.FacturaPdfService;
import java.io.InputStream;
import java.util.List;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component("lecturaAction")
@Scope("prototype")
public class LecturaAction extends BaseAction {

    @Autowired
    private LecturaService lecturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FacturaPdfService facturaPdfService;


    private LecturaDto lectura;
    private List<LecturaDto> facturas;
    private List<UsuarioDto> usuariosActivos;
    private Long id;

    // PDF
    private InputStream pdfStream;
    private String pdfFileName;

    // Paginación
    private int page = 0;
    private int size = 10;
    private int totalPages;
    private long totalElements;

    // Filtros
    private String q;
    private String filtro; // todas, pendientes, pagadas

    // Pago
    private String metodoPago;

    public String listar() {
        Pageable pageable = PageRequest.of(page, size);
        Page<LecturaDto> resultado;

        if (q != null && !q.isBlank()) {
            resultado = lecturaService.buscarFacturas(q, pageable);
        } else if ("pendientes".equals(filtro)) {
            resultado = lecturaService.obtenerFacturasPendientes(pageable);
        } else if ("pagadas".equals(filtro)) {
            resultado = lecturaService.obtenerFacturasPagadas(pageable);
        } else if ("vencidas".equals(filtro)) {
            resultado = lecturaService.obtenerFacturasPendientes(pageable);
        } else {
            resultado = lecturaService.obtenerTodasFacturas(pageable);
        }

        facturas = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        usuariosActivos = usuarioService.obtenerUsuariosActivos();
        if (lectura == null) {
            lectura = new LecturaDto();
        }
        return SUCCESS;
    }

    public String ingresar() {
        try {
            LecturaDto resultado = lecturaService.ingresarLectura(lectura);
            flashExito("Lectura ingresada exitosamente. Factura #" + resultado.getId()
                    + " generada por $" + String.format("%.0f", resultado.getValorTotal()));
        } catch (Exception e) {
            flashError(e.getMessage());
            usuariosActivos = usuarioService.obtenerUsuariosActivos();
            return INPUT;
        }
        return SUCCESS;
    }

    public String pagar() {
        try {
            PagoDto pagoDto = PagoDto.builder().metodoPago(metodoPago).build();
            lecturaService.registrarPago(id, pagoDto);
            flashExito("Pago registrado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String eliminar() {
        try {
            lecturaService.eliminarFactura(id);
            flashExito("Factura eliminada exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String detalle() {
        try {
            lectura = lecturaService.obtenerFactura(id);
        } catch (Exception e) {
            flashError(e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public String descargarPdf() {
        try {
            byte[] pdf = facturaPdfService.generarPdf(id);
            pdfStream = new java.io.ByteArrayInputStream(pdf);
            pdfFileName = "Factura_" + id + ".pdf";
        } catch (Exception e) {
            flashError(e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public InputStream getPdfStream() {
        return pdfStream;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public LecturaDto getLectura() {
        return lectura;
    }

    @StrutsParameter(depth = 1)
    public void setLectura(LecturaDto lectura) {
        this.lectura = lectura;
    }

    public List<LecturaDto> getFacturas() {
        return facturas;
    }

    public List<UsuarioDto> getUsuariosActivos() {
        return usuariosActivos;
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

    public String getFiltro() {
        return filtro;
    }

    @StrutsParameter
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    @StrutsParameter
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
