-- 注意：该页面对应的前台目录为views/wmstask文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026053110374330480', NULL, '任务表', '/wmstask/wmsTasksList', 'wmstask/WmsTasksList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374330481', '2026053110374330480', '添加任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374330482', '2026053110374330480', '编辑任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374340483', '2026053110374330480', '删除任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374340484', '2026053110374330480', '批量删除任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374340485', '2026053110374330480', '导出excel_任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053110374340486', '2026053110374330480', '导入excel_任务表', NULL, NULL, 0, NULL, NULL, 2, 'wmstask:wms_tasks:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 10:37:48', NULL, NULL, 0, 0, '1', 0);