-- 工作流系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS flow_workflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE flow_workflow;

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    leader_id BIGINT COMMENT '负责人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    INDEX idx_parent_id (parent_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    UNIQUE KEY uk_username (username),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_type TINYINT COMMENT '菜单类型（1-目录，2-菜单，3-按钮）',
    icon VARCHAR(100) COMMENT '菜单图标',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    permission VARCHAR(100) COMMENT '权限标识',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_type (menu_type),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志（0-正常，1-已删除）',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 流程模型扩展表
CREATE TABLE IF NOT EXISTS bpm_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    model_id VARCHAR(64) COMMENT 'Flowable模型ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称',
    model_key VARCHAR(100) NOT NULL COMMENT '模型标识',
    description VARCHAR(500) COMMENT '模型描述',
    category VARCHAR(50) COMMENT '分类',
    form_id BIGINT COMMENT '关联表单ID',
    status TINYINT DEFAULT 0 COMMENT '状态（0-草稿，1-已部署）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_model_key (model_key),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程模型扩展表';

-- 流程表单表（已移动到下方完整定义）

-- 流程实例扩展表
CREATE TABLE IF NOT EXISTS bpm_process_instance_ext (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    process_definition_id VARCHAR(64) NOT NULL COMMENT '流程定义ID',
    process_definition_key VARCHAR(100) COMMENT '流程定义标识',
    process_definition_name VARCHAR(100) COMMENT '流程定义名称',
    start_user_id BIGINT COMMENT '发起人ID',
    start_user_name VARCHAR(50) COMMENT '发起人姓名',
    business_key VARCHAR(100) COMMENT '业务主键',
    title VARCHAR(200) COMMENT '流程标题',
    status TINYINT DEFAULT 1 COMMENT '状态（1-运行中，2-已完成，3-已终止）',
    form_data TEXT COMMENT '表单数据JSON',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration BIGINT COMMENT '持续时间（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_process_instance (process_instance_id),
    INDEX idx_start_user (start_user_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例扩展表';

-- 任务扩展表
CREATE TABLE IF NOT EXISTS bpm_task_ext (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
    task_name VARCHAR(100) COMMENT '任务名称',
    task_def_key VARCHAR(100) COMMENT '任务定义标识',
    process_instance_id VARCHAR(64) COMMENT '流程实例ID',
    process_definition_id VARCHAR(64) COMMENT '流程定义ID',
    assignee_id BIGINT COMMENT '办理人ID',
    assignee_name VARCHAR(50) COMMENT '办理人姓名',
    owner_id BIGINT COMMENT '委托人ID',
    owner_name VARCHAR(50) COMMENT '委托人姓名',
    status TINYINT DEFAULT 0 COMMENT '状态（0-待处理，1-已处理，2-已委托）',
    result TINYINT COMMENT '处理结果（1-同意，2-拒绝，3-退回）',
    comment VARCHAR(500) COMMENT '处理意见',
    form_data TEXT COMMENT '表单数据JSON',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration BIGINT COMMENT '持续时间（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_task (task_id),
    INDEX idx_assignee (assignee_id),
    INDEX idx_process_instance (process_instance_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务扩展表';

-- 流程表单表（包含category字段的完整定义）
CREATE TABLE IF NOT EXISTS bpm_form (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_key VARCHAR(100) NOT NULL COMMENT '表单标识（唯一）',
    description VARCHAR(500) COMMENT '表单描述',
    category VARCHAR(50) COMMENT '分类',
    form_json LONGTEXT COMMENT '表单JSON配置',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_form_key (form_key),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程表单表';

-- 表单与流程模型关联表
CREATE TABLE IF NOT EXISTS bpm_model_form (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    model_id VARCHAR(64) NOT NULL COMMENT '流程模型ID',
    form_id BIGINT NOT NULL COMMENT '表单ID',
    node_key VARCHAR(100) COMMENT '流程节点标识（为空表示全局表单）',
    form_type TINYINT DEFAULT 1 COMMENT '表单类型（1-全局表单，2-节点表单）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_model_form_node (model_id, form_id, node_key),
    INDEX idx_model_id (model_id),
    INDEX idx_form_id (form_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单与流程模型关联表';

-- 初始化数据

-- 插入默认部门
INSERT INTO sys_dept (id, dept_name, parent_id, sort_order) VALUES
(1, '总公司', 0, 1),
(2, '技术部', 1, 1),
(3, '财务部', 1, 2),
(4, '人事部', 1, 3)
ON DUPLICATE KEY UPDATE dept_name = VALUES(dept_name);

-- 插入默认角色
INSERT INTO sys_role (id, role_name, role_code, description, sort_order) VALUES
(1, '超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', 1),
(2, '系统管理员', 'admin', '系统管理员', 2),
(3, '普通用户', 'user', '普通用户', 3)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- 插入默认用户（密码：123456，MD5加密后：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO sys_user (id, username, password, real_name, phone, email, dept_id, status) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@flow.com', 1, 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1)
ON DUPLICATE KEY UPDATE user_id = user_id;

-- 插入默认菜单
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) VALUES
-- 系统管理
(1, '系统管理', 0, 1, 'Setting', '/system', NULL, NULL, 1),
(2, '用户管理', 1, 2, NULL, 'user', 'system/user/index', 'system:user:list', 1),
(21, '用户新增', 2, 3, NULL, NULL, NULL, 'system:user:add', 1),
(22, '用户编辑', 2, 3, NULL, NULL, NULL, 'system:user:edit', 2),
(23, '用户删除', 2, 3, NULL, NULL, NULL, 'system:user:delete', 3),
(24, '用户详情', 2, 3, NULL, NULL, NULL, 'system:user:detail', 4),
(25, '重置密码', 2, 3, NULL, NULL, NULL, 'system:user:resetPwd', 5),
(3, '角色管理', 1, 2, NULL, 'role', 'system/role/index', 'system:role:list', 2),
(31, '角色新增', 3, 3, NULL, NULL, NULL, 'system:role:add', 1),
(32, '角色编辑', 3, 3, NULL, NULL, NULL, 'system:role:edit', 2),
(33, '角色删除', 3, 3, NULL, NULL, NULL, 'system:role:delete', 3),
(34, '分配菜单', 3, 3, NULL, NULL, NULL, 'system:role:assignMenu', 4),
(4, '菜单管理', 1, 2, NULL, 'menu', 'system/menu/index', 'system:menu:list', 3),
(41, '菜单新增', 4, 3, NULL, NULL, NULL, 'system:menu:add', 1),
(42, '菜单编辑', 4, 3, NULL, NULL, NULL, 'system:menu:edit', 2),
(43, '菜单删除', 4, 3, NULL, NULL, NULL, 'system:menu:delete', 3),
(5, '部门管理', 1, 2, NULL, 'dept', 'system/dept/index', 'system:dept:list', 4),
(51, '部门新增', 5, 3, NULL, NULL, NULL, 'system:dept:add', 1),
(52, '部门编辑', 5, 3, NULL, NULL, NULL, 'system:dept:edit', 2),
(53, '部门删除', 5, 3, NULL, NULL, NULL, 'system:dept:delete', 3),

-- 流程管理
(10, '流程管理', 0, 1, 'Connection', '/workflow', NULL, NULL, 2),
(11, '流程模型', 10, 2, NULL, 'model', 'workflow/model/index', 'workflow:model:list', 1),
(111, '模型新增', 11, 3, NULL, NULL, NULL, 'workflow:model:add', 1),
(112, '模型编辑', 11, 3, NULL, NULL, NULL, 'workflow:model:edit', 2),
(113, '模型删除', 11, 3, NULL, NULL, NULL, 'workflow:model:delete', 3),
(114, '模型部署', 11, 3, NULL, NULL, NULL, 'workflow:model:deploy', 4),
(12, '流程定义', 10, 2, NULL, 'definition', 'workflow/definition/index', 'workflow:definition:list', 2),
(121, '定义详情', 12, 3, NULL, NULL, NULL, 'workflow:definition:detail', 1),
(122, '定义激活', 12, 3, NULL, NULL, NULL, 'workflow:definition:activate', 2),
(123, '定义挂起', 12, 3, NULL, NULL, NULL, 'workflow:definition:suspend', 3),
(124, '定义XML', 12, 3, NULL, NULL, NULL, 'workflow:definition:xml', 4),
(13, '流程实例', 10, 2, NULL, 'instance', 'workflow/instance/index', 'workflow:instance:list', 3),
(14, '待办任务', 10, 2, NULL, 'todo', 'workflow/todo/index', 'workflow:task:todo', 4),
(15, '已办任务', 10, 2, NULL, 'done', 'workflow/done/index', 'workflow:task:done', 5),
(16, '表单设计', 10, 2, NULL, 'form', 'workflow/form/index', 'workflow:form:list', 6),
(161, '表单新增', 16, 3, NULL, NULL, NULL, 'workflow:form:add', 1),
(162, '表单编辑', 16, 3, NULL, NULL, NULL, 'workflow:form:edit', 2),
(163, '表单删除', 16, 3, NULL, NULL, NULL, 'workflow:form:delete', 3),
(17, '流程监控', 10, 2, NULL, 'monitor', 'workflow/monitor/index', 'workflow:monitor:list', 7)

ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 给超级管理员分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0
ON DUPLICATE KEY UPDATE role_id = role_id;
