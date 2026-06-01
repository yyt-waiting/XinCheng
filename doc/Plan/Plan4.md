3. 上架管理(实战)
3.1 任务目标
通过实战开发，掌握仓储系统中 "上架" 功能的完整实现逻辑，理解 "收货暂存→永久
储位" 的库存转移机制，
例：将 A01 暂存区的 100 箱饮料转移到 B02 货架，完成后 B02 储位的库存增加 100
，A01 暂存区库存清零。
最终实现：
1.
2.
3.
4.
自动创建上架任务（收货完成后）
查询待上架任务列表
执行上架操作（同步库存转移）
查询上架记录
3.2 流程分析
• 创建上架任务
• 上架流程
3.3 创建上架任务
前置准备
拷贝 课程资料-->代码-->初始代码--上架--PutawayTasksController.java 
到 org.jeecg.modules.wms.inorder.controller 包下
任务内容：
实现 "收货完成后自动创建上架任务" 的功能，需满足：
• 仅当入库单状态变为 "收货完成" 时创建
• 仅为 "良品" 创建上架任务（不良品跳过）
• 每条良品收货记录对应 1 条上架任务
开发提示：
1.
触发时机：在WmsTasksServiceImpl 的receive 方法中，判断入库单状态为
INBOUND_RECEIVED 时调用创建方法
Java
// 参考代码片段
String inOrderStatus = 
stockInOrdersService.updateReceivedStatus(stockInOrderId);
if 
(WarehouseDictEnum.INBOUND_RECEIVED.getCode().equals(inOrderStatus
)) {
 // 调用创建上架任务的方法
 createPutawayTask(stockInOrderId);
}
2.
任务表字段设置：
￮ task_type= 上架（参考枚举WarehouseDictEnum.TASK_TYPE_PUTAWAY）
￮ task_status= 已创建（TASK_STATUS_CREATED）
￮ source_location_code= 收货时的暂存储位
￮ quantity= 收货数量（需从收货记录中获取）
检验标准：
收货完成后，查询wms_tasks表，是否生成符合条件的上架任务记录
3.4 查询待上架任务列表
任务内容：
开发接口，支持执行人查询自己的待上架任务（状态为 "已创建" 或 "执行中"），需返
回：
• 任务 ID、任务编号、商品名称、来源储位名称、待上架数量、入库单号
开发提示：
1.
Controller 层：参考收货任务查询，新增PutawayTasksController，提供
/list 接口
Java
代码块
@Operation(summary="待上架任务查询")
@GetMapping(value = "/list")
public Result<IPage<WmsTasks>> list(WmsTasks wmsTasks,
                               @RequestParam(name="pageNo", 
defaultValue="1") Integer pageNo,
                               @RequestParam(name="pageSize", 
defaultValue="10") Integer pageSize,
                               HttpServletRequest req) {
      // 1. 关键：设置任务类型为"上架任务"，过滤其他任务
      
wmsTasks.setTaskType(WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode()
);
      // 2. 设置执行人=当前登录用户（仅查自己的任务）
      LoginUser sysUser = (LoginUser) 
SecurityUtils.getSubject().getPrincipal();
      wmsTasks.setOperator(sysUser.getId());
      // 3. 调用Service查询分页数据（复用任务表的通用查询方法）
      IPage<WmsTasks> wmsTasksIPage = 
wmsTasksService.list(wmsTasks, pageNo, pageSize);
      return Result.OK(wmsTasksIPage);
}
1.关联查询：在 Mapper 中编写 SQL，关联wms_products（商品表）和
wms_storage_locations（储位表）
检验标准：
调用接口后，能正确返回当前执行人的待上架任务，且包含关联信息
3.5 执行上架操作（核心：库存转移）
任务内容：
开发上架接口，实现 "从来源储位（暂存）转移到目标储位（正式）"，需同步更新：
•上架任务状态（已完成数量累加）
•库存表：来源储位扣减，目标储位增加
•记录库存变更日志
开发提示：
1.库存转移逻辑：创建WmsInventoryTransByPutway类，实现transfer方法
TypeScript
@Override
@Transactional
public void transfer(WmsInventoryTransParam param) {
    // 1. 来源储位扣减库存
    deductSourceInventory(param);
    // 2. 目标储位增加库存
    addTargetInventory(param);
    // 3. 记录库存变更日志
    recordTransLog(param);
}
2.扣减来源库存：需校验库存充足（stock_quantity >= 本次数量）
Java
private void deductSourceInventory(WmsInventoryTransParam param) {
    WmsInventory source = 
wmsInventoryService.getInventoryByUniqueKey(
        param.getProductId(), param.getSourceLocationCode(), 
param.getBatchNumber()
    );
    if (source == null || source.getStockQuantity() < 
param.getExecQuantity()) {
        throw new RuntimeException("来源储位库存不足");
    }
    // 更新：stock_quantity -= 数量，available_quantity -= 数量
}
3.增加目标库存：若目标储位已有该商品 + 批次，累加数量；否则新增记录
检验标准：
上架后：
•来源储位库存数量正确减少
•目标储位库存数量正确增加
• 任务表的completed_quantity正确累加
3.6 查询上架记录
任务内容：
开发接口，查询某上架任务的所有执行记录，需包含：
• 记录 ID、执行时间、执行人、来源储位、目标储位、执行数量、商品名称
开发提示：
1.
数据来源：从wms_tasks_records 表查询（任务记录），关联商品表和储位
表
2.
接口设计：在PutawayTasksController 中新增/records 接口，接收
wmsTasksRecords参数
TypeScript
@GetMapping("/records")
public Result<IPage<WmsTasksRecordsVO>> records(WmsTasksRecords 
wmsTasksRecords, Integer pageNo, Integer pageSize) {
 IPage<WmsTasksRecordsVO> records = 
wmsTasksRecordsService.queryPutawayRecords(taskId, pageNo, 
pageSize);
}
   // 1. 过滤任务类型：仅查询上架任务的记录
   // 2. 分页查询（复用任务记录表的通用分页方法）
 return Result.OK(records);
检验标准：
调用接口后，能正确返回该任务的所有上架记录，包括每次的执行数量和储位信息