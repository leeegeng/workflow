package com.flow.workflow.service.impl;

import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import com.flow.workflow.service.BpmDefinitionService;
import com.flow.workflow.vo.ProcessDefinitionVO;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义Service实现
 */
@Slf4j
@Service
public class BpmDefinitionServiceImpl implements BpmDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public Result<List<ProcessDefinitionVO>> listDefinitions() {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitions = query.orderByDeploymentId().desc().list();

        List<ProcessDefinitionVO> voList = definitions.stream().map(def -> {
            ProcessDefinitionVO vo = new ProcessDefinitionVO();
            vo.setId(def.getId());
            // 如果名称为空，使用流程标识作为名称
            String name = def.getName();
            if (name == null || name.trim().isEmpty()) {
                name = def.getKey();
            }
            vo.setName(name);
            vo.setKey(def.getKey());
            vo.setVersion(def.getVersion());
            vo.setCategory(def.getCategory());
            vo.setDescription(def.getDescription());
            vo.setSuspended(def.isSuspended());
            vo.setDeploymentId(def.getDeploymentId());

            // 获取部署时间
            Deployment deployment = repositoryService.createDeploymentQuery()
                    .deploymentId(def.getDeploymentId())
                    .singleResult();
            if (deployment != null) {
                vo.setDeploymentTime(deployment.getDeploymentTime().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            }

            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @Override
    public Result<ProcessDefinitionVO> getDefinitionById(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        ProcessDefinitionVO vo = new ProcessDefinitionVO();
        vo.setId(definition.getId());
        // 如果名称为空，使用流程标识作为名称
        String name = definition.getName();
        if (name == null || name.trim().isEmpty()) {
            name = definition.getKey();
        }
        vo.setName(name);
        vo.setKey(definition.getKey());
        vo.setVersion(definition.getVersion());
        vo.setCategory(definition.getCategory());
        vo.setDescription(definition.getDescription());
        vo.setSuspended(definition.isSuspended());
        vo.setDeploymentId(definition.getDeploymentId());

        return Result.success(vo);
    }

    @Override
    public Result<Void> activateDefinition(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (!definition.isSuspended()) {
            throw new BusinessException("流程定义已经是激活状态");
        }

        repositoryService.activateProcessDefinitionById(definitionId);
        log.info("激活流程定义成功: {}", definitionId);
        return Result.success();
    }

    @Override
    public Result<Void> suspendDefinition(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (definition.isSuspended()) {
            throw new BusinessException("流程定义已经是挂起状态");
        }

        repositoryService.suspendProcessDefinitionById(definitionId);
        log.info("挂起流程定义成功: {}", definitionId);
        return Result.success();
    }

    @Override
    public Result<String> getDefinitionXml(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        try {
            byte[] xmlBytes = repositoryService.getResourceAsStream(definition.getDeploymentId(),
                    definition.getResourceName()).readAllBytes();
            return Result.success(new String(xmlBytes, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("获取流程定义XML失败", e);
            throw new BusinessException("获取流程定义XML失败");
        }
    }

    @Override
    public Result<String> getDefinitionImage(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        // 返回流程图URL，前端可以通过该URL获取图片
        String imageUrl = "/workflow/definition/" + definitionId + "/image";
        return Result.success(imageUrl);
    }

    @Override
    public Result<Void> deleteDefinition(String definitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        // 检查是否有正在运行的流程实例
        long runningInstances = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionId(definitionId)
                .count();

        if (runningInstances > 0) {
            throw new BusinessException("该流程定义存在正在运行的流程实例，无法删除");
        }

        // 删除部署（会级联删除流程定义）
        repositoryService.deleteDeployment(definition.getDeploymentId(), true);

        log.info("删除流程定义成功: {}", definitionId);
        return Result.success();
    }

}
