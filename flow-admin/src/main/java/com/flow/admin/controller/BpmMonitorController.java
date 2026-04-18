package com.flow.admin.controller;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmMonitorService;
import com.flow.workflow.vo.ProcessMonitorVO;
import com.flow.workflow.vo.ProcessStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程监控控制器
 */
@RestController
@RequestMapping("/workflow/monitor")
public class BpmMonitorController {

    @Autowired
    private BpmMonitorService monitorService;

    /**
     * 获取流程监控列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('workflow:monitor:list')")
    public Result<List<ProcessMonitorVO>> list() {
        return monitorService.getProcessMonitorList();
    }

    /**
     * 获取流程实例详情
     */
    @GetMapping("/instance/{processInstanceId}")
    @PreAuthorize("hasAuthority('workflow:monitor:detail')")
    public Result<ProcessMonitorVO> getInstanceDetail(@PathVariable String processInstanceId) {
        return monitorService.getProcessInstanceDetail(processInstanceId);
    }

    /**
     * 获取流程统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('workflow:monitor:statistics')")
    public Result<ProcessStatisticsVO> getStatistics() {
        return monitorService.getProcessStatistics();
    }

    /**
     * 获取流程图高亮数据
     */
    @GetMapping("/instance/{processInstanceId}/highlight")
    @PreAuthorize("hasAuthority('workflow:monitor:detail')")
    public Result<Map<String, Object>> getHighlightData(@PathVariable String processInstanceId) {
        return monitorService.getProcessHighlightData(processInstanceId);
    }

    /**
     * 获取流程执行历史
     */
    @GetMapping("/instance/{processInstanceId}/history")
    @PreAuthorize("hasAuthority('workflow:monitor:detail')")
    public Result<List<Map<String, Object>>> getHistory(@PathVariable String processInstanceId) {
        return monitorService.getProcessHistory(processInstanceId);
    }

    /**
     * 获取节点耗时统计
     */
    @GetMapping("/statistics/node/{processDefinitionKey}")
    @PreAuthorize("hasAuthority('workflow:monitor:statistics')")
    public Result<List<Map<String, Object>>> getNodeStatistics(@PathVariable String processDefinitionKey) {
        return monitorService.getNodeDurationStatistics(processDefinitionKey);
    }

    /**
     * 强制终止流程实例
     */
    @PostMapping("/instance/{processInstanceId}/terminate")
    @PreAuthorize("hasAuthority('workflow:monitor:terminate')")
    public Result<Void> terminate(@PathVariable String processInstanceId,
                                   @RequestParam(required = false) String reason) {
        return monitorService.forceTerminateProcessInstance(processInstanceId, reason);
    }

    /**
     * 批量终止流程实例
     */
    @PostMapping("/batch-terminate")
    @PreAuthorize("hasAuthority('workflow:monitor:terminate')")
    public Result<Void> batchTerminate(@RequestBody List<String> processInstanceIds,
                                        @RequestParam(required = false) String reason) {
        return monitorService.batchTerminateProcessInstances(processInstanceIds, reason);
    }

}
