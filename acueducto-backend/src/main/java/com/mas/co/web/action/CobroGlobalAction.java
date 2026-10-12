package com.mas.co.web.action;

import com.mas.co.dto.CobroGlobalDto;
import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.UsuarioDto;
import com.mas.co.service.CobroGlobalService;
import com.mas.co.service.CuotaService;
import com.mas.co.service.UsuarioService;
import java.util.List;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("cobroGlobalAction")
@Scope("prototype")
public class CobroGlobalAction extends BaseAction {

    @Autowired
    private CobroGlobalService cobroGlobalService;

    @Autowired
    private CuotaService cuotaService;

    @Autowired
    private UsuarioService usuarioService;

    private CobroGlobalDto cobroGlobal;
    private List<CobroGlobalDto> cobrosGlobales;
    private List<CuotaDto> cuotas;
    private List<UsuarioDto> usuariosSinCobro;
    private int totalUsuariosActivos;
    private Long id;
    private Long cuotaId;
    private Long usuarioId;

    // Paginación
    private int page = 0;
    private int size = 10;
    private int totalPages;
    private long totalElements;

    public String listar() {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<CobroGlobalDto> resultado = cobroGlobalService.obtenerCobrosGlobales(pageable);

        cobrosGlobales = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        totalUsuariosActivos = usuarioService.obtenerUsuariosActivos().size();
        if (cobroGlobal == null) {
            cobroGlobal = new CobroGlobalDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            CobroGlobalDto resultado = cobroGlobalService.crearCobroGlobal(cobroGlobal);
            id = resultado.getId();
            flashExito("Cobro global creado y aplicado a " + resultado.getTotalUsuarios() + " usuarios.");
        } catch (Exception e) {
            flashError(e.getMessage());
            totalUsuariosActivos = usuarioService.obtenerUsuariosActivos().size();
            return INPUT;
        }
        return SUCCESS;
    }

    public String detalle() {
        try {
            cobroGlobal = cobroGlobalService.obtenerCobroGlobal(id);
            cuotas = cobroGlobalService.obtenerCuotas(id);
            usuariosSinCobro = cobroGlobalService.obtenerUsuariosSinCobro(id);
        } catch (Exception e) {
            flashError(e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    public String pagoAnticipado() {
        try {
            CuotaDto resultado = cuotaService.registrarPagoAnticipado(cuotaId);
            flashExito("Pago anticipado registrado para " + resultado.getUsuarioNombre()
                    + ". La cuota no se cobrará más en su factura.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String revertirPagoAnticipado() {
        try {
            CuotaDto resultado = cuotaService.revertirPagoAnticipado(cuotaId);
            flashExito("Pago anticipado revertido para " + resultado.getUsuarioNombre()
                    + ". La cuota vuelve a cobrarse en su factura.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String agregarUsuario() {
        try {
            CuotaDto resultado = cobroGlobalService.agregarUsuario(id, usuarioId);
            flashExito("Cobro aplicado a " + resultado.getUsuarioNombre() + ".");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String cancelar() {
        try {
            cobroGlobalService.cancelarCobroGlobal(id);
            flashExito("Cobro global cancelado. Las cuotas pendientes no se cobrarán más.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String eliminar() {
        try {
            cobroGlobalService.eliminarCobroGlobal(id);
            flashExito("Cobro global eliminado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public CobroGlobalDto getCobroGlobal() {
        return cobroGlobal;
    }

    @StrutsParameter(depth = 1)
    public void setCobroGlobal(CobroGlobalDto cobroGlobal) {
        this.cobroGlobal = cobroGlobal;
    }

    public List<CobroGlobalDto> getCobrosGlobales() {
        return cobrosGlobales;
    }

    public List<CuotaDto> getCuotas() {
        return cuotas;
    }

    public List<UsuarioDto> getUsuariosSinCobro() {
        return usuariosSinCobro;
    }

    public int getTotalUsuariosActivos() {
        return totalUsuariosActivos;
    }

    public Long getId() {
        return id;
    }

    @StrutsParameter
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCuotaId() {
        return cuotaId;
    }

    @StrutsParameter
    public void setCuotaId(Long cuotaId) {
        this.cuotaId = cuotaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    @StrutsParameter
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
}
