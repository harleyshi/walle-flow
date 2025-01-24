package com.walle.engine.configuration;

import com.walle.engine.DAGEngineSpringInitializer;
import com.walle.engine.EngineManager;
import com.walle.engine.MySQLEngineManager;
import com.walle.engine.loader.EngineLoader;
import com.walle.engine.loader.MySQLEngineLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
@Configuration
@EnableConfigurationProperties({WalleFlowProperties.class})
public class WalleEngineAutoConfiguration {

    @Bean("walleFlowDataSource")
    public DataSource walleFlowDataSource(WalleFlowProperties properties) {
        String storeType = properties.getStoreType();
        try {
            if(StringUtils.isBlank(storeType)){
                throw new RuntimeException("storeType property cannot be null");
            }
            if(storeType.equalsIgnoreCase("mysql")){
                WalleFlowProperties.MySQLConfig mySQLConfig = properties.getMysqlConfig();
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setUrl(mySQLConfig.getUrl());
                dataSource.setUsername(mySQLConfig.getUsername());
                dataSource.setPassword(mySQLConfig.getPassword());
                dataSource.init();
                return dataSource;
            }
            throw new RuntimeException("storeType is not supported");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "walle.engine.store-type", havingValue = "mysql")
    public EngineLoader engineLoader(@Qualifier("walleFlowDataSource") DataSource dataSource) {
        return new MySQLEngineLoader(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "walle.engine.store-type", havingValue = "mysql")
    public EngineManager engineManager(EngineLoader engineLoader) {
        return new MySQLEngineManager(engineLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    public DAGEngineSpringInitializer dagEngineSpringInitializer(EngineManager engineManager) {
        return new DAGEngineSpringInitializer(engineManager);
    }
}