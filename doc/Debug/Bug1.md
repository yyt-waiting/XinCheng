1.收货操作失败：
MenuItem.vue:110 storePathTitle /inorder/wmsReceiveTasksList
signMd5Utils.js:42 sign requestBody: {column: 'createTime', order: 'desc', pageNo: '1', pageSize: '10', stockInOrderNumber: undefined, …}

报错提示：
 ### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'p.owner_name' in 'field list' ### The error may exist in file [D:\cursor\HarmonyOS\Github project\XinCheng\xincheng-wms-java-course\jeecg-module-wms\target\classes\org\jeecg\modules\wms\wmstask\mapper\xml\WmsTasksMapper.xml] ### The error may involve defaultParameterMap ### The error occurred while setting parameters ### SQL: SELECT t.*, o.order_number AS stockInOrderNumber, w.warehouse_name AS warehouseName, p.owner_name AS ownerName, p.product_name AS productName FROM wms_tasks t LEFT JOIN wms_stock_in_orders o ON t.stock_in_order_id = o.id LEFT JOIN wms_warehouses w ON t.target_warehouse_id = w.id LEFT JOIN wms_products p ON t.product_id = p.id WHERE t.task_type = ? AND t.task_status = ? ORDER BY t.create_time DESC LIMIT ? ### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'p.owner_name' in 'field list' ; bad SQL grammar []



2.上架操作失败

MenuItem.vue:110 storePathTitle /inorder/wmsPutwayTasksList
signMd5Utils.js:42 sign requestBody: {column: 'createTime', order: 'desc', pageNo: '1', pageSize: '10', taskNumber: undefined, …}

报错消息：
 ### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'p.owner_name' in 'field list' ### The error may exist in file [D:\cursor\HarmonyOS\Github project\XinCheng\xincheng-wms-java-course\jeecg-module-wms\target\classes\org\jeecg\modules\wms\wmstask\mapper\xml\WmsTasksMapper.xml] ### The error may involve defaultParameterMap ### The error occurred while setting parameters ### SQL: SELECT t.*, o.order_number AS stockInOrderNumber, w.warehouse_name AS warehouseName, p.owner_name AS ownerName, p.product_name AS productName FROM wms_tasks t LEFT JOIN wms_stock_in_orders o ON t.stock_in_order_id = o.id LEFT JOIN wms_warehouses w ON t.target_warehouse_id = w.id LEFT JOIN wms_products p ON t.product_id = p.id WHERE t.task_type = ? ORDER BY t.create_time DESC LIMIT ? ### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'p.owner_name' in 'field list' ; bad SQL grammar []


3.上架记录操作失败，Required request parameter 'taskId' for method parameter type String is not present


MenuItem.vue:110 storePathTitle /inorder/wmsPutwayTasksRecordsList
signMd5Utils.js:42 sign requestBody: {column: 'createTime', order: 'desc', pageNo: '1', pageSize: '10', taskNumber: undefined, …}
