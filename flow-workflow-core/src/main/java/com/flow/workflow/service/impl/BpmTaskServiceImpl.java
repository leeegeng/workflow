package com.flow.workflow.service.impl;

import com.flow.common.exception.BusinessException;
import com.flow.common.result.Result;
import com.flow.common.result.ResultCode;
import com.flow.workflow.dto.TaskCompleteDTO;
import com.flow.workflow.service.BpmTaskService;
import com.flow.workflow.vo.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务Service实现
 */
@Slf4j
@Service
public class BpmTaskServiceImpl implements BpmTaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public Result<List<TaskVO>> getTodoList(Long userId) {
        String userIdStr = String.valueOf(userId);
        
        // 1. 查询直接分配给用户的任务
        List<Task> assigneeTasks = taskService.createTaskQuery()
                .taskAssignee(userIdStr)
                .orderByTaskCreateTime()
                .desc()
                .list();
        
        // 2. 查询用户所在候选组的任务（未认领的）
        List<String> candidateGroups = getUserCandidateGroups(userId);
        
        List<Task> groupTasks = new ArrayList<>();
        if (!candidateGroups.isEmpty()) {
            groupTasks = taskService.createTaskQuery()
                    .taskCandidateGroupIn(candidateGroups)
                    .taskUnassigned()  // 只查询未认领的
                    .orderByTaskCreateTime()
                    .desc()
                    .list();
        }
        
        // 3. 查询用户作为候选人的任务（未认领的）
        List<Task> candidateTasks = taskService.createTaskQuery()
                .taskCandidateUser(userIdStr)
                .taskUnassigned()  // 只查询未认领的
                .orderByTaskCreateTime()
                .desc()
                .list();
        
        // 4. 合并去重
        Set<String> taskIds = new HashSet<>();
        List<Task> allTasks = new ArrayList<>();
        
        for (Task task : assigneeTasks) {
            if (taskIds.add(task.getId())) {
                allTasks.add(task);
            }
        }
        
        for (Task task : groupTasks) {
            if (taskIds.add(task.getId())) {
                allTasks.add(task);
            }
        }
        
        for (Task task : candidateTasks) {
            if (taskIds.add(task.getId())) {
                allTasks.add(task);
            }
        }

        List<TaskVO> voList = allTasks.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }
    
    /**
     * 获取用户的候选组列表
     */
    private List<String> getUserCandidateGroups(Long userId) {
        List<String> groups = new ArrayList<>();
        
        // 从流程变量中获取用户的角色信息
        // 这里简化处理，实际应该从用户服务获取
        groups.add("ROLE_MANAGER");
        groups.add("ROLE_HR");
        groups.add("ROLE_GM");
        groups.add("ROLE_FINANCE");
        
        return groups;
    }

    @Override
    public Result<List<TaskVO>> getDoneList(Long userId) {
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(String.valueOf(userId))
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();

        List<TaskVO> voList = historicTasks.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(voList);
    }

    @Override
    public Result<TaskVO> getTaskById(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        return Result.success(convertToVO(task));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeTask(TaskCompleteDTO completeDTO) {
        Task task = taskService.createTaskQuery()
                .taskId(completeDTO.getTaskId())
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 检查任务是否已被处??
        if (task.getAssignee() == null) {
            throw new BusinessException(ResultCode.TASK_ALREADY_HANDLED);
        }

        // 检查是否有权限处理
        if (!String.valueOf(completeDTO.getUserId()).equals(task.getAssignee())) {
            throw new BusinessException(ResultCode.TASK_NO_PERMISSION);
        }

        // 添加批注
        if (completeDTO.getComment() != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), completeDTO.getComment());
        }

        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        if (completeDTO.getVariables() != null) {
            variables.putAll(completeDTO.getVariables());
        }
        if (completeDTO.getFormData() != null) {
            variables.put("formData", completeDTO.getFormData());
        }
        if (completeDTO.getResult() != null) {
            variables.put("result", completeDTO.getResult());
        }

        // 完成任务
        taskService.complete(task.getId(), variables);

        log.info("完成任务成功: {}, 处理?? {}", task.getId(), completeDTO.getUserId());
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delegateTask(String taskId, Long assigneeId, Long delegateToUserId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 检查是否有权限委托
        if (!String.valueOf(assigneeId).equals(task.getAssignee())) {
            throw new BusinessException(ResultCode.TASK_NO_PERMISSION);
        }

        // 委托任务
        taskService.delegateTask(taskId, String.valueOf(delegateToUserId));

        log.info("委托任务成功: {}, ??{} 委托??{}", taskId, assigneeId, delegateToUserId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> transferTask(String taskId, Long assigneeId, Long transferToUserId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 检查是否有权限转办
        if (!String.valueOf(assigneeId).equals(task.getAssignee())) {
            throw new BusinessException(ResultCode.TASK_NO_PERMISSION);
        }

        // 转办任务（直接修改办理人??
        taskService.setAssignee(taskId, String.valueOf(transferToUserId));

        log.info("转办任务成功: {}, ??{} 转办??{}", taskId, assigneeId, transferToUserId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> rejectTask(String taskId, Long userId, String comment) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 检查是否有权限处理
        if (!String.valueOf(userId).equals(task.getAssignee())) {
            throw new BusinessException(ResultCode.TASK_NO_PERMISSION);
        }

        // 添加批注
        if (comment != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "拒绝: " + comment);
        }

        // 设置流程变量表示拒绝
        Map<String, Object> variables = new HashMap<>();
        variables.put("result", 2); // 2表示拒绝
        variables.put("rejected", true);

        // 完成任务，流程会走向拒绝分支
        taskService.complete(task.getId(), variables);

        log.info("拒绝任务成功: {}, 处理?? {}", taskId, userId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> backTask(String taskId, Long userId, String comment) {
        // TODO: 实现退回上一步功??
        // 需要使用Flowable的驳回功能或动态修改流程节??
        // 这里先简单实现为添加批注
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        if (comment != null) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(), "退?? " + comment);
        }

        log.info("退回任务成?? {}, 处理?? {}", taskId, userId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> claimTask(String taskId, Long userId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 检查任务是否已被认领
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            if (task.getAssignee().equals(String.valueOf(userId))) {
                return Result.success(); // 已被自己认领
            }
            throw new BusinessException("任务已被其他人认领");
        }

        taskService.claim(taskId, String.valueOf(userId));
        log.info("认领任务成功: {}, 用户: {}", taskId, userId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> unclaimTask(String taskId, Long userId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        // 只能取消自己的认领
        if (!String.valueOf(userId).equals(task.getAssignee())) {
            throw new BusinessException("只能释放自己认领的任务");
        }

        taskService.unclaim(taskId);
        log.info("释放任务成功: {}, 用户: {}", taskId, userId);
        return Result.success();
    }

    /**
     * 转换为VO（Task??
     */
    private TaskVO convertToVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(task.getId());
        vo.setTaskName(task.getName());
        vo.setTaskDefKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setAssigneeId(task.getAssignee() != null ? Long.valueOf(task.getAssignee()) : null);
        vo.setCreateTime(task.getCreateTime() != null ?
                task.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setStatus(0); // 待处??

        // 获取流程定义信息
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        if (processInstance != null) {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processInstance.getProcessDefinitionId())
                    .singleResult();
            if (processDefinition != null) {
                vo.setProcessDefinitionName(processDefinition.getName());
            }

            // 获取流程变量
            Map<String, Object> variables = runtimeService.getVariables(processInstance.getId());
            vo.setTitle((String) variables.get("title"));
            Object startUserId = variables.get("startUserId");
            if (startUserId != null) {
                vo.setStartUserId(Long.valueOf(startUserId.toString()));
            }
            vo.setFormData((Map<String, Object>) variables.get("formData"));
        }

        return vo;
    }

    /**
     * 转换为VO（HistoricTaskInstance??
     */
    private TaskVO convertToVO(HistoricTaskInstance historicTask) {
        TaskVO vo = new TaskVO();
        vo.setTaskId(historicTask.getId());
        vo.setTaskName(historicTask.getName());
        vo.setTaskDefKey(historicTask.getTaskDefinitionKey());
        vo.setProcessInstanceId(historicTask.getProcessInstanceId());
        vo.setAssigneeId(historicTask.getAssignee() != null ? Long.valueOf(historicTask.getAssignee()) : null);
        vo.setCreateTime(historicTask.getCreateTime() != null ?
                historicTask.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setEndTime(historicTask.getEndTime() != null ?
                historicTask.getEndTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);
        vo.setStatus(1); // 已处??

        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicTask.getProcessDefinitionId())
                .singleResult();
        if (processDefinition != null) {
            vo.setProcessDefinitionName(processDefinition.getName());
        }

        // 获取历史流程实例信息
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(historicTask.getProcessInstanceId())
                .singleResult();
        if (historicProcessInstance != null) {
            Map<String, Object> variables = historicProcessInstance.getProcessVariables();
            if (variables != null) {
                vo.setTitle((String) variables.get("title"));
                Object startUserId = variables.get("startUserId");
                if (startUserId != null) {
                    vo.setStartUserId(Long.valueOf(startUserId.toString()));
                }
            }
        }

        return vo;
    }
}
