package com.mas.co.config;

import jakarta.servlet.Filter;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrutsConfig {

  @Bean
  public FilterRegistrationBean<Filter> strutsFilter() {
    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
    registration.setFilter((Filter) new StrutsPrepareAndExecuteFilter());
    registration.addUrlPatterns("/struts/*", "/admin/*");
    registration.setName("struts2");
    registration.setOrder(1);
    return registration;
  }
}
