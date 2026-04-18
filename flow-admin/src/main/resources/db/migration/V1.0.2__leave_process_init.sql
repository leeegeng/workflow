-- =====================================================
-- Flyway Migration: V1.0.2
-- 简单请假流程初始化
-- 包含：表单定义、流程模型基础数据
-- =====================================================

-- 1. 创建请假申请表单
INSERT INTO bpm_form (id, form_name, form_key, form_json, description, status, deleted, create_time, update_time)
VALUES (
    1,
    '请假申请表单',
    'leave-form',
    '{"fields": [{"type": "select", "label": "请假类型", "field": "leaveType", "required": true, "options": [{"label": "事假", "value": "personal"}, {"label": "病假", "value": "sick"}, {"label": "年假", "value": "annual"}, {"label": "调休", "value": "compensatory"}, {"label": "婚假", "value": "marriage"}, {"label": "产假", "value": "maternity"}, {"label": "丧假", "value": "bereavement"}]}, {"type": "datetime", "label": "开始时间", "field": "startTime", "required": true, "format": "yyyy-MM-dd HH:mm"}, {"type": "datetime", "label": "结束时间", "field": "endTime", "required": true, "format": "yyyy-MM-dd HH:mm"}, {"type": "number", "label": "请假天数", "field": "leaveDays", "required": true, "min": 0.5, "max": 365, "step": 0.5}, {"type": "textarea", "label": "请假原因", "field": "reason", "required": true, "placeholder": "请详细说明请假原因...", "rows": 4}, {"type": "textarea", "label": "工作交接", "field": "handover", "required": false, "placeholder": "请说明工作交接情况...", "rows": 3}]}',
    '员工请假申请表单，包含请假类型、时间、原因等信息',
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON DUPLICATE KEY UPDATE
    form_name = VALUES(form_name),
    form_json = VALUES(form_json),
    description = VALUES(description),
    update_time = CURRENT_TIMESTAMP;

-- 2. 创建审批意见表单
INSERT INTO bpm_form (id, form_name, form_key, form_json, description, status, deleted, create_time, update_time)
VALUES (
    2,
    '审批意见表单',
    'approval-form',
    '{"fields": [{"type": "radio", "label": "审批结果", "field": "approved", "required": true, "options": [{"label": "批准", "value": true}, {"label": "拒绝", "value": false}]}, {"type": "textarea", "label": "审批意见", "field": "comment", "required": true, "placeholder": "请输入审批意见...", "rows": 4}]}',
    '审批人填写审批结果和意见',
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON DUPLICATE KEY UPDATE
    form_name = VALUES(form_name),
    form_json = VALUES(form_json),
    description = VALUES(description),
    update_time = CURRENT_TIMESTAMP;

-- 3. 创建请假流程模型
INSERT INTO bpm_model (id, model_name, model_key, category, description, form_id, status, version, deleted, create_time, update_time)
VALUES (
    1,
    '简单请假流程',
    'leave-simple',
    '人事管理',
    '员工请假审批流程，根据请假天数自动路由：≤3天部门经理审批，>3天需总经理审批',
    1,
    0,
    1,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON DUPLICATE KEY UPDATE
    model_name = VALUES(model_name),
    category = VALUES(category),
    description = VALUES(description),
    form_id = VALUES(form_id),
    update_time = CURRENT_TIMESTAMP;
