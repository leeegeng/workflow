-- 修复系统管理按钮权限
-- 执行方式：使用 MySQL 客户端或数据库管理工具执行

USE flow_workflow;

-- 用户管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) VALUES
(21, '用户新增', 2, 3, NULL, NULL, NULL, 'system:user:add', 1),
(22, '用户编辑', 2, 3, NULL, NULL, NULL, 'system:user:edit', 2),
(23, '用户删除', 2, 3, NULL, NULL, NULL, 'system:user:delete', 3),
(24, '用户详情', 2, 3, NULL, NULL, NULL, 'system:user:detail', 4),
(25, '重置密码', 2, 3, NULL, NULL, NULL, 'system:user:resetPwd', 5)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), permission = VALUES(permission);

-- 角色管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) VALUES
(31, '角色新增', 3, 3, NULL, NULL, NULL, 'system:role:add', 1),
(32, '角色编辑', 3, 3, NULL, NULL, NULL, 'system:role:edit', 2),
(33, '角色删除', 3, 3, NULL, NULL, NULL, 'system:role:delete', 3),
(34, '分配菜单', 3, 3, NULL, NULL, NULL, 'system:role:assignMenu', 4)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), permission = VALUES(permission);

-- 菜单管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) VALUES
(41, '菜单新增', 4, 3, NULL, NULL, NULL, 'system:menu:add', 1),
(42, '菜单编辑', 4, 3, NULL, NULL, NULL, 'system:menu:edit', 2),
(43, '菜单删除', 4, 3, NULL, NULL, NULL, 'system:menu:delete', 3)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), permission = VALUES(permission);

-- 部门管理按钮权限
INSERT INTO sys_menu (id, menu_name, parent_id, menu_type, icon, path, component, permission, sort_order) VALUES
(51, '部门新增', 5, 3, NULL, NULL, NULL, 'system:dept:add', 1),
(52, '部门编辑', 5, 3, NULL, NULL, NULL, 'system:dept:edit', 2),
(53, '部门删除', 5, 3, NULL, NULL, NULL, 'system:dept:delete', 3)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), permission = VALUES(permission);

-- 给超级管理员分配所有新权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0 AND id BETWEEN 21 AND 53
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 验证权限添加成功
SELECT * FROM sys_menu WHERE menu_type = 3 ORDER BY parent_id, sort_order;
