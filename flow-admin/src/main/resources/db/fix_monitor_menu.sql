-- 修复流程监控菜单和权限
-- 执行方式：使用 MySQL 客户端或数据库管理工具执行

USE flow_workflow;

-- 添加流程监控菜单
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) 
VALUES (17, '流程监控', 10, 2, NULL, 'monitor', 'workflow/monitor/index', 'workflow:monitor:list', 7)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), permission = VALUES(permission);

-- 给超级管理员分配流程监控菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 17);

-- 验证添加成功
SELECT * FROM sys_menu WHERE id = 17;
SELECT * FROM sys_role_menu WHERE role_id = 1 AND menu_id = 17;
