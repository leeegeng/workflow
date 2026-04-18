package com.flow.workflow.service.impl;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmMonitorService;
import com.flow.workflow.vo.ProcessMonitorVO;
import com.flow.workflow.vo.ProcessStatisticsVO;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BpmMonitorServiceImpl implements BpmMonitorService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ManagementService managementService;

    @Override
    public Result<List<ProcessMonitorVO>> getProcessMonitorList() {
        return Result.success(List.of());
    }

    @Override
    public Result<ProcessMonitorVO> getProcessInstanceDetail(String processInstanceId) {
        return Result.success(null);
    }

    @Override
    public Result<ProcessStatisticsVO> getProcessStatistics() {
        ProcessStatisticsVO vo = new ProcessStatisticsVO();
        vo.setRunningCount(runtimeService.createProcessInstanceQuery().count());
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
