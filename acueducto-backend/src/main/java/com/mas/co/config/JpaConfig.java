package com.mas.co.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de JPA y auditoría.
 * 
 * @author MAS Development Team
 * @version 1.0
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.mas.co.repository", "com.mas.co.security"})
@EnableTransactionManagement
public class JpaConfig {
    // Configuración adicional de JPA si es necesaria
}