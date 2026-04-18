package com.flow.workflow.service.impl;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BpmReportServiceImpl implements BpmReportService {

    @Override
    public Result<List<Map<String, Object>>> getProcessStartStatistics(String startTime, String endTime) {
        return Result.success(List.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getTaskEfficiencyStatistics(String startTime, String endTime) {
        return Result.success(List.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getPersonalWorkloadStatistics(String startTime, String endTime) {
        return Result.success(List.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getProcessDurationStatistics(String startTime, String endTime) {
        return Result.success(List.of());
    }

    @Override
    public Result<Map<String, Object>> getProcessStatusDistribution() {
        return Result.success(Map.of());
    }

    @Override
    public Result<List<Map<String, Object>>> getMonthlyProcessTrend(int months) {
        return Result.success(List.of());
    }
}