package com.mas.co.web.action;

import com.mas.co.dto.UsuarioDto;
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

@Component("usuarioAction")
@Scope("prototype")
public class UsuarioAction extends BaseAction {

    @Autowired
    private UsuarioService usuarioService;

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

    public String listar() {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre"));
        Page<UsuarioDto> resultado;

        if (q != null && !q.isBlank()) {
            resultado = usuarioService.buscarUsuarios(q, pageable);
        } else {
            resultado = usuarioService.obtenerUsuarios(pageable);
        }

        usuarios = resultado.getContent();
        totalPages = resultado.getTotalPages();
        totalElements = resultado.getTotalElements();
        return SUCCESS;
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
