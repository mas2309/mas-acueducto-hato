package com.mas.co.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        if (username != null) {
            loginAttemptService.loginFailed(username);
        }

        if (username != null && loginAttemptService.isBlocked(username)) {
            int minutes = loginAttemptService.getRemainingMinutes(username);
            setDefaultFailureUrl("/login?locked=true&minutes=" + minutes);
        } else {
            setDefaultFailureUrl("/login?error=true");
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
