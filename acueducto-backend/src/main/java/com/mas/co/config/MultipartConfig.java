package com.mas.co.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration(
            DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> registration =
                new ServletRegistrationBean<>(dispatcherServlet, "/");
        registration.setMultipartConfig(new MultipartConfigElement("",
                10485760L, 10485760L, 0));
        registration.setName("dispatcherServlet");
        return registration;
    }

    @Bean
    public DispatcherServletPath dispatcherServletPath() {
        return () -> "/";
    }
}
