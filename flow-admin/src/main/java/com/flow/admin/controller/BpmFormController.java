package com.flow.admin.controller;

import com.flow.admin.dto.BpmFormDTO;
import com.flow.admin.service.BpmFormService;
import com.flow.admin.vo.BpmFormVO;
import com.flow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单管理控制器
 */
@RestController
@RequestMapping("/workflow/form")
public class BpmFormController {

    @Autowired
    private BpmFormService formService;

    /**
     * 获取表单列表
     */
    @GetMapping("/list")
    public Result<List<BpmFormVO>> list() {
        return formService.listForms();
    }

    /**
     * 获取表单详情
     */
    @GetMapping("/{id}")
    public Result<BpmFormVO> getById(@PathVariable Long id) {
        return formService.getFormById(id);
    }

    /**
     * 根据表单标识获取表单
     */
    @GetMapping("/key/{formKey}")
    public Result<BpmFormVO> getByKey(@PathVariable String formKey) {
        return formService.getFormByKey(formKey);
    }

    /**
     * 创建表单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:form:add')")
    public Result<Long> add(@Validated @RequestBody BpmFormDTO formDTO) {
        return formService.createForm(formDTO);
    }

    /**
     * 更新表单
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:form:edit')")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody BpmFormDTO formDTO) {
        formDTO.setId(id);
        return formService.updateForm(formDTO);
    }

    /**
     * 删除表单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:form:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return formService.deleteForm(id);
    }

    /**
     * 获取表单渲染数据
     */
    @GetMapping("/{id}/render")
    public Result<Map<String, Object>> getRenderData(@PathVariable Long id) {
        return formService.getFormRenderData(id);
    }

    /**
     * 验证表单数据
     */
    @PostMapping("/{id}/validate")
    public Result<Void> validate(@PathVariable Long id, @RequestBody Map<String, Object> formData) {
        return formService.validateFormData(id, formData);
    }

}
