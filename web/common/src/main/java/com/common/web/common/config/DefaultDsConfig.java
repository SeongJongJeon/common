package com.common.web.common.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

import javax.sql.DataSource;

public class DefaultDsConfig {
    @Autowired
    protected Environment environment;

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private void setCommonDataSource(ComboPooledDataSource dataSource) throws Exception {
        dataSource.setDriverClass(environment.getRequiredProperty("db.jdbc.driver"));

        dataSource.setAcquireIncrement(Integer.valueOf(environment.getRequiredProperty("db.acquireIncrement")));
        dataSource.setMaxStatements(Integer.valueOf(environment.getRequiredProperty("db.maxStatements")));
        dataSource.setAcquireRetryAttempts(Integer.valueOf(environment.getRequiredProperty("db.acquireRetryAttempts")));
        dataSource.setMaxIdleTime(Integer.valueOf(environment.getRequiredProperty("db.maxIdleTime")));
        dataSource.setMaxConnectionAge(Integer.valueOf(environment.getRequiredProperty("db.maxConnectionAge")));
        dataSource.setCheckoutTimeout(Integer.valueOf(environment.getRequiredProperty("db.checkoutTimeout")));
        dataSource.setIdleConnectionTestPeriod(Integer.valueOf(environment.getRequiredProperty("db.idleConnectionTestPeriod")));
        dataSource.setTestConnectionOnCheckout(Boolean.valueOf(environment.getRequiredProperty("db.testConnectionOnCheckout")));
        dataSource.setPreferredTestQuery(environment.getRequiredProperty("db.preferredTestQuery"));
        dataSource.setTestConnectionOnCheckin(Boolean.valueOf(environment.getRequiredProperty("db.testConnectionOnCheckin")));
    }

    @Primary
    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(environment.getRequiredProperty("db.master.url"));
        dataSource.setUser(environment.getRequiredProperty("db.master.user"));
        dataSource.setPassword(environment.getRequiredProperty("db.master.pwd"));
        dataSource.setMinPoolSize(Integer.valueOf(environment.getRequiredProperty("db.master.minPoolSize")));
        dataSource.setMaxPoolSize(Integer.valueOf(environment.getRequiredProperty("db.master.maxPoolSize")));
        setCommonDataSource(dataSource);

        return dataSource;
    }
}
