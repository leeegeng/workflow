package com.flow.workflow.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 组任务监听器 - 动态设置候选组
 */
@Slf4j
@Component
public class GroupTaskListener implements TaskListener {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
        String processInstanceId = delegateTask.getProcessInstanceId();
        
        // 获取流程发起人
        String startUserId = (String) delegateTask.getVariable("startUserId");
        
        log.info("任务 [{}] 触发监听器, 发起人: {}", taskDefinitionKey, startUserId);
        
        // 根据任务节点和发起人计算候选组
        Set<String> candidateGroups = calculateCandidateGroups(
            taskDefinitionKey, 
            startUserId,
            delegateTask
        );
        
        if (!candidateGroups.isEmpty()) {
            delegateTask.addCandidateGroups(new ArrayList<>(candidateGroups));
            log.info("任务 [{}] 设置候选组: {}", delegateTask.getName(), candidateGroups);
        }
    }
    
    /**
     * 计算候选组
     */
    private Set<String> calculateCandidateGroups(String taskKey, String startUserId, DelegateTask task) {
        Set<String> groups = new HashSet<>();
        
        // 从流程变量中获取预定义的候选组
        String predefinedGroups = (String) task.getVariable(taskKey + "_groups");
        if (predefinedGroups != null && !predefinedGroups.isEmpty()) {
            String[] groupArray = predefinedGroups.split(",");
            for (String group : groupArray) {
                groups.add(group.trim());
            }
            return groups;
        }
        
        // 从流程变量中获取部门ID
        Object deptIdObj = task.getVariable("deptId");
        
        // 根据任务定义key自动计算
        switch (taskKey) {
            case "submit-leave":
                // 提交请假申请 - 申请人自己
                if (startUserId != null) {
                    groups.add("USER_" + startUserId);
                }
                break;
                
            case "dept-approval":
            case "manager-approval":
                // 部门经理审批
                if (deptIdObj != null) {
                    groups.add("DEPT_" + deptIdObj + "_MANAGER");
                }
                // 如果没有部门信息，使用默认经理角色
                if (groups.isEmpty()) {
                    groups.add("ROLE_MANAGER");
                }
                break;
                
            case "gm-approval":
                // 总经理审批 - 根据金额判断
                Object leaveDays = task.getVariable("leaveDays");
                if (leaveDays != null) {
                    try {
                        int days = Integer.parseInt(leaveDays.toString());
                        if (days > 3) {
                            groups.add("ROLE_GM"); // 大于3天需要总经理审批
                        } else {
                            // 小于等于3天直接跳过或给部门经理
                            groups.add("ROLE_MANAGER");
                        }
                    } catch (NumberFormatException e) {
                        groups.add("ROLE_GM");
                    }
                } else {
                    groups.add("ROLE_GM");
                }
                break;
                
            case "hr-approval":
            case "hr-record":
                // 人事部门角色
                groups.add("ROLE_HR");
                groups.add("ROLE_HR_MANAGER");
                break;
                
            case "finance-approval":
                // 财务审批 - 根据金额判断
                Object amountObj = task.getVariable("amount");
                if (amountObj != null) {
                    try {
                        double amount = Double.parseDouble(amountObj.toString());
                        if (amount > 10000) {
                            groups.add("ROLE_FINANCE_MANAGER"); // 大额需财务经理
                        } else {
                            groups.add("ROLE_FINANCE"); // 普通财务
                        }
                    } catch (NumberFormatException e) {
                        groups.add("ROLE_FINANCE");
                    }
                } else {
                    groups.add("ROLE_FINANCE");
                }
                break;
                
            case "it-approval":
                groups.add("ROLE_IT");
                break;
                
            default:
                // 默认使用任务key作为角色
                groups.add("ROLE_" + taskKey.toUpperCase());
                break;
        }
        
        return groups;
    }
}
