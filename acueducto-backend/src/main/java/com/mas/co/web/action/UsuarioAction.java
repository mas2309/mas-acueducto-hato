package com.mas.co.web.action;

import com.mas.co.dto.UsuarioDto;
import com.mas.co.service.UsuarioService;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("usuarioAction")
@Scope("prototype")
public class UsuarioAction extends BaseAction {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ListadoExcelService listadoExcelService;

    private UsuarioDto usuario;
    private List<UsuarioDto> usuarios;
    private Long id;

    // Paginación
    private int page = 0;
    private int size = 10;
    private int totalPages;
    private long totalElements;

    // Búsqueda
    private String q;

    // Excel export
    private InputStream excelStream;
    private String excelFileName;

    public String listar() {
        Page<UsuarioDto> resultado = consultar(PageRequest.of(page, size, Sort.by("nombre")));

        usuarios = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
    }

    /** Exporta a Excel los mismos usuarios del listado (con la búsqueda aplicada), sin paginar. */
    public String exportarExcel() {
        try {
            boolean hayBusqueda = q != null && !q.isBlank();
            List<UsuarioDto> data = consultar(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).getContent();
            byte[] excel = listadoExcelService.generarExcelUsuarios(
                    data, hayBusqueda ? "Nombre contiene \"" + q + "\"" : "Usuarios activos");
            excelStream = new ByteArrayInputStream(excel);
            excelFileName = "Usuarios_" + LocalDate.now() + ".xlsx";
        } catch (Exception e) {
            flashError("Error al generar Excel: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    private Page<UsuarioDto> consultar(Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return usuarioService.buscarUsuarios(q, pageable);
        }
        return usuarioService.obtenerUsuarios(pageable);
    }

    public InputStream getExcelStream() {
        return excelStream;
    }

    public String getExcelFileName() {
        return excelFileName;
    }

    public String formulario() {
        if (id != null) {
            usuario = usuarioService.obtenerUsuario(id);
        } else {
            usuario = new UsuarioDto();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            if (usuario.getId() != null) {
                usuarioService.actualizarUsuario(usuario.getId(), usuario);
                flashExito("Usuario actualizado exitosamente.");
            } else {
                usuarioService.crearUsuario(usuario);
                flashExito("Usuario creado exitosamente.");
            }
        } catch (Exception e) {
            flashError(e.getMessage());
            return INPUT;
        }
        return SUCCESS;
    }

    public String eliminar() {
        try {
            usuarioService.eliminarUsuario(id);
            flashExito("Usuario eliminado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    public String desactivar() {
        try {
            usuarioService.desactivarUsuario(id);
            flashExito("Usuario desactivado exitosamente.");
        } catch (Exception e) {
            flashError(e.getMessage());
        }
        return SUCCESS;
    }

    // Getters y Setters

    @StrutsParameter(depth = 1)
    public UsuarioDto getUsuario() {
        return usuario;
    }

    @StrutsParameter(depth = 1)
    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public List<UsuarioDto> getUsuarios() {
        return usuarios;
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
