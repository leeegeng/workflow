package com.flow.workflow.service.impl;

import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import com.flow.workflow.dto.BpmModelDTO;
import com.flow.workflow.service.BpmModelService;
import com.flow.workflow.vo.BpmModelVO;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程模型Service实现
 */
@Slf4j
@Service
public class BpmModelServiceImpl implements BpmModelService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Result<List<BpmModelVO>> listModels() {
        ModelQuery query = repositoryService.createModelQuery();
        List<Model> models = query.orderByCreateTime().desc().list();

        List<BpmModelVO> voList = models.stream().map(this::convertToVO).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<BpmModelVO> getModelById(String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }
        return Result.success(convertToVO(model));
    }

    @Override
    public Result<String> createModel(BpmModelDTO modelDTO) {
        // 检查模型标识是否已存在
        Model existModel = repositoryService.createModelQuery()
                .modelKey(modelDTO.getModelKey())
                .singleResult();
        if (existModel != null) {
            throw new BusinessException("模型标识已存在");
        }

        // 创建模型
        Model model = repositoryService.newModel();
        model.setName(modelDTO.getModelName());
        model.setKey(modelDTO.getModelKey());
        model.setCategory(modelDTO.getCategory());

        // 设置元数据
        String metaInfo = String.format("{\"description\":\"%s\",\"formId\":%s}",
                modelDTO.getDescription() != null ? modelDTO.getDescription() : "",
                modelDTO.getFormId() != null ? modelDTO.getFormId() : "null");
        model.setMetaInfo(metaInfo);

        repositoryService.saveModel(model);

        log.info("创建流程模型成功: {}", modelDTO.getModelName());
        return Result.success(model.getId());
    }

    @Override
    public Result<Void> updateModel(String modelId, BpmModelDTO modelDTO) {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        model.setName(modelDTO.getModelName());
        model.setCategory(modelDTO.getCategory());

        String metaInfo = String.format("{\"description\":\"%s\",\"formId\":%s}",
                modelDTO.getDescription() != null ? modelDTO.getDescription() : "",
                modelDTO.getFormId() != null ? modelDTO.getFormId() : "null");
        model.setMetaInfo(metaInfo);

        repositoryService.saveModel(model);

        log.info("更新流程模型成功: {}", modelDTO.getModelName());
        return Result.success();
    }

    @Override
    public Result<Void> deleteModel(String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        repositoryService.deleteModel(modelId);

        log.info("删除流程模型成功: {}", model.getName());
        return Result.success();
    }

    @Override
    public Result<Void> deployModel(String modelId) {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        // 获取BPMN XML
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        if (bpmnBytes == null) {
            throw new BusinessException("模型BPMN内容为空，无法部署");
        }

        String bpmnXml = new String(bpmnBytes, StandardCharsets.UTF_8);

        // 部署流程
        Deployment deployment = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(model.getCategory())
                .addString(model.getKey() + ".bpmn20.xml", bpmnXml)
                .deploy();

        log.info("部署流程模型成功: {}, 部署ID: {}", model.getName(), deployment.getId());
        return Result.success();
    }

    @Override
    public Result<String> getModelBpmnXml(String modelId) {
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        if (bpmnBytes == null) {
            return Result.success("");
        }
        String bpmnXml = new String(bpmnBytes, StandardCharsets.UTF_8);
        return Result.success(bpmnXml);
    }

    @Override
    public Result<Void> saveModelBpmnXml(String modelId, String bpmnXml) {
        Model model = repositoryService.getModel(modelId);
        if (model == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        repositoryService.addModelEditorSource(modelId, bpmnXml.getBytes(StandardCharsets.UTF_8));

        log.info("保存流程模型BPMN成功: {}", model.getName());
        return Result.success();
    }

    /**
     * 转换为VO
     */
    private BpmModelVO convertToVO(Model model) {
        BpmModelVO vo = new BpmModelVO();
        vo.setId(model.getId());
        vo.setModelName(model.getName());
        vo.setModelKey(model.getKey());
        vo.setCategory(model.getCategory());
        vo.setVersion(model.getVersion());

        // 解析metaInfo
        String metaInfo = model.getMetaInfo();
        if (metaInfo != null) {
            // 简单解析description
            int descStart = metaInfo.indexOf("\"description\":\"");
            if (descStart != -1) {
                descStart += "\"description\":\"".length();
                int descEnd = metaInfo.indexOf("\"", descStart);
                if (descEnd != -1) {
                    vo.setDescription(metaInfo.substring(descStart, descEnd));
                }
            }
        }

        // 检查是否已部署
        long deploymentCount = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(model.getKey())
                .count();
        vo.setStatus(deploymentCount > 0 ? 1 : 0);

        return vo;
    }
}
