package com.flow.workflow.service;

import com.flow.common.result.Result;
import com.flow.workflow.dto.ProcessStartDTO;
import com.flow.workflow.vo.ProcessInstanceVO;
import com.flow.workflow.vo.TaskVO;

import java.util.List;
import java.util.Map;

/**
 * 流程实例Service接口
 */
public interface BpmProcessInstanceService {

    /**
     * 启动流程实例
     */
    Result<String> startProcessInstance(ProcessStartDTO startDTO);

    /**
     * 获取我的发起列表
     */
    Result<List<ProcessInstanceVO>> getMyStartedProcessInstances(Long userId);

    /**
     * 获取流程实例详情
     */
    Result<ProcessInstanceVO> getProcessInstanceById(String processInstanceId);

    /**
     * 终止流程实例
     */
    Result<Void> terminateProcessInstance(String processInstanceId, String reason);

    /**
     * 删除流程实例
     */
    Result<Void> deleteProcessInstance(String processInstanceId, String reason);

    /**
     * 获取流程进度??
     */
    Result<String> getProcessProgressImage(String processInstanceId);

}
