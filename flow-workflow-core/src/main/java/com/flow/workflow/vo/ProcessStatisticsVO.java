package com.flow.workflow.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 流程统计VO
 */
@Data
public class ProcessStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 流程定义总数
     */
    private Long processDefinitionCount;

    /**
     * 流程实例总数
     */
    private Long processInstanceCount;

    /**
     * 运行中流程数
     */
    private Long runningCount;

    /**
     * 已完成流程数
     */
    private Long completedCount;

    /**
     * 已终止流程数
     */
    private Long terminatedCount;

    /**
     * 今日发起流程??
     */
    private Long todayStartCount;

    /**
     * 今日完成流程??
     */
    private Long todayCompleteCount;

    /**
     * 待办任务总数
     */
    private Long todoTaskCount;

    /**
     * 已办任务总数
     */
    private Long doneTaskCount;

    /**
     * 平均流程耗时（毫秒）
     */
    private Long averageDuration;

    /**
     * 平均流程耗时格式??
     */
    private String averageDurationFormat;

    /**
     * 流程发起趋势（按天统计）
     */
    private List<Map<String, Object>> startTrend;

    /**
     * 流程完成趋势（按天统计）
     */
    private List<Map<String, Object>> completeTrend;

    /**
     * 各流程类型统??
     */
    private List<Map<String, Object>> processTypeStatistics;

}
