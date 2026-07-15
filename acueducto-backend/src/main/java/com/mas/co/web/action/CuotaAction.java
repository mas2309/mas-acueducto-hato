package com.mas.co.web.action;

import com.mas.co.dto.CuotaDto;
import com.mas.co.dto.UsuarioDto;
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

@Component("cuotaAction")
@Scope("prototype")
public class CuotaAction extends BaseAction {

    @Autowired
    private CuotaService cuotaService;

    @Autowired
    private UsuarioService usuarioService;

    private CuotaDto cuota;
    private List<CuotaDto> cuotas;
    private List<UsuarioDto> usuariosActivos;
    private Long id;

    // Paginación
    private int page = 0;
    private int size = 10;
    private int totalPages;
    private long totalElements;

    // Búsqueda
    private String q;

    public String listar() {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaInsert"));
        Page<CuotaDto> resultado;

        if (q != null && !q.isBlank()) {
            resultado = cuotaService.buscarCuotas(q, pageable);
        } else {
            resultado = cuotaService.obtenerCuotas(pageable);
        }

        cuotas = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    public String formulario() {
        usuariosActivos = usuarioService.obtenerUsuariosActivos();
        if (id != null) {
            cuota = cuotaService.obtenerCuota(id);
        } else {
            cuota = new CuotaDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            if (cuota.getId() != null) {
                cuotaService.actualizarCuota(cuota.getId(), cuota);
                flashExito("Cuota actualizada exitosamente.");
            } else {
                cuotaService.crearCuota(cuota);
                flashExito("Cuota creada exitosamente.");
            }
        } catch (Exception e) {
            flashError(e.getMessage());
            usuariosActivos = usuarioService.obtenerUsuariosActivos();
            return INPUT;
        }
        return SUCCESS;
    }

    public String eliminar() {
        try {
            cuotaService.eliminarCuota(id);
            flashExito("Cuota eliminada exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String desactivar() {
        try {
            cuotaService.desactivarCuota(id);
            flashExito("Cuota desactivada exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String procesarPago() {
        try {
            CuotaDto resultado = cuotaService.procesarPagoCuota(id);
            if (!resultado.getActivo()) {
                flashExito("Pago procesado. La cuota ha sido completada y desactivada.");
            } else {
                flashExito("Pago procesado exitosamente. Cuota " + resultado.getCuotaActual() + "/" + resultado.getNumeroCuota());
            }
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public CuotaDto getCuota() {
        return cuota;
    }

    @StrutsParameter(depth = 1)
    public void setCuota(CuotaDto cuota) {
        this.cuota = cuota;
    }

    public List<CuotaDto> getCuotas() {
        return cuotas;
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
}
