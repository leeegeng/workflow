package com.flow.workflow.service;

import com.flow.common.result.Result;

import java.util.List;
import java.util.Map;

/**
 * 报表统计服务接口
 */
public interface BpmReportService {

    /**
     * 获取流程发起统计（按流程类型??
     */
    Result<List<Map<String, Object>>> getProcessStartStatistics(String startTime, String endTime);

    /**
     * 获取任务处理效率统计
     */
    Result<List<Map<String, Object>>> getTaskEfficiencyStatistics(String startTime, String endTime);

    /**
     * 获取个人工作量统??
     */
    Result<List<Map<String, Object>>> getPersonalWorkloadStatistics(String startTime, String endTime);

    /**
     * 获取流程耗时统计
     */
    Result<List<Map<String, Object>>> getProcessDurationStatistics(String startTime, String endTime);

    /**
     * 获取流程状态分布统??
     */
    Result<Map<String, Object>> getProcessStatusDistribution();

    /**
     * 获取月度流程趋势
     */
    Result<List<Map<String, Object>>> getMonthlyProcessTrend(int months);

}
