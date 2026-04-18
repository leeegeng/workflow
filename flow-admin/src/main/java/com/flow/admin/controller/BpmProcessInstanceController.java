package com.flow.admin.controller;

import com.flow.admin.security.SecurityUser;
import com.flow.common.result.Result;
import com.flow.workflow.dto.ProcessStartDTO;
import com.flow.workflow.service.BpmProcessInstanceService;
import com.flow.workflow.vo.ProcessInstanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程实例控制器
 */
@RestController
@RequestMapping("/workflow/instance")
public class BpmProcessInstanceController {

    @Autowired
    private BpmProcessInstanceService processInstanceService;

    /**
     * 启动流程实例
     */
    @PostMapping("/start")
    @PreAuthorize("hasAuthority('workflow:instance:start')")
    public Result<String> start(@Validated @RequestBody ProcessStartDTO startDTO) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        startDTO.setStartUserId(securityUser.getUserId());

        return processInstanceService.startProcessInstance(startDTO);
    }

    /**
     * 获取我的发起列表
     */
    @GetMapping("/my-started")
    public Result<List<ProcessInstanceVO>> myStarted() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return processInstanceService.getMyStartedProcessInstances(securityUser.getUserId());
    }

    /**
     * 获取流程实例详情
     */
    @GetMapping("/{processInstanceId}")
    public Result<ProcessInstanceVO> getById(@PathVariable String processInstanceId) {
        return processInstanceService.getProcessInstanceById(processInstanceId);
    }

    /**
     * 终止流程实例
     */
    @PostMapping("/{processInstanceId}/terminate")
    @PreAuthorize("hasAuthority('workflow:instance:terminate')")
    public Result<Void> terminate(@PathVariable String processInstanceId,
                                   @RequestParam(required = false) String reason) {
        return processInstanceService.terminateProcessInstance(processInstanceId, reason);
    }

    /**
     * 删除流程实例
     */
    @DeleteMapping("/{processInstanceId}")
    @PreAuthorize("hasAuthority('workflow:instance:delete')")
    public Result<Void> delete(@PathVariable String processInstanceId,
                               @RequestParam(required = false) String reason) {
        return processInstanceService.deleteProcessInstance(processInstanceId, reason);
    }

}
