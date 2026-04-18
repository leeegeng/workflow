package com.flow.admin.controller;

import com.flow.common.result.Result;
import com.flow.workflow.service.BpmDefinitionService;
import com.flow.workflow.vo.ProcessDefinitionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程定义控制器
 */
@RestController
@RequestMapping("/workflow/definition")
public class BpmDefinitionController {

    @Autowired
    private BpmDefinitionService definitionService;

    /**
     * 获取流程定义列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('workflow:definition:list')")
    public Result<List<ProcessDefinitionVO>> list() {
        return definitionService.listDefinitions();
    }

    /**
     * 获取流程定义详情
     */
    @GetMapping("/{definitionId}")
    @PreAuthorize("hasAuthority('workflow:definition:detail')")
    public Result<ProcessDefinitionVO> getById(@PathVariable String definitionId) {
        return definitionService.getDefinitionById(definitionId);
    }

    /**
     * 激活流程定义
     */
    @PostMapping("/{definitionId}/activate")
    @PreAuthorize("hasAuthority('workflow:definition:activate')")
    public Result<Void> activate(@PathVariable String definitionId) {
        return definitionService.activateDefinition(definitionId);
    }

    /**
     * 挂起流程定义
     */
    @PostMapping("/{definitionId}/suspend")
    @PreAuthorize("hasAuthority('workflow:definition:suspend')")
    public Result<Void> suspend(@PathVariable String definitionId) {
        return definitionService.suspendDefinition(definitionId);
    }

    /**
     * 获取流程定义XML
     */
    @GetMapping("/{definitionId}/xml")
    @PreAuthorize("hasAuthority('workflow:definition:xml')")
    public Result<String> getXml(@PathVariable String definitionId) {
        return definitionService.getDefinitionXml(definitionId);
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{definitionId}")
    @PreAuthorize("hasAuthority('workflow:definition:delete')")
    public Result<Void> delete(@PathVariable String definitionId) {
        return definitionService.deleteDefinition(definitionId);
    }

}
