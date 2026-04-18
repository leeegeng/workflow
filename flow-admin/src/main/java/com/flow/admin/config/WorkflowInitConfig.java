package com.flow.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * 工作流初始化配置
 * 应用启动时自动部署预定义的流程
 */
@Slf4j
@Configuration
public class WorkflowInitConfig implements CommandLineRunner {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化工作流流程...");

        // 部署简单请假流程
        deployLeaveProcess();

        log.info("工作流流程初始化完成");
    }

    /**
     * 部署请假流程
     */
    private void deployLeaveProcess() {
        String processKey = "leave-simple";
        String resourceName = "processes/leave-simple.bpmn20.xml";

        try {
            // 检查流程是否已部署
            ProcessDefinition existingDef = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processKey)
                    .latestVersion()
                    .singleResult();

            if (existingDef != null) {
                log.info("流程 [{}] 已部署，版本: {}", processKey, existingDef.getVersion());
                return;
            }

            // 读取BPMN文件
            ClassPathResource resource = new ClassPathResource(resourceName);
            if (!resource.exists()) {
                log.warn("流程定义文件不存在: {}", resourceName);
                return;
            }

            // 部署流程
            try (InputStream inputStream = resource.getInputStream()) {
                Deployment deployment = repositoryService.createDeployment()
                        .name("简单请假流程")
                        .key(processKey)
                        .addInputStream("leave-simple.bpmn20.xml", inputStream)
                        .deploy();

                log.info("流程 [{}] 部署成功，部署ID: {}", processKey, deployment.getId());

                // 获取部署后的流程定义
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deployment.getId())
                        .singleResult();

                if (processDefinition != null) {
                    log.info("流程定义信息: ID={}, Key={}, Version={}",
                            processDefinition.getId(),
                            processDefinition.getKey(),
                            processDefinition.getVersion());
                }
            }
        } catch (Exception e) {
            log.error("部署流程 [{}] 失败: {}", processKey, e.getMessage(), e);
        }
    }
}
