package com.mas.co.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** Recursos públicos de la PWA servidos desde la raíz del sitio. */
    private static final String[] PWA_RECURSOS = {"/sw.js", "/manifest.json", "/offline.html"};

    /** Devuelve una copia para que nadie pueda modificar el arreglo original (SpotBugs MS_MUTABLE_ARRAY). */
    public static String[] pwaRecursos() {
        return PWA_RECURSOS.clone();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/", "classpath:/static/");

        // PWA: el service worker debe servirse desde la raíz para controlar todo el sitio (scope "/").
        // Sin caché HTTP para que el navegador detecte de inmediato las nuevas versiones.
        registry.addResourceHandler(PWA_RECURSOS)
                .addResourceLocations("/pwa/")
                .setCacheControl(CacheControl.noCache());
    }
}
