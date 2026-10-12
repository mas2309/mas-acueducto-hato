package com.mas.co.web.action;

import com.mas.co.dto.LecturaDto;
import com.mas.co.dto.PagoDto;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.service.LecturaService;
import com.mas.co.service.UsuarioService;
import com.mas.co.service.impl.FacturaPdfService;
import com.mas.co.service.impl.ListadoExcelService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
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

    @Autowired
    private ListadoExcelService listadoExcelService;


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
    private String filtro;
    private int anioFiltro = java.time.LocalDate.now().getYear();

    // Pago
    private String metodoPago;

    // Excel export
    private InputStream excelStream;
    private String excelFileName;
    private boolean todo;

    public String listar() {
        Page<LecturaDto> resultado = consultar(PageRequest.of(page, size));

        facturas = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    /**
     * Exporta a Excel las mismas facturas del listado (con sus filtros), sin paginar. Con
     * {@code todo} exporta todas las facturas del sistema.
     */
    public String exportarExcel() {
        try {
            Pageable sinPaginar = PageRequest.of(0, Integer.MAX_VALUE);
            List<LecturaDto> data = todo
                    ? lecturaService.obtenerTodasFacturas(sinPaginar).getContent()
                    : consultar(sinPaginar).getContent();
            byte[] excel = listadoExcelService.generarExcelFacturas(data, todo ? "Todas las facturas" : describirFiltro());
            excelStream = new ByteArrayInputStream(excel);
            excelFileName = "Facturas_" + (todo ? "todas" : nombreFiltro()) + "_" + LocalDate.now() + ".xlsx";
        } catch (Exception e) {
            flashError("Error al generar Excel: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    private Page<LecturaDto> consultar(Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return lecturaService.buscarFacturas(q, pageable);
        } else if ("pendientes".equals(filtro)) {
            return lecturaService.obtenerFacturasPendientes(pageable);
        } else if ("vencidas".equals(filtro)) {
            return lecturaService.obtenerFacturasVencidas(pageable);
        } else if ("pagadas".equals(filtro)) {
            return lecturaService.obtenerFacturasPagadas(pageable);
        }
        return lecturaService.obtenerFacturasPorAnio(anioFiltro, pageable);
    }

    private String describirFiltro() {
        if (q != null && !q.isBlank()) {
            return "Usuario contiene \"" + q + "\"";
        }
        return switch (nombreFiltro()) {
            case "pendientes" -> "Facturas pendientes de pago";
            case "vencidas" -> "Facturas vencidas";
            case "pagadas" -> "Facturas pagadas";
            default -> "Facturas del año " + anioFiltro;
        };
    }

    private String nombreFiltro() {
        if (q != null && !q.isBlank()) {
            return "busqueda";
        }
        if ("pendientes".equals(filtro) || "vencidas".equals(filtro) || "pagadas".equals(filtro)) {
            return filtro;
        }
        return String.valueOf(anioFiltro);
    }

    public InputStream getExcelStream() {
        return excelStream;
    }

    public String getExcelFileName() {
        return excelFileName;
    }

    public boolean isTodo() {
        return todo;
    }

    @StrutsParameter
    public void setTodo(boolean todo) {
        this.todo = todo;
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

    public int getAnioFiltro() {
        return anioFiltro;
    }

    @StrutsParameter
    public void setAnioFiltro(int anioFiltro) {
        this.anioFiltro = anioFiltro;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    @StrutsParameter
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
