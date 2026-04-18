package com.flow.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flow.admin.dto.BpmFormDTO;
import com.flow.admin.entity.BpmForm;
import com.flow.admin.mapper.BpmFormMapper;
import com.flow.admin.service.BpmFormService;
import com.flow.admin.vo.BpmFormVO;
import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表单Service实现
 */
@Slf4j
@Service
public class BpmFormServiceImpl extends ServiceImpl<BpmFormMapper, BpmForm> implements BpmFormService {

    @Override
    public Result<List<BpmFormVO>> listForms() {
        List<BpmForm> formList = list();
        List<BpmFormVO> voList = formList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<BpmFormVO> getFormById(Long id) {
        BpmForm form = getById(id);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        return Result.success(convertToVO(form));
    }

    @Override
    public Result<BpmFormVO> getFormByKey(String formKey) {
        BpmForm form = baseMapper.selectByFormKey(formKey);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        return Result.success(convertToVO(form));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> createForm(BpmFormDTO formDTO) {
        // 检查表单标识是否已存在
        BpmForm existForm = baseMapper.selectByFormKey(formDTO.getFormKey());
        if (existForm != null) {
            throw new BusinessException("表单标识已存在");
        }

        BpmForm form = new BpmForm();
        BeanUtils.copyProperties(formDTO, form);

        save(form);
        log.info("创建表单成功: {}", form.getFormName());
        return Result.success(form.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateForm(BpmFormDTO formDTO) {
        BpmForm form = getById(formDTO.getId());
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        // 如果修改了formKey，检查是否与其他表单冲突
        if (!form.getFormKey().equals(formDTO.getFormKey())) {
            BpmForm existForm = baseMapper.selectByFormKey(formDTO.getFormKey());
            if (existForm != null) {
                throw new BusinessException("表单标识已存在");
            }
        }

        BeanUtils.copyProperties(formDTO, form);
        updateById(form);

        log.info("更新表单成功: {}", form.getFormName());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteForm(Long id) {
        BpmForm form = getById(id);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        removeById(id);
        log.info("删除表单成功: {}", form.getFormName());
        return Result.success();
    }

    @Override
    public Result<Map<String, Object>> getFormRenderData(Long formId) {
        BpmForm form = getById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        Map<String, Object> renderData = new HashMap<>();
        renderData.put("formId", form.getId());
        renderData.put("formName", form.getFormName());
        renderData.put("formKey", form.getFormKey());

        // 解析表单JSON配置
        if (StringUtils.hasText(form.getFormJson())) {
            try {
                JSONObject formJson = JSON.parseObject(form.getFormJson());
                renderData.put("layout", formJson.get("layout"));
                renderData.put("config", formJson.get("config"));
            } catch (Exception e) {
                log.error("解析表单JSON失败: {}", e.getMessage());
            }
        }

        return Result.success(renderData);
    }

    @Override
    public Result<Void> validateFormData(Long formId, Map<String, Object> formData) {
        BpmForm form = getById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        // 解析表单配置进行校验
        if (StringUtils.hasText(form.getFormJson())) {
            try {
                JSONObject formJson = JSON.parseObject(form.getFormJson());
                JSONArray fields = formJson.getJSONArray("fields");

                if (fields != null) {
                    for (int i = 0; i < fields.size(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        String fieldName = field.getString("field");
                        Boolean required = field.getBoolean("required");

                        if (Boolean.TRUE.equals(required)) {
                            Object value = formData.get(fieldName);
                            if (value == null || (value instanceof String && !StringUtils.hasText((String) value))) {
                                String label = field.getString("label");
                                throw new BusinessException(label + "不能为空");
                            }
                        }
                    }
                }
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("验证表单数据失败: {}", e.getMessage());
            }
        }

        return Result.success();
    }

    /**
     * 转换为VO
     */
    private BpmFormVO convertToVO(BpmForm form) {
        BpmFormVO vo = new BpmFormVO();
        BeanUtils.copyProperties(form, vo);

        // 解析表单JSON
        if (StringUtils.hasText(form.getFormJson())) {
            try {
                JSONObject formJson = JSON.parseObject(form.getFormJson());
                JSONArray fields = formJson.getJSONArray("fields");
                if (fields != null) {
                    List<BpmFormVO.FormFieldVO> fieldVOList = new ArrayList<>();
                    for (int i = 0; i < fields.size(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        BpmFormVO.FormFieldVO fieldVO = new BpmFormVO.FormFieldVO();
                        fieldVO.setField(field.getString("field"));
                        fieldVO.setLabel(field.getString("label"));
                        fieldVO.setType(field.getString("type"));
                        fieldVO.setRequired(field.getBoolean("required"));
                        fieldVO.setDefaultValue(field.get("defaultValue"));
                        fieldVOList.add(fieldVO);
                    }
                    vo.setFields(fieldVOList);
                }
            } catch (Exception e) {
                log.error("解析表单JSON失败: {}", e.getMessage());
            }
        }

        return vo;
    }
}
