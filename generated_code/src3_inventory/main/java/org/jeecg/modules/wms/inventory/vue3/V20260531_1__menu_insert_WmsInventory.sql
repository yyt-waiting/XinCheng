-- 注意：该页面对应的前台目录为views/inventory文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026053111002300170', NULL, '库存表', '/inventory/wmsInventoryList', 'inventory/WmsInventoryList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002300171', '2026053111002300170', '添加库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002300172', '2026053111002300170', '编辑库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002300173', '2026053111002300170', '删除库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002310174', '2026053111002300170', '批量删除库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002310175', '2026053111002300170', '导出excel_库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111002310176', '2026053111002300170', '导入excel_库存表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:00:17', NULL, NULL, 0, 0, '1', 0);