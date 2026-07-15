package com.mas.co.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!adminUserRepository.existsByUsername("admin")) {
            AdminUser admin = AdminUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin123*"))
                    .nombreCompleto("Administrador Sistema")
                    .email("admin@acueducto.com")
                    .role(Role.ADMIN)
                    .build();

            adminUserRepository.save(admin);
            log.info("Usuario admin creado por defecto (usuario: admin, password: Admin123*)");
        }
    }
}
