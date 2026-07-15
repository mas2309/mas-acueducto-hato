package com.mas.co.security;

import com.mas.co.exception.BusinessException;
import com.mas.co.security.dto.AuthResponse;
import com.mas.co.security.dto.LoginRequest;
import com.mas.co.security.dto.RegisterRequest;
import com.mas.co.security.dto.TokenRefreshRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    public AuthResponse login(LoginRequest request, String ip) {
        if (loginAttemptService.isBlocked(request.getUsername())) {
            int minutes = loginAttemptService.getRemainingMinutes(request.getUsername());
            log.warn("Intento de login bloqueado para '{}' desde IP '{}'. Bloqueado por {} minutos más.",
                    request.getUsername(), ip, minutes);
            throw new BusinessException(
                    "Cuenta bloqueada por múltiples intentos fallidos. Intente en " + minutes + " minutos.",
                    "AUTH_ACCOUNT_LOCKED");
        }

        if (loginAttemptService.isIpBlocked(ip)) {
            log.warn("IP '{}' bloqueada por exceso de intentos.", ip);
            throw new BusinessException(
                    "Demasiados intentos desde esta dirección. Intente más tarde.",
                    "AUTH_IP_LOCKED");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            loginAttemptService.loginFailed(request.getUsername(), ip);
            if (loginAttemptService.isBlocked(request.getUsername())) {
                log.warn("Cuenta '{}' BLOQUEADA tras alcanzar el máximo de intentos.", request.getUsername());
            }
            throw e;
        }

        loginAttemptService.loginSucceeded(request.getUsername());

        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado", "AUTH_USER_NOT_FOUND"));

        user.setUltimoLogin(LocalDateTime.now());
        adminUserRepository.save(user);

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (adminUserRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El usuario ya existe", "AUTH_USER_EXISTS");
        }

        AdminUser user = AdminUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .email(request.getEmail())
                .role(request.getRole())
                .build();

        adminUserRepository.save(user);
        log.info("Usuario registrado: {} con rol {}", user.getUsername(), user.getRole());

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token inválido", "AUTH_INVALID_REFRESH"));

        if (!refreshToken.isValido()) {
            refreshTokenRepository.revokeAllByUserId(refreshToken.getAdminUser().getId());
            throw new BusinessException("Refresh token expirado o revocado", "AUTH_REFRESH_EXPIRED");
        }

        AdminUser user = refreshToken.getAdminUser();
        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String username) {
        adminUserRepository.findByUsername(username).ifPresent(user ->
                refreshTokenRepository.revokeAllByUserId(user.getId()));
        log.info("Logout exitoso para usuario: {}", username);
    }

    private AuthResponse buildAuthResponse(AdminUser user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = jwtService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .username(user.getUsername())
                .nombreCompleto(user.getNombreCompleto())
                .role(user.getRole())
                .build();
    }
}
