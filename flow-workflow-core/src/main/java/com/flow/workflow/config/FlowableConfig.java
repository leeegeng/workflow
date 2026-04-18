package com.flow.workflow.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Flowable工作流配置类
 */
@Slf4j
@Configuration
public class FlowableConfig {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostConstruct
    public void init() {
        log.info("========== Flowable工作流引擎初始化完成 ==========");
        log.info("已部署流程定义数?? {}", repositoryService.createProcessDefinitionQuery().count());
        log.info("运行中的流程实例数量: {}", runtimeService.createProcessInstanceQuery().count());
        log.info("待办任务数量: {}", taskService.createTaskQuery().count());
    }

}
