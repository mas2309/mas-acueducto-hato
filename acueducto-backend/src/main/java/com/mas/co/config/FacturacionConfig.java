package com.mas.co.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "acueducto.facturacion")
@Getter
@Setter
public class FacturacionConfig {

    private int diaLimitePago = 20;
}
