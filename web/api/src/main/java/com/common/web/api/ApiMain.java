package com.common.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Slf4j
@EnableCaching(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.common"}, exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ApiMain {
    @Value("${security.user.name}")
    private String propertyHello;
    @Value("${core.env}")
    private String env;

    public static String parseArguments(String[] args) {
        String env = "local";
        if (args.length > 0) {
            switch (args[0]) {
                case "dev":
                    env = "dev";
                    break;
                case "prod":
                    env = "prod";
                    break;
            }
        }

        return env;
    }

    public static void main(String[] args) {
        String env = parseArguments(args);

        new SpringApplicationBuilder(ApiMain.class)
                .properties(
                        String.format("spring.config.location:classpath:common-%s.properties,classpath:core-%s.yaml", env, env)
                )
                .build()
                .run(args);
        log.info("Start ApiMain");
    }

    @Bean
    public CommandLineRunner runner() {
        return (a) -> {
            log.info("CommandLineRunner: " + propertyHello + " : " + env);
        };
    }
}
