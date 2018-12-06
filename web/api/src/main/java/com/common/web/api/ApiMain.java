package com.common.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Slf4j
@EnableCaching(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.common"})
public class ApiMain {
    public static void main(String[] args) {
        SpringApplication.run(ApiMain.class, args);
        log.info("Start ApiMain");
    }
}
