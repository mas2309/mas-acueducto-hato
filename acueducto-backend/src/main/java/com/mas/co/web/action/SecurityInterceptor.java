package com.mas.co.web.action;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.interceptor.AbstractInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Interceptor de Struts que verifica la autenticación de Spring Security.
 * Redirige al login si no hay sesión activa.
 */
public class SecurityInterceptor extends AbstractInterceptor {

    private static final String LOGIN_RESULT = "login";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return LOGIN_RESULT;
        }

        // Poner usuario en el contexto de Struts para las vistas
        ActionContext.getContext().getSession().put("authenticatedUser", authentication.getName());
        ActionContext.getContext().getSession().put("userRole",
                authentication.getAuthorities().iterator().next().getAuthority());

        return invocation.invoke();
    }
}
