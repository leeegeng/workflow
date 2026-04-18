package com.flow.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理后台启动类
 */
@SpringBootApplication(scanBasePackages = {"com.flow"})
@MapperScan("com.flow.admin.mapper")
public class FlowAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowAdminApplication.class, args);
    }

}
