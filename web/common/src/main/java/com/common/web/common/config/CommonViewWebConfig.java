package com.common.web.common.config;

import com.common.web.common.handler.FreemarkerExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.servlet.MultipartConfigElement;

public class CommonViewWebConfig implements WebMvcConfigurer {
    /**
     * Freemarker 설정
     *
     * @return
     * @throws Exception
     */
    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(false);
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setPrefix("/views/ftl/");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html; charset=UTF-8");
        return resolver;
    }

    /**
     * Freemarker 에러 발생시 설정
     *
     * @return
     * @throws Exception
     */
    @Bean
    public FreeMarkerConfigurer freemarkerConfig() throws Exception {
        FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactoryBean();
        factory.setTemplateLoaderPaths("classpath:templates", "classpath:/org/springframework/web/servlet/view/freemarker");
        factory.setDefaultEncoding("UTF-8");

        //freemarker 에서 문법 에러나 오류 발생시 핸들링 하기위한 클래스 등록
        freemarker.template.Configuration configuration = factory.createConfiguration();
        configuration.setTemplateExceptionHandler(freemarkerExceptionHandler());

        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setConfiguration(configuration);
        return configurer;
    }

    @Bean
    public FreemarkerExceptionHandler freemarkerExceptionHandler() {
        return new FreemarkerExceptionHandler();
    }

    /**
     * 파일 업로드 설정 (1)
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        org.springframework.boot.web.servlet.MultipartConfigFactory factory = new org.springframework.boot.web.servlet.MultipartConfigFactory();
        factory.setMaxFileSize(-1);
//		factory.setMaxRequestSize(getMaxUploadFileSize());
        return factory.createMultipartConfig();
    }

    /**
     * 파일 업로드 설정 (2)
     *
     * @return
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
