package com.common.web.site;

import com.common.web.common.utils.FlagUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by alex.
 * Date: 2018-12-07
 */
@Slf4j
@EnableCaching(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.common"}, exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class SiteMain {
    public static void main(String[] args) {
        String env = FlagUtil.getEnvFromArgs(args);

        new SpringApplicationBuilder(SiteMain.class)
                .properties(
                        String.format("spring.config.location:classpath:core-%s.yml", env, env)
                )
                .build()
                .run(args);
        log.info(String.format("Start SiteMain (%s)", env));
    }
}
