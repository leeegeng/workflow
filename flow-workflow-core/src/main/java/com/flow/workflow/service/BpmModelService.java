package com.flow.workflow.service;

import com.flow.common.result.Result;
import com.flow.workflow.dto.BpmModelDTO;
import com.flow.workflow.vo.BpmModelVO;

import java.util.List;

/**
 * 流程模型Service接口
 */
public interface BpmModelService {

    /**
     * 获取模型列表
     */
    Result<List<BpmModelVO>> listModels();

    /**
     * 获取模型详情
     */
    Result<BpmModelVO> getModelById(String modelId);

    /**
     * 创建模型
     */
    Result<String> createModel(BpmModelDTO modelDTO);

    /**
     * 更新模型
     */
    Result<Void> updateModel(String modelId, BpmModelDTO modelDTO);

    /**
     * 删除模型
     */
    Result<Void> deleteModel(String modelId);

    /**
     * 部署模型
     */
    Result<Void> deployModel(String modelId);

    /**
     * 获取模型BPMN XML
     */
    Result<String> getModelBpmnXml(String modelId);

    /**
     * 保存模型BPMN XML
     */
    Result<Void> saveModelBpmnXml(String modelId, String bpmnXml);

}
