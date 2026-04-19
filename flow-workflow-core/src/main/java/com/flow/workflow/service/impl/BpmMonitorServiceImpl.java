package com.flow.workflow.service.impl;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmMonitorService;
import com.flow.workflow.vo.ProcessMonitorVO;
import com.flow.workflow.vo.ProcessStatisticsVO;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmMonitorServiceImpl implements BpmMonitorService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ManagementService managementService;

    @Override
    public Result<List<ProcessMonitorVO>> getProcessMonitorList() {
        // 查询所有运行中的流程实例
        List<ProcessInstance> runningInstances = runtimeService.createProcessInstanceQuery()
                .orderByStartTime()
                .desc()
                .list();

        // 查询已完成的流程实例（最近100条）
        List<HistoricProcessInstance> completedInstances = historyService.createHistoricProcessInstanceQuery()
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .listPage(0, 100);

        // 合并并转换为VO
        List<ProcessMonitorVO> voList = runningInstances.stream()
                .map(this::convertRunningToVO)
                .collect(Collectors.toList());

        voList.addAll(completedInstances.stream()
                .map(this::convertHistoricToVO)
                .collect(Collectors.toList()));

        return Result.success(voList);
    }

    /**
     * 转换运行中的流程实例为VO
     */
    private ProcessMonitorVO convertRunningToVO(ProcessInstance instance) {
        ProcessMonitorVO vo = new ProcessMonitorVO();
        vo.setProcessInstanceId(instance.getId());
        vo.setProcessDefinitionId(instance.getProcessDefinitionId());
        vo.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        vo.setStartTime(instance.getStartTime() != null ?
                instance.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setStatus(1);
        vo.setStatusName("运行中");

        // 获取流程定义名称
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(instance.getProcessDefinitionId())
                .singleResult();
        if (definition != null) {
            vo.setProcessDefinitionName(definition.getName());
        }

        // 查询流程变量
        loadVariables(instance.getId(), vo);

        // 获取当前节点
        String currentActivityId = runtimeService.createExecutionQuery()
                .executionId(instance.getId())
                .singleResult()
                .getActivityId();
        if (currentActivityId != null) {
            vo.setCurrentNodeName(currentActivityId);
        }

        // 计算持续时间
        if (vo.getStartTime() != null) {
            vo.setDuration(System.currentTimeMillis() - instance.getStartTime().getTime());
            vo.setDurationFormat(formatDuration(vo.getDuration()));
        }

        return vo;
    }

    /**
     * 转换历史流程实例为VO
     */
    private ProcessMonitorVO convertHistoricToVO(HistoricProcessInstance instance) {
        ProcessMonitorVO vo = new ProcessMonitorVO();
        vo.setProcessInstanceId(instance.getId());
        vo.setProcessDefinitionId(instance.getProcessDefinitionId());
        vo.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        vo.setStartTime(instance.getStartTime() != null ?
                instance.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setEndTime(instance.getEndTime() != null ?
                instance.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setDuration(instance.getDurationInMillis());
        vo.setDurationFormat(formatDuration(instance.getDurationInMillis()));
        vo.setStatus(2);
        vo.setStatusName("已完成");

        // 获取流程定义名称
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(instance.getProcessDefinitionId())
                .singleResult();
        if (definition != null) {
            vo.setProcessDefinitionName(definition.getName());
        }

        // 查询流程变量
        loadVariables(instance.getId(), vo);

        return vo;
    }

    /**
     * 加载流程变量
     */
    private void loadVariables(String processInstanceId, ProcessMonitorVO vo) {
        historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list()
                .forEach(var -> {
                    switch (var.getVariableName()) {
                        case "title":
                            vo.setTitle((String) var.getValue());
                            break;
                        case "startUserName":
                            vo.setStartUserName((String) var.getValue());
                            break;
                        case "startUserId":
                            if (var.getValue() != null) {
                                vo.setStartUserId(Long.valueOf(var.getValue().toString()));
                            }
                            break;
                    }
                });
    }

    /**
     * 格式化持续时间
     */
    private String formatDuration(Long millis) {
        if (millis == null) return "-";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "天" + (hours % 24) + "小时";
        if (hours > 0) return hours + "小时" + (minutes % 60) + "分";
        if (minutes > 0) return minutes + "分" + (seconds % 60) + "秒";
        return seconds + "秒";
    }

    @Override
    public Result<ProcessMonitorVO> getProcessInstanceDetail(String processInstanceId) {
        return Result.success(null);
    }

    @Override
    public Result<ProcessStatisticsVO> getProcessStatistics() {
        ProcessStatisticsVO vo = new ProcessStatisticsVO();
        
        // 流程定义数量
        vo.setProcessDefinitionCount(repositoryService.createProcessDefinitionQuery().count());
        
        // 运行中的流程实例
        vo.setRunningCount(runtimeService.createProcessInstanceQuery().count());
        
        // 已完成的流程实例
        vo.setCompletedCount(historyService.createHistoricProcessInstanceQuery().finished().count());
        
        // 待办任务数量
        vo.setTodoTaskCount(taskService.createTaskQuery().count());
        
        // 今日发起的流程
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        vo.setTodayStartCount(historyService.createHistoricProcessInstanceQuery()
                .startedAfter(java.util.Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant()))
                .count());
        
        // 平均耗时（已完成的流程）
        List<HistoricProcessInstance> completedInstances = historyService.createHistoricProcessInstanceQuery()
                .finished()
                .list();
        if (!completedInstances.isEmpty()) {
            long totalDuration = completedInstances.stream()
                    .mapToLong(HistoricProcessInstance::getDurationInMillis)
                    .sum();
            long avgDuration = totalDuration / completedInstances.size();
            vo.setAverageDuration(avgDuration);
            vo.setAverageDurationFormat(formatDuration(avgDuration));
        }
        
        return Result.success(vo);
    }

    @Override
    public Result<Map<String, Object>> getProcessHighlightData(String processInstanceId) {
        return Result.success(Map.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getProcessHistory(String processInstanceId) {
        return Result.success(List.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getNodeDurationStatistics(String processDefinitionKey) {
        return Result.success(List.of());
    }

    @Override
    public Result<Void> forceTerminateProcessInstance(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        return Result.success();
    }

    @Override
    public Result<Void> batchTerminateProcessInstances(List<String> processInstanceIds, String reason) {
        for (String id : processInstanceIds) {
            runtimeService.deleteProcessInstance(id, reason);
        }
        return Result.success();
    }
}
