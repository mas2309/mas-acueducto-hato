package com.mas.co.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Maneja los accesos denegados del panel administrativo (Struts) mostrando
 * un mensaje amigable en vez de la página de error por defecto.
 */
@Component
public class WebAccessDeniedHandler implements AccessDeniedHandler {

    private static final String FLASH_ERROR_SESSION_KEY = "_flashError";
    private static final String DASHBOARD_URL = "/admin/dashboard.action";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
            throws IOException {
        request.getSession().setAttribute(FLASH_ERROR_SESSION_KEY, "No tiene permisos para realizar esta acción.");
        response.sendRedirect(request.getContextPath() + DASHBOARD_URL);
    }
}
