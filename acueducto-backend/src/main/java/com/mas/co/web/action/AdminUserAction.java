package com.mas.co.web.action;

import com.mas.co.security.AdminUser;
import com.mas.co.security.AdminUserRepository;
import com.mas.co.security.Role;
import java.util.List;
import org.apache.struts2.interceptor.parameter.StrutsParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("adminUserAction")
public class AdminUserAction extends BaseAction {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private List<AdminUser> adminUsers;
    private AdminUser adminUser;
    private Long id;
    private String password;

    public String listar() {
        adminUsers = adminUserRepository.findAll();
        return SUCCESS;
    }

    public String formulario() {
        if (id != null) {
            adminUser = adminUserRepository.findById(id).orElse(null);
            if (adminUser == null) {
                flashError("Usuario administrativo no encontrado");
                return "redirect";
            }
        } else {
            adminUser = new AdminUser();
        }
        return SUCCESS;
    }

    public String guardar() {
        try {
            if (adminUser.getId() != null) {
                AdminUser existing = adminUserRepository.findById(adminUser.getId()).orElse(null);
                if (existing == null) {
                    flashError("Usuario no encontrado");
                    return "redirect";
                }
                existing.setNombreCompleto(adminUser.getNombreCompleto());
                existing.setEmail(adminUser.getEmail());
                existing.setRole(adminUser.getRole());
                existing.setActivo(adminUser.getActivo());
                if (password != null && !password.isBlank()) {
                    existing.setPassword(passwordEncoder.encode(password));
                }
                adminUserRepository.save(existing);
                flashExito("Usuario actualizado exitosamente");
            } else {
                if (adminUserRepository.existsByUsername(adminUser.getUsername())) {
                    flashError("El nombre de usuario ya existe");
                    return INPUT;
                }
                adminUser.setPassword(passwordEncoder.encode(password));
                adminUserRepository.save(adminUser);
                flashExito("Usuario creado exitosamente");
            }
        } catch (Exception e) {
            flashError("Error al guardar: " + e.getMessage());
            return INPUT;
        }
        return "redirect";
    }

    public String eliminar() {
        try {
            adminUserRepository.deleteById(id);
            flashExito("Usuario eliminado exitosamente");
        } catch (Exception e) {
            flashError("Error al eliminar: " + e.getMessage());
        }
        return "redirect";
    }

    public Role[] getRoles() {
        return Role.values();
    }

    // --- Getters y Setters con @StrutsParameter ---

    @StrutsParameter(depth = 2)
    public AdminUser getAdminUser() {
        return adminUser;
    }

    @StrutsParameter(depth = 2)
    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public List<AdminUser> getAdminUsers() {
        return adminUsers;
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
    public String getPassword() {
        return password;
    }

    @StrutsParameter
    public void setPassword(String password) {
        this.password = password;
    }
}
