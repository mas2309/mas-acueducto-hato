package com.mas.co.web;

import com.newrelic.api.agent.NewRelic;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Registra una línea por petición (método, ruta, estado y duración) y deja el usuario autenticado
 * en el MDC y en la transacción de New Relic, para relacionar logs y trazas.
 *
 * <p>Spring Boot lo registra después de la cadena de Spring Security, por lo que el usuario ya
 * está resuelto tanto para la API (JWT) como para el panel web (sesión).
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String MDC_USUARIO = "usuario";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.nanoTime();
        String usuario = usuarioAutenticado();
        if (usuario != null) {
            MDC.put(MDC_USUARIO, usuario);
            NewRelic.addCustomParameter(MDC_USUARIO, usuario);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duracionMs = (System.nanoTime() - inicio) / 1_000_000;
            int estado = response.getStatus();
            if (estado >= 400) {
                log.warn("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), estado, duracionMs);
            } else {
                log.info("{} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), estado, duracionMs);
            }
            MDC.remove(MDC_USUARIO);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.matches(".*\\.(css|js|map|png|jpg|jpeg|gif|svg|ico|woff2?|ttf)$");
    }

    private static String usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();
    }
}
