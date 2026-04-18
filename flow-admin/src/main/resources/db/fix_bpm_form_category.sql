-- 修复 bpm_form 表缺少 category 字段的问题
-- 执行方式：使用 MySQL 客户端或数据库管理工具执行

USE flow_workflow;

-- 为 bpm_form 表添加 category 字段
ALTER TABLE bpm_form ADD COLUMN category VARCHAR(50) COMMENT '分类' AFTER description;

-- 验证字段是否添加成功
DESCRIBE bpm_form;
