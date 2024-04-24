package org.fs.rallyroundbackend.config;

import org.fs.rallyroundbackend.filter.JwtAuthenticationFilter;
import org.fs.rallyroundbackend.filter.MercadoPagoAccountLinkFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for registering custom filters as beans in the Spring Security filter chain or the Spring boot's
 * Embedded Servlet Container.
 * */
@Configuration
public class FiltersConfig {

    /**
     * Creates a FilterRegistrationBean for JwtAuthenticationFilter but marks it as disabled by default.
     * This means the filter won't be registered in the Spring Boot's Embedded Servlet Container.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
    jwtAuthenticationFilterFilterRegistrationBean(JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    /**
     * Creates a FilterRegistrationBean for MercadoPagoAccountLinkFilter but marks it as disabled by default.
     * This means the filter won't be registered in the Spring Boot's Embedded Servlet Container.
     */
    @Bean
    public FilterRegistrationBean<MercadoPagoAccountLinkFilter>
    mercadoPagoAccountLinkFilterRegistrationBean(MercadoPagoAccountLinkFilter mercadoPagoAccountLinkFilter) {
        FilterRegistrationBean<MercadoPagoAccountLinkFilter> filterRegistrationBean =
                new FilterRegistrationBean<>(mercadoPagoAccountLinkFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

}
