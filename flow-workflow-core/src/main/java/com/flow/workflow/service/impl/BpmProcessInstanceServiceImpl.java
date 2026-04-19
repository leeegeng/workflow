package com.flow.workflow.service.impl;

import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import com.flow.workflow.dto.ProcessStartDTO;
import com.flow.workflow.service.BpmProcessInstanceService;
import com.flow.workflow.vo.ProcessInstanceVO;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程实例Service实现
 */
@Slf4j
@Service
public class BpmProcessInstanceServiceImpl implements BpmProcessInstanceService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Override
    public Result<String> startProcessInstance(ProcessStartDTO startDTO) {
        // 检查流程定义
        ProcessDefinition processDefinition = null;
        String definitionKey = null;
        
        // 优先使用 processDefinitionKey
        if (startDTO.getProcessDefinitionKey() != null && !startDTO.getProcessDefinitionKey().trim().isEmpty()) {
            definitionKey = startDTO.getProcessDefinitionKey();
            processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(definitionKey)
                    .latestVersion()
                    .singleResult();
        } 
        // 其次使用 processDefinitionId
        else if (startDTO.getProcessDefinitionId() != null && !startDTO.getProcessDefinitionId().trim().isEmpty()) {
            String definitionId = startDTO.getProcessDefinitionId();
            processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(definitionId)
                    .singleResult();
            if (processDefinition != null) {
                definitionKey = processDefinition.getKey();
            }
        }

        if (processDefinition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (processDefinition.isSuspended()) {
            throw new BusinessException("流程定义已挂起，无法启动");
        }

        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        if (startDTO.getFormData() != null) {
            variables.put("formData", startDTO.getFormData());
        }
        // 优先使用 DTO 中的 title，否则使用 variables 中的 title
        if (startDTO.getTitle() != null && !startDTO.getTitle().trim().isEmpty()) {
            variables.put("title", startDTO.getTitle());
        } else if (startDTO.getVariables() != null && startDTO.getVariables().get("title") != null) {
            variables.put("title", startDTO.getVariables().get("title"));
        }
        // 设置发起人ID（优先使用 DTO 中的）
        variables.put("startUserId", startDTO.getStartUserId());
        // 将其他变量合并进来（排除已设置的 key）
        if (startDTO.getVariables() != null) {
            startDTO.getVariables().forEach((key, value) -> {
                if (!variables.containsKey(key)) {
                    variables.put(key, value);
                }
            });
        }

        // 设置当前用户（用于记录发起人）
        String authenticatedUserId = String.valueOf(startDTO.getStartUserId());
        org.flowable.common.engine.impl.identity.Authentication.setAuthenticatedUserId(authenticatedUserId);

        try {
            // 启动流程实例
            ProcessInstance processInstance;
            if (startDTO.getBusinessKey() != null && !startDTO.getBusinessKey().trim().isEmpty()) {
                processInstance = runtimeService.startProcessInstanceByKey(
                        definitionKey,
                        startDTO.getBusinessKey(),
                        variables
                );
            } else {
                processInstance = runtimeService.startProcessInstanceByKey(
                        definitionKey,
                        variables
                );
            }
            log.info("启动流程实例成功: {}, 实例ID: {}", processDefinition.getName(), processInstance.getId());
            return Result.success(processInstance.getId());
        } finally {
            // 清除当前用户
            org.flowable.common.engine.impl.identity.Authentication.setAuthenticatedUserId(null);
        }
    }

    @Override
    public Result<List<ProcessInstanceVO>> getMyStartedProcessInstances(Long userId) {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .startedBy(String.valueOf(userId))
                .orderByProcessInstanceStartTime()
                .desc()
                .list();

        List<ProcessInstanceVO> voList = historicProcessInstances.stream()
                .map(hpi -> convertToVOWithVariables(hpi, userId))
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @Override
    public Result<ProcessInstanceVO> getProcessInstanceById(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (historicProcessInstance == null) {
            throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
        }

        return Result.success(convertToVO(historicProcessInstance));
    }

    @Override
    public Result<Void> terminateProcessInstance(String processInstanceId, String reason) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance == null) {
            throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
        }

        runtimeService.deleteProcessInstance(processInstanceId, reason != null ? reason : "手动终止");

        log.info("终止流程实例成功: {}, 原因: {}", processInstanceId, reason);
        return Result.success();
    }

    @Override
    public Result<Void> deleteProcessInstance(String processInstanceId, String reason) {
        // 先尝试删除运行中的流程实??
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance != null) {
            runtimeService.deleteProcessInstance(processInstanceId, reason != null ? reason : "删除流程");
        }

        // 删除历史流程实例
        historyService.deleteHistoricProcessInstance(processInstanceId);

        log.info("删除流程实例成功: {}", processInstanceId);
        return Result.success();
    }

    @Override
    public Result<String> getProcessProgressImage(String processInstanceId) {
        // TODO: 生成流程进度图（需要使用Flowable的DiagramGenerator??
        // 这里先返回空，后续可以实现流程图高亮显示当前节点
        return Result.success("");
    }

    /**
     * 转换为VO（包含变量查询）
     */
    private ProcessInstanceVO convertToVOWithVariables(HistoricProcessInstance historicProcessInstance, Long userId) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setProcessInstanceId(historicProcessInstance.getId());
        vo.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
        vo.setProcessDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
        vo.setBusinessKey(historicProcessInstance.getBusinessKey());
        vo.setStartTime(historicProcessInstance.getStartTime() != null ?
                historicProcessInstance.getStartTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setEndTime(historicProcessInstance.getEndTime() != null ?
                historicProcessInstance.getEndTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setDuration(historicProcessInstance.getDurationInMillis());
        vo.setStartUserId(userId);

        // 获取流程定义名称
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                .singleResult();
        if (processDefinition != null) {
            vo.setProcessDefinitionName(processDefinition.getName());
        }

        // 设置状??
        if (historicProcessInstance.getEndTime() != null) {
            vo.setStatus(2); // 已完??
        } else {
            vo.setStatus(1); // 运行??
        }

        // 单独查询流程变量
        try {
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(historicProcessInstance.getId())
                    .list()
                    .stream()
                    .collect(Collectors.toMap(
                            v -> v.getVariableName(),
                            v -> v.getValue() != null ? v.getValue() : ""
                    ));
            
            vo.setTitle((String) variables.get("title"));
            vo.setStartUserName((String) variables.get("startUserName"));
            vo.setFormData((Map<String, Object>) variables.get("formData"));
        } catch (Exception e) {
            log.warn("获取流程变量失败: {}", historicProcessInstance.getId(), e);
        }

        return vo;
    }

    /**
     * 转换为VO
     */
    private ProcessInstanceVO convertToVO(HistoricProcessInstance historicProcessInstance) {
        return convertToVOWithVariables(historicProcessInstance, null);
    }
}
