package com.flow.admin.controller;

import com.flow.common.result.Result;
import com.flow.workflow.dto.BpmModelDTO;
import com.flow.workflow.service.BpmModelService;
import com.flow.workflow.vo.BpmModelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程模型控制器
 */
@RestController
@RequestMapping("/workflow/model")
public class BpmModelController {

    @Autowired
    private BpmModelService modelService;

    /**
     * 获取模型列表
     */
    @GetMapping("/list")
    public Result<List<BpmModelVO>> list() {
        return modelService.listModels();
    }

    /**
     * 获取模型详情
     */
    @GetMapping("/{modelId}")
    public Result<BpmModelVO> getById(@PathVariable String modelId) {
        return modelService.getModelById(modelId);
    }

    /**
     * 创建模型
     */
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:model:add')")
    public Result<String> add(@Validated @RequestBody BpmModelDTO modelDTO) {
        return modelService.createModel(modelDTO);
    }

    /**
     * 更新模型
     */
    @PutMapping("/{modelId}")
    @PreAuthorize("hasAuthority('workflow:model:edit')")
    public Result<Void> update(@PathVariable String modelId, @Validated @RequestBody BpmModelDTO modelDTO) {
        return modelService.updateModel(modelId, modelDTO);
    }

    /**
     * 删除模型
     */
    @DeleteMapping("/{modelId}")
    @PreAuthorize("hasAuthority('workflow:model:delete')")
    public Result<Void> delete(@PathVariable String modelId) {
        return modelService.deleteModel(modelId);
    }

    /**
     * 部署模型
     */
    @PostMapping("/{modelId}/deploy")
    @PreAuthorize("hasAuthority('workflow:model:deploy')")
    public Result<Void> deploy(@PathVariable String modelId) {
        return modelService.deployModel(modelId);
    }

    /**
     * 获取模型BPMN XML
     */
    @GetMapping("/{modelId}/bpmn")
    public Result<String> getBpmnXml(@PathVariable String modelId) {
        return modelService.getModelBpmnXml(modelId);
    }

    /**
     * 保存模型BPMN XML
     */
    @PostMapping("/{modelId}/bpmn")
    @PreAuthorize("hasAuthority('workflow:model:edit')")
    public Result<Void> saveBpmnXml(@PathVariable String modelId, @RequestBody String bpmnXml) {
        return modelService.saveModelBpmnXml(modelId, bpmnXml);
    }

}
