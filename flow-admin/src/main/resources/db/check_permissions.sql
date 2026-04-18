-- 检查权限是否正确添加
USE flow_workflow;

-- 查看所有按钮权限
SELECT id, menu_name, permission, menu_type, parent_id 
FROM sys_menu 
WHERE deleted = 0 
ORDER BY parent_id, sort_order;

-- 查看超级管理员的权限
SELECT m.id, m.menu_name, m.permission
FROM sys_menu m
JOIN sys_role_menu rm ON m.id = rm.menu_id
WHERE rm.role_id = 1 AND m.deleted = 0
ORDER BY m.parent_id, m.sort_order;

-- 检查用户管理相关权限
SELECT * FROM sys_menu 
WHERE permission LIKE 'system:user:%' AND deleted = 0;
