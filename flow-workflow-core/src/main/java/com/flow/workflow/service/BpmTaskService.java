package com.flow.workflow.service;

import com.flow.common.result.Result;
import com.flow.workflow.dto.TaskCompleteDTO;
import com.flow.workflow.vo.TaskVO;

import java.util.List;

/**
 * 任务Service接口
 */
public interface BpmTaskService {

    /**
     * 获取待办任务列表
     */
    Result<List<TaskVO>> getTodoList(Long userId);

    /**
     * 获取已办任务列表
     */
    Result<List<TaskVO>> getDoneList(Long userId);

    /**
     * 获取任务详情
     */
    Result<TaskVO> getTaskById(String taskId);

    /**
     * 完成任务
     */
    Result<Void> completeTask(TaskCompleteDTO completeDTO);

    /**
     * 委托任务
     */
    Result<Void> delegateTask(String taskId, Long assigneeId, Long delegateToUserId);

    /**
     * 转办任务
     */
    Result<Void> transferTask(String taskId, Long assigneeId, Long transferToUserId);

    /**
     * 驳回任务
     */
    Result<Void> rejectTask(String taskId, Long userId, String comment);

    /**
     * 退回上一??
     */
    Result<Void> backTask(String taskId, Long userId, String comment);

    /**
     * 认领任务
     */
    Result<Void> claimTask(String taskId, Long userId);

    /**
     * 取消认领任务
     */
    Result<Void> unclaimTask(String taskId, Long userId);

}
