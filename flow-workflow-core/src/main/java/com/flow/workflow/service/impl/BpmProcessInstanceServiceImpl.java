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
        // 检查流程定义是否存??
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(startDTO.getProcessDefinitionKey())
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (processDefinition.isSuspended()) {
            throw new BusinessException("流程定义已挂起，无法启动");
        }

        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        if (startDTO.getVariables() != null) {
            variables.putAll(startDTO.getVariables());
        }
        if (startDTO.getFormData() != null) {
            variables.put("formData", startDTO.getFormData());
        }
        variables.put("startUserId", startDTO.getStartUserId());
        variables.put("title", startDTO.getTitle());

        // 启动流程实例
        ProcessInstance processInstance;
        if (startDTO.getBusinessKey() != null) {
            processInstance = runtimeService.startProcessInstanceByKey(
                    startDTO.getProcessDefinitionKey(),
                    startDTO.getBusinessKey(),
                    variables
            );
        } else {
            processInstance = runtimeService.startProcessInstanceByKey(
                    startDTO.getProcessDefinitionKey(),
                    variables
            );
        }

        log.info("启动流程实例成功: {}, 实例ID: {}", processDefinition.getName(), processInstance.getId());
        return Result.success(processInstance.getId());
    }

    @Override
    public Result<List<ProcessInstanceVO>> getMyStartedProcessInstances(Long userId) {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .startedBy(String.valueOf(userId))
                .orderByProcessInstanceStartTime()
                .desc()
                .list();

        List<ProcessInstanceVO> voList = historicProcessInstances.stream()
                .map(this::convertToVO)
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
     * 转换为VO
     */
    private ProcessInstanceVO convertToVO(HistoricProcessInstance historicProcessInstance) {
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

        // 获取流程变量
        Map<String, Object> processVariables = historicProcessInstance.getProcessVariables();
        if (processVariables != null) {
            vo.setTitle((String) processVariables.get("title"));
            Object startUserId = processVariables.get("startUserId");
            if (startUserId != null) {
                vo.setStartUserId(Long.valueOf(startUserId.toString()));
            }
            vo.setFormData((Map<String, Object>) processVariables.get("formData"));
        }

        return vo;
    }
}
