package com.flow.admin.controller;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 报表统计控制器
 */
@RestController
@RequestMapping("/workflow/report")
public class BpmReportController {

    @Autowired
    private BpmReportService reportService;

    /**
     * 获取流程发起统计
     */
    @GetMapping("/process-start")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<List<Map<String, Object>>> getProcessStartStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getProcessStartStatistics(startTime, endTime);
    }

    /**
     * 获取任务处理效率统计
     */
    @GetMapping("/task-efficiency")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<List<Map<String, Object>>> getTaskEfficiencyStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getTaskEfficiencyStatistics(startTime, endTime);
    }

    /**
     * 获取个人工作量统计
     */
    @GetMapping("/personal-workload")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<List<Map<String, Object>>> getPersonalWorkloadStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getPersonalWorkloadStatistics(startTime, endTime);
    }

    /**
     * 获取流程耗时统计
     */
    @GetMapping("/process-duration")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<List<Map<String, Object>>> getProcessDurationStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        return reportService.getProcessDurationStatistics(startTime, endTime);
    }

    /**
     * 获取流程状态分布
     */
    @GetMapping("/status-distribution")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<Map<String, Object>> getProcessStatusDistribution() {
        return reportService.getProcessStatusDistribution();
    }

    /**
     * 获取月度流程趋势
     */
    @GetMapping("/monthly-trend")
    @PreAuthorize("hasAuthority('workflow:report:view')")
    public Result<List<Map<String, Object>>> getMonthlyProcessTrend(
            @RequestParam(defaultValue = "6") int months) {
        return reportService.getMonthlyProcessTrend(months);
    }

}
