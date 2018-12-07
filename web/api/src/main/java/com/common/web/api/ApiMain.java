package com.common.web.api;

import com.common.web.common.utils.FlagUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Slf4j
@EnableCaching(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.common"}, exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class ApiMain {
    @Value("${core.env}")
    private String env;

    /**
     * yaml에서 설정한 컨테이너의 기본 port 변경
     */
    @Component
    public class CustomContainerConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        @Override
        public void customize(ConfigurableServletWebServerFactory factory) {
            factory.setPort(8445);
        }
    }

    /**
     * Default tomcat 설정변경 (yaml 파일에서 ssl 설정을 하면 http는 사용안됨 해서 아래와 같이 설정해야 함.)
     *
     * @return
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8090);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    public static void main(String[] args) {
        String env = FlagUtil.getEnvFromArgs(args);

        new SpringApplicationBuilder(ApiMain.class)
                .properties(
                        String.format("spring.config.location:classpath:core-%s.yml,classpath:api-%s.yml", env, env)
                )
                .build()
                .run(args);
        log.info(String.format("Start ApiMain (%s)", env));
    }
}
