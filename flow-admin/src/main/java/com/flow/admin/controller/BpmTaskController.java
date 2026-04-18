package com.flow.admin.controller;

import com.flow.admin.security.SecurityUser;
import com.flow.common.result.Result;
import com.flow.workflow.dto.TaskCompleteDTO;
import com.flow.workflow.service.BpmTaskService;
import com.flow.workflow.vo.TaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务控制器
 */
@RestController
@RequestMapping("/workflow/task")
public class BpmTaskController {

    @Autowired
    private BpmTaskService taskService;

    /**
     * 获取待办任务列表
     */
    @GetMapping("/todo")
    public Result<List<TaskVO>> todo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.getTodoList(securityUser.getUserId());
    }

    /**
     * 获取已办任务列表
     */
    @GetMapping("/done")
    public Result<List<TaskVO>> done() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.getDoneList(securityUser.getUserId());
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public Result<TaskVO> getById(@PathVariable String taskId) {
        return taskService.getTaskById(taskId);
    }

    /**
     * 完成任务
     */
    @PostMapping("/complete")
    public Result<Void> complete(@Validated @RequestBody TaskCompleteDTO completeDTO) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        completeDTO.setUserId(securityUser.getUserId());

        return taskService.completeTask(completeDTO);
    }

    /**
     * 委托任务
     */
    @PostMapping("/{taskId}/delegate")
    public Result<Void> delegate(@PathVariable String taskId,
                                  @RequestParam Long delegateToUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.delegateTask(taskId, securityUser.getUserId(), delegateToUserId);
    }

    /**
     * 转办任务
     */
    @PostMapping("/{taskId}/transfer")
    public Result<Void> transfer(@PathVariable String taskId,
                                  @RequestParam Long transferToUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.transferTask(taskId, securityUser.getUserId(), transferToUserId);
    }

    /**
     * 驳回任务
     */
    @PostMapping("/{taskId}/reject")
    public Result<Void> reject(@PathVariable String taskId,
                                @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.rejectTask(taskId, securityUser.getUserId(), comment);
    }

    /**
     * 退回上一步
     */
    @PostMapping("/{taskId}/back")
    public Result<Void> back(@PathVariable String taskId,
                              @RequestParam(required = false) String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return taskService.backTask(taskId, securityUser.getUserId(), comment);
    }

}
