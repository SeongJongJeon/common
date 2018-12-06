package com.common.web.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .apiListingReferenceOrdering(new Ordering<ApiListingReference>() {
                    @Override
                    public int compare(ApiListingReference left, ApiListingReference right) {
                        return 0;
                    }
                })
                .apiInfo(apiInfo())
                .ignoredParameterTypes(ApiIgnore.class, JsonIgnore.class)
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.common"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Official java implementation of the common.")
                .version("1.0")
                .build();
    }
}
