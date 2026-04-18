package com.flow.workflow.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 会签任务监听??
 */
@Slf4j
@Component
public class MultiInstanceTaskListener implements TaskListener, ExecutionListener {

    private static final String APPROVE_COUNT = "approveCount";
    private static final String REJECT_COUNT = "rejectCount";
    private static final String RESULT_PREFIX = "result_";

    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();
        log.info("会签任务监听器触?? 事件: {}, 任务ID: {}", eventName, delegateTask.getId());

        if ("complete".equals(eventName)) {
            // 任务完成时，统计结果
            handleTaskComplete(delegateTask);
        } else if ("create".equals(eventName)) {
            // 任务创建时，初始化计??
            handleTaskCreate(delegateTask);
        }
    }

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        log.info("会签执行监听器触?? 事件: {}, 执行ID: {}", eventName, execution.getId());

        if ("start".equals(eventName)) {
            // 会签开始时，初始化计数??
            initCounters(execution);
        }
    }

    /**
     * 处理任务创建
     */
    private void handleTaskCreate(DelegateTask delegateTask) {
        // 可以在这里设置任务的办理??
        log.info("创建会签任务: {}", delegateTask.getName());
    }

    /**
     * 处理任务完成
     */
    private void handleTaskComplete(DelegateTask delegateTask) {
        // 获取流程实例ID，然后通过RuntimeService操作变量
        String processInstanceId = delegateTask.getProcessInstanceId();
        org.flowable.engine.RuntimeService runtimeService = org.flowable.engine.impl.context.Context.getProcessEngineConfiguration().getRuntimeService();

        // 获取任务处理结果
        Integer result = (Integer) delegateTask.getVariable("result");
        if (result == null) {
            result = 1; // 默认同意
        }

        // 获取当前计数
        Integer approveCount = (Integer) runtimeService.getVariable(processInstanceId, APPROVE_COUNT);
        Integer rejectCount = (Integer) runtimeService.getVariable(processInstanceId, REJECT_COUNT);

        if (approveCount == null) approveCount = 0;
        if (rejectCount == null) rejectCount = 0;

        // 记录当前任务的审批结??
        String taskAssignee = delegateTask.getAssignee();
        runtimeService.setVariable(processInstanceId, RESULT_PREFIX + taskAssignee, result);

        // 更新计数
        if (result == 1) {
            approveCount++;
            runtimeService.setVariable(processInstanceId, APPROVE_COUNT, approveCount);
            log.info("会签任务同意, 当前通过?? {}", approveCount);
        } else {
            rejectCount++;
            runtimeService.setVariable(processInstanceId, REJECT_COUNT, rejectCount);
            log.info("会签任务拒绝, 当前拒绝?? {}", rejectCount);
        }

        // 保存审批意见
        String comment = (String) delegateTask.getVariable("comment");
        if (comment != null) {
            runtimeService.setVariable(processInstanceId, "comment_" + taskAssignee, comment);
        }
    }

    /**
     * 初始化计数器
     */
    private void initCounters(DelegateExecution execution) {
        execution.setVariable(APPROVE_COUNT, 0);
        execution.setVariable(REJECT_COUNT, 0);
        execution.setVariable("multiInstanceResult", "pending");
        log.info("初始化会签计数器");
    }

}
