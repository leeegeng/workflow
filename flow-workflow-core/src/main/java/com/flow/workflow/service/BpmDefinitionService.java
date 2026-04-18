package com.flow.workflow.service;

import com.flow.common.result.Result;
import com.flow.workflow.vo.ProcessDefinitionVO;

import java.util.List;

/**
 * 流程定义Service接口
 */
public interface BpmDefinitionService {

    /**
     * 获取流程定义列表
     */
    Result<List<ProcessDefinitionVO>> listDefinitions();

    /**
     * 获取流程定义详情
     */
    Result<ProcessDefinitionVO> getDefinitionById(String definitionId);

    /**
     * 激活流程定义
     */
    Result<Void> activateDefinition(String definitionId);

    /**
     * 挂起流程定义
     */
    Result<Void> suspendDefinition(String definitionId);

    /**
     * 获取流程定义XML
     */
    Result<String> getDefinitionXml(String definitionId);

    /**
     * 获取流程定义图片
     */
    Result<String> getDefinitionImage(String definitionId);

    /**
     * 删除流程定义
     */
    Result<Void> deleteDefinition(String definitionId);

}
