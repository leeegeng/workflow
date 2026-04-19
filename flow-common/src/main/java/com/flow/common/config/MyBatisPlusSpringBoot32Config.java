package com.flow.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus Spring Boot 3.2+ 兼容配置
 * 解决 factoryBeanObjectType 类型错误
 * Mapper 扫描已在 FlowAdminApplication 中配置
 */
@Configuration
public class MyBatisPlusSpringBoot32Config {
    // Mapper 扫描配置已移至 FlowAdminApplication
}
