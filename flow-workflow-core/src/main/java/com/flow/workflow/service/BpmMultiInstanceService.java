package com.flow.workflow.service;

import com.flow.common.result.Result;

import java.util.List;
import java.util.Map;

/**
 * 会签服务接口
 */
public interface BpmMultiInstanceService {

    /**
     * 创建并行会签任务
     *
     * @param processInstanceId 流程实例ID
     * @param taskKey          任务标识
     * @param assigneeList     办理人列??
     * @param passRate         通过比例??-1??
     * @return 结果
     */
    Result<Void> createParallelMultiInstance(String processInstanceId,
                                             String taskKey,
                                             List<Long> assigneeList,
                                             Double passRate);

    /**
     * 创建串行会签任务
     *
     * @param processInstanceId 流程实例ID
     * @param taskKey          任务标识
     * @param assigneeList     办理人列??
     * @param passRate         通过比例??-1??
     * @return 结果
     */
    Result<Void> createSequentialMultiInstance(String processInstanceId,
                                               String taskKey,
                                               List<Long> assigneeList,
                                               Double passRate);

    /**
     * 获取会签进度
     *
     * @param processInstanceId 流程实例ID
     * @param taskKey          任务标识
     * @return 会签进度信息
     */
    Result<Map<String, Object>> getMultiInstanceProgress(String processInstanceId, String taskKey);

    /**
     * 添加会签人员
     *
     * @param processInstanceId 流程实例ID
     * @param taskKey          任务标识
     * @param assigneeId       添加的办理人ID
     * @return 结果
     */
    Result<Void> addMultiInstanceAssignee(String processInstanceId,
                                          String taskKey,
                                          Long assigneeId);

    /**
     * 移除会签人员
     *
     * @param processInstanceId 流程实例ID
     * @param taskKey          任务标识
     * @param assigneeId       移除的办理人ID
     * @return 结果
     */
    Result<Void> removeMultiInstanceAssignee(String processInstanceId,
                                             String taskKey,
                                             Long assigneeId);

}
