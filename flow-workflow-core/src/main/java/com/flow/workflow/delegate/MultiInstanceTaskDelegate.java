package com.flow.workflow.delegate;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 会签任务委托??
 * 用于处理会签任务的完成条件判??
 */
@Slf4j
@Component
public class MultiInstanceTaskDelegate implements JavaDelegate {

    /**
     * 会签通过比例（默??00%，即全部通过??
     */
    private static final String PASS_RATE = "passRate";

    /**
     * 会签结果计数
     */
    private static final String APPROVE_COUNT = "approveCount";
    private static final String REJECT_COUNT = "rejectCount";

    @Override
    public void execute(DelegateExecution execution) {
        log.info("执行会签任务委托, 流程实例ID: {}", execution.getProcessInstanceId());

        // 获取会签结果
        Integer approveCount = (Integer) execution.getVariable(APPROVE_COUNT);
        Integer rejectCount = (Integer) execution.getVariable(REJECT_COUNT);

        if (approveCount == null) approveCount = 0;
        if (rejectCount == null) rejectCount = 0;

        // 获取会签人员总数
        Collection<String> assigneeList = (Collection<String>) execution.getVariable("assigneeList");
        int total = assigneeList != null ? assigneeList.size() : 0;

        // 获取通过比例
        Double passRate = (Double) execution.getVariable(PASS_RATE);
        if (passRate == null) {
            passRate = 1.0; // 默认100%
        }

        // 计算所需通过人数
        int requiredPassCount = (int) Math.ceil(total * passRate);

        log.info("会签统计: 通过={}, 拒绝={}, 总数={}, 需要通过={}",
                approveCount, rejectCount, total, requiredPassCount);

        // 判断是否通过
        boolean passed = approveCount >= requiredPassCount;
        boolean rejected = rejectCount > (total - requiredPassCount);

        // 设置会签结果
        execution.setVariable("multiInstanceResult", passed ? "pass" : (rejected ? "reject" : "pending"));
        execution.setVariable("multiInstancePassed", passed);
        execution.setVariable("multiInstanceRejected", rejected);
    }

}
