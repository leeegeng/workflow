package com.flow.workflow.service;

import com.flow.common.result.Result;
import com.flow.workflow.vo.ProcessMonitorVO;
import com.flow.workflow.vo.ProcessStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 流程监控服务接口
 */
public interface BpmMonitorService {

    /**
     * 获取流程监控列表
     */
    Result<List<ProcessMonitorVO>> getProcessMonitorList();

    /**
     * 获取流程实例详情（包含运行状态）
     */
    Result<ProcessMonitorVO> getProcessInstanceDetail(String processInstanceId);

    /**
     * 获取流程统计信息
     */
    Result<ProcessStatisticsVO> getProcessStatistics();

    /**
     * 获取流程图高亮数据（显示当前运行节点??
     */
    Result<Map<String, Object>> getProcessHighlightData(String processInstanceId);

    /**
     * 获取流程执行历史
     */
    Result<List<Map<String, Object>>> getProcessHistory(String processInstanceId);

    /**
     * 获取流程节点耗时统计
     */
    Result<List<Map<String, Object>>> getNodeDurationStatistics(String processDefinitionKey);

    /**
     * 强制终止流程实例
     */
    Result<Void> forceTerminateProcessInstance(String processInstanceId, String reason);

    /**
     * 批量终止流程实例
     */
    Result<Void> batchTerminateProcessInstances(List<String> processInstanceIds, String reason);

}
