package com.flow.common.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus Spring Boot 3.2+ 兼容配置
 * 解决 factoryBeanObjectType 类型错误
 */
@Configuration
public class MyBatisPlusSpringBoot32Config {

    /**
     * 配置 Mapper 扫描
     */
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.flow.**.mapper");
        return configurer;
    }
}
