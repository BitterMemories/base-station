package com.ixecloud.position.baselocation.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DBConfig {

    @Autowired(required = false)
    private MBeanExporter mBeanExporter;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig catalogDbConfig() {
        return new HikariConfig();
    }


    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        if (mBeanExporter != null) {
            mBeanExporter.addExcludedBean("dataSource");
        }
        HikariConfig hikariConfig = this.catalogDbConfig();
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name = "transactionManager")
    public DataSourceTransactionManager transactionManager(
            @Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
