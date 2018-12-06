package com.common.web.common.config;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class CommonWebConfig extends WebMvcConfigurerAdapter {
    /**
     * Logback의 [%X{req.xForwardedFor}] [%X{req.remoteHost}] [%X{req.requestURI}] 사용
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MDCInsertingServletFilter());
        registration.addUrlPatterns("/*");
        registration.setName("MDCInsertingServletFilter");
        registration.setOrder(1);
        return registration;
    }
}
