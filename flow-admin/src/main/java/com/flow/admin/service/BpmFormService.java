package com.flow.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flow.admin.dto.BpmFormDTO;
import com.flow.admin.entity.BpmForm;
import com.flow.admin.vo.BpmFormVO;
import com.flow.common.result.Result;

import java.util.List;
import java.util.Map;

/**
 * 表单Service接口
 */
public interface BpmFormService extends IService<BpmForm> {

    /**
     * 获取表单列表
     */
    Result<List<BpmFormVO>> listForms();

    /**
     * 获取表单详情
     */
    Result<BpmFormVO> getFormById(Long id);

    /**
     * 根据表单标识获取表单
     */
    Result<BpmFormVO> getFormByKey(String formKey);

    /**
     * 创建表单
     */
    Result<Long> createForm(BpmFormDTO formDTO);

    /**
     * 更新表单
     */
    Result<Void> updateForm(BpmFormDTO formDTO);

    /**
     * 删除表单
     */
    Result<Void> deleteForm(Long id);

    /**
     * 获取表单渲染数据（用于前端渲染表单）
     */
    Result<Map<String, Object>> getFormRenderData(Long formId);

    /**
     * 验证表单数据
     */
    Result<Void> validateFormData(Long formId, Map<String, Object> formData);

}
