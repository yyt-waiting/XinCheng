-- 注意：该页面对应的前台目录为views/inventory文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026053111011390170', NULL, '库存变更表', '/inventory/wmsInventoryTransList', 'inventory/WmsInventoryTransList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011390171', '2026053111011390170', '添加库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011390172', '2026053111011390170', '编辑库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011390173', '2026053111011390170', '删除库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011390174', '2026053111011390170', '批量删除库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011400175', '2026053111011390170', '导出excel_库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026053111011400176', '2026053111011390170', '导入excel_库存变更表', NULL, NULL, 0, NULL, NULL, 2, 'inventory:wms_inventory_trans:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-05-31 11:01:17', NULL, NULL, 0, 0, '1', 0);