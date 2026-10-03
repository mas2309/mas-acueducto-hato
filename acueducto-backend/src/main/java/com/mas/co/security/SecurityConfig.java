package com.mas.co.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final WebLoginFailureHandler webLoginFailureHandler;
    private final WebLoginSuccessHandler webLoginSuccessHandler;
    private final LoginRateLimitFilter loginRateLimitFilter;
    private final WebAccessDeniedHandler webAccessDeniedHandler;

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomUserDetailsService userDetailsService,
                          WebLoginFailureHandler webLoginFailureHandler,
                          WebLoginSuccessHandler webLoginSuccessHandler,
                          LoginRateLimitFilter loginRateLimitFilter,
                          WebAccessDeniedHandler webAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.webAccessDeniedHandler = webAccessDeniedHandler;
        this.webLoginFailureHandler = webLoginFailureHandler;
        this.webLoginSuccessHandler = webLoginSuccessHandler;
        this.loginRateLimitFilter = loginRateLimitFilter;
    }

    /**
     * Cadena de seguridad para la API REST (stateless con JWT).
     * Consumida por la aplicación móvil.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                .requestMatchers("/api/v1/auth/register").hasRole("ESCRITURA")
                .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/users/**").hasRole("ESCRITURA")
                .requestMatchers(HttpMethod.GET, "/api/v1/lecturas/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/lecturas/**").hasRole("ESCRITURA")
                .requestMatchers(HttpMethod.GET, "/api/v1/cuotas/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/cuotas/**").hasRole("ESCRITURA")
                .requestMatchers(HttpMethod.GET, "/api/v1/values/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/values/**").hasRole("ESCRITURA")
                .requestMatchers("/api/v1/ingresos/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/gastos/**").hasAnyRole("ESCRITURA", "LECTURA")
                .requestMatchers("/api/v1/reports/**").hasAnyRole("ESCRITURA", "LECTURA")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cadena de seguridad para la capa Web/Struts (stateful con sesión + formulario login).
     * Panel administrativo.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/login", "/logout")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/static/**").permitAll()
                // Gestion de cuentas administrativas: exclusiva de ESCRITURA (lectura incluida).
                .requestMatchers("/admin/admin-user-guardar*", "/admin/admin-user-eliminar*").hasRole("ESCRITURA")
                // Ingresos y Gastos: LECTURA conserva escritura aqui, por excepcion de negocio.
                .requestMatchers(
                    "/admin/ingreso-guardar*", "/admin/ingreso-eliminar*",
                    "/admin/gasto-guardar*", "/admin/gasto-eliminar*", "/admin/gasto-pagar*"
                ).hasAnyRole("ESCRITURA", "LECTURA")
                // Resto de acciones de escritura del panel: solo ESCRITURA.
                .requestMatchers(
                    "/admin/usuario-guardar*", "/admin/usuario-eliminar*", "/admin/usuario-desactivar*",
                    "/admin/cuota-guardar*", "/admin/cuota-eliminar*", "/admin/cuota-desactivar*", "/admin/cuota-pago*",
                    "/admin/lectura-ingresar*", "/admin/factura-pagar*", "/admin/factura-eliminar*"
                ).hasRole("ESCRITURA")
                // Resto del panel (listados, formularios de vista, detalle, exportaciones): lectura para ambos roles.
                .requestMatchers("/admin/**").hasAnyRole("ESCRITURA", "LECTURA")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(webLoginSuccessHandler)
                .failureHandler(webLoginFailureHandler)
                .permitAll()
            )
            .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(webAccessDeniedHandler));

        return http.build();
    }

    /**
     * Cadena de seguridad por defecto para recursos públicos (Swagger, actuator, etc).
     */
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/static/**",
                    "/WEB-INF/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    /**
     * Evita que Spring Boot registre los filtros de seguridad como filtros globales del servlet.
     */
    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<LoginRateLimitFilter> rateLimitFilterRegistration(LoginRateLimitFilter filter) {
        org.springframework.boot.web.servlet.FilterRegistrationBean<LoginRateLimitFilter> registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
