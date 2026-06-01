现在我们需要完成【星辰 WMS 仓储管理系统】的“收货管理、库存变更、上架（实战开发）”三个核心模块。

请根据下面提供的详细业务流程、表结构和代码蓝图，为我编写和改造完整的 Java 后端代码。请保证代码的高质量、防重性、事务原子性，并提供清晰的文件名、包路径和新增的 import 语句。

---

### 一、核心数据库表结构（Context）

1. **库存表 `wms_inventory`**
   - 核心约束：`unique (product_id, location_code, batch_number)`（同一商品、在同一储位、且是同一个批次，在库中只能有一条库存记录）。
   - 关键字段：`stock_quantity`（在库总数）, `allocated_quantity`（分配锁定数）, `available_quantity`（可用数 = 在库 - 分配）, `is_sellable`（1=可售/良品，0=不可售/不良品）。

2. **库存变更日志表 `wms_inventory_trans`**
   - 记录每一次库存变动，包括收货、上架、拣货等，记录变动前后的数量与储位。

---

### 二、详细代码开发任务与规格说明

#### 任务 1：收货任务控制与总控逻辑开发
我们需要将前端发起的收货操作（`WmsTasksRecords`），通过 Controller、Service 最终实现收货入库以及上游状态的变更。

**1.1 控制器 `ReceiveTasksController.java`**
- **路径**：`org.jeecg.modules.wms.inorder.controller`
- **任务**：实现 `@PostMapping("/addRecords")`。
- **细节**：前端传的 `id` 字段实际是任务 ID，控制层需要将其转存到 `taskId` 字段中，并将 `id` 设为 `null`（防止覆盖已有记录），最后调用 `wmsTasksService.receive(wmsTasksRecords)`。

**1.2 业务接口 `IWmsTasksService.java`**
- **路径**：`org.jeecg.modules.wms.wmstask.service`
- **任务**：添加 `void receive(WmsTasksRecords wmsTasksRecords);` 接口定义。

**1.3 核心实现类 `WmsTasksServiceImpl.java`**
- **路径**：`org.jeecg.modules.wms.wmstask.service.impl`
- **任务**：实现 `receive` 核心总控逻辑，以及私有辅助校验方法 `execute`。
- **业务逻辑流程 (receive方法)**：
  1. 调用私有方法 `execute(wmsTasksRecords)` 执行收货（写入日志 + 更新当前任务数量与状态）。
  2. 调用 `stockInOrderItemsService.updateReceivedStatus(stockInOrderItemId)` 更新入库单明细的数量和状态。
  3. 调用 `stockInOrdersService.updateReceivedStatus(stockInOrderId)` 更新入库单整单的数量和状态。
  4. 【新增步骤】：组装 `WmsInventoryTransParam`，设置ProductId、ExecQuantity、WarehouseId、TargetLocationCode、BatchNumber、ExpiryDate、TransactionType（值为收货）、Remarks、OperationTime、IsSellable（根据是否良品判断，良品为"1"否则为"0"），并调用 `wmsInventoryTransByReceiving.transfer(inventoryTransParam)` 同步更新库存。
  5. 判断如果入库单返回状态为“收货完成”（`INBOUND_RECEIVED`），则调用 `createPutawayTask(stockInOrderId)` 创建自动上架任务。
- **业务校验逻辑 (execute方法)**：
  - **约束 1**：累计完成数量校验。本次执行数 + 历史已完成数 不能大于 预期计划数。
  - **约束 2**：不良品存放校验。根据前端传的储位编码，查询储位（`WmsStorageLocations`），如果该储位是可售储位（`isSellable == "1"`），但收货属性为不良品（`INVENTORY_ATTRIBUTE_DEFECTIVE`），则直接抛出业务异常 `JeecgBootException`：“不能将不良品存放在可售储位上！”。
  - **记录保存**：补充记录实体中的创建人、执行时间、仓库ID、单据ID等主档信息，保存收货日志。累加更新 `wms_tasks` 主表的已完成数量，若等于计划数量，则更新任务状态为“已完成”（`TASK_STATUS_COMPLETED`）。

---

#### 任务 2：上游入库单明细与主表状态更新
当某一次收货记录被保存后，上游的入库单明细及主表必须随之自动更新累计数量和状态。

**2.1 入库明细更新 `WmsStockInOrderItemsServiceImpl.java`**
- **路径**：`org.jeecg.modules.wms.inorder.service.impl`
- **任务**：实现 `updateReceivedStatus(String stockInOrderItemId)`。
- **细节**：
  1. 查询该明细，并查询当前明细所有的收货记录列表（`WmsTasksRecords`）。
  2. 通过 Stream 流过滤出不良品和良品记录，分别累加数量。
  3. 更新明细的已收良品数量（`receivedQuantity`）和不良品数量（`defectiveQuantity`）。
  4. 如果已收总数（良品 + 不良品）等于明细的计划总数（`expectedQuantity`），则把状态修改为“明细收货完成”（`INBOUND_DETAIL_RECEIVED`）。保存修改。

**2.2 入库单主表更新 `WmsStockInOrdersServiceImpl.java`**
- **路径**：`org.jeecg.modules.wms.inorder.service.impl`
- **任务**：实现 `updateReceivedStatus(String stockInOrderId)`。
- **细节**：
  1. 校验当前单据状态只有在“收货中”或“审核通过”时，方可更新。否则抛异常。
  2. 查询该入库单下的所有明细行（`List<WmsStockInOrderItems>`）。
  3. 累计明细中的总完成良品和缺陷品数量。
  4. 使用 `anyMatch` 判断，若“存在任意一个明细的状态不等于明细收货完成”，则整单状态为“收货中” (`INBOUND_RECEIVING`)；若全部收完，则状态扭转为“收货完成” (`INBOUND_RECEIVED`)。
  5. 更新入库单数据并返回最新状态。

---

#### 任务 3：收货场景下的通用库存变更与同步
当点击收货后，系统必须同步修改库存表和库存流水表。

**3.1 参数实体定义 `WmsInventoryTransParam.java`**
- **路径**：`org.jeecg.modules.wms.inventory.vo`
- **任务**：提供通用的库存变更 VO。包含商品ID、执行数量、仓库ID、来源储位编码、目的储位编码、批次号、保质期、变更类型、执行人、执行时间、是否可售。

**3.2 唯一约束匹配查询 `WmsInventoryServiceImpl.java`**
- **路径**：`org.jeecg.modules.wms.inventory.service.impl`
- **任务**：实现根据唯一约束匹配查询库存：`WmsInventory getInventoryByUniqueKey(String productId, String locationCode, String batchNumber)`。
- **细节**：严格匹配这三个字段。由于批次号可能为空，在构建 `eq` 查询条件时，若 `batchNumber` 为空，应转为空字符串 `""` 匹配，避免 SQL 与 NULL 比较导致查询失效。

**3.3 收货库存变更实现类 `WmsInventoryTransByReceiving.java`**
- **路径**：`org.jeecg.modules.wms.inventory.service.impl`
- **类注解**：实现接口 `IWmsInventoryTransService`，并添加 `@Service("wmsInventoryTransByReceiving")` 注解以便与其他场景（如上架）区分。
- **核心逻辑 (`transfer` 方法)**：
  1. **第一步 参数校验**：非空校验目标储位、商品、数量、是否可售。校验商品和储位在主档中必须真实存在，否则抛业务异常。
  2. **第二步 唯一键查询**：调用 `getInventoryByUniqueKey` 校验是否存在已有库存。
  3. **第三步 新增或累加库存**：
     - **若不存在**：新建 `WmsInventory` 实体。使用 `BeanUtils.copyProperties` 复制基础信息，从商品表补全关联的货主（OwnerId）。设置在库数量 = 本次执行数量；若为良品，则可用数量 = 执行数量，否则可用数量 = 0；分配锁定数 = 0；保存新记录。
     - **若已存在**：更新已有记录。使用 `LambdaUpdateWrapper`，在库数量累加本次执行数。只有在 `isSellable == "1"`（可售/良品）时，才累加更新可用数量 `available_quantity`，不可售/不良品不更新可用数量。
  4. **第四步 写入变更流水日志**：新建并填充 `WmsInventoryTrans` 日志实体，记录操作人、变动数量（ChangeQuantity）、交易类型（TransactionType）、关联单据备注，保存日志。

---

#### 任务 4：自动创建上架任务与执行上架（库存转移）实战
“收货”把货物临时放进了 A01（暂存区），现在我们需要开发“上架”功能，将货物转移到正式货位。

**4.1 自动创建上架任务逻辑（WmsTasksServiceImpl.java 中的私有方法）**
- **任务**：实现 `createPutawayTask(String stockInOrderId)`。
- **逻辑**：
  1. 根据 `stockInOrderId` 查询该单据下所有已经收货的**良品（良品才上架，不良品不生成上架任务）**的收货明细记录（`WmsTasksRecords`）。
  2. 遍历这些收货明细，为每一条记录创建一条全新的上架任务（`WmsTasks`）：
     - `taskType` 设为 上架（`TASK_TYPE_PUTAWAY`）。
     - `taskStatus` 设为 已创建（`TASK_STATUS_CREATED`）。
     - `sourceLocationCode` 设为 收货时的目标储位（即暂存区储位）。
     - `quantity` 设为 本次收货数量。
     - 补全关联的商品ID、入库单ID、仓库ID、执行人等主档信息，保存上架任务。

**4.2 查询待上架任务列表 `PutawayTasksController.java`**
- **路径**：`org.jeecg.modules.wms.inorder.controller`
- **任务**：提供 `list` 分页查询接口（`/list`）。
- **细节**：过滤 `taskType` 必须为 `TASK_TYPE_PUTAWAY`，过滤操作人为当前登录用户的 ID（`sysUser.getId()`），调用 `wmsTasksService.list` 进行分页。在对应的 Mapper SQL 中，需通过多表关联查询，输出商品名称和储位名称。

**4.3 上架库存转移业务逻辑类 `WmsInventoryTransByPutway.java`**
- **路径**：`org.jeecg.modules.wms.inventory.service.impl`
- **类注解**：实现接口 `IWmsInventoryTransService`，添加 `@Service("wmsInventoryTransByPutway")`。
- **转移核心逻辑 (`transfer`方法)**：
  1. **来源储位扣减**：根据“商品+来源储位（暂存区）+批次”查询来源库存记录。校验库存是否存在且在库数量足够。若满足，在库数量扣减本次上架数量；若是良品，可用数量同步扣减。
  2. **目的储位累加**：根据“商品+目的储位（正式货架）+批次”查询目的库存记录。若不存在则新建（可用数量和在库数量等于上架数）；若已存在，累加在库数量及可用数量。
  3. **变更日志记录**：记录库存转移日志。

---

请现在作为最顶级的 Java 专家，为我生成上面描述中需要新建和修改的、完整的、生产级别的 Java 核心代码！代码要求具有极高的鲁棒性，且完美符合 MyBatis-Plus 与 Spring Boot 3 的开发规范。




相关文件名的列举和说明：
为了支持通用的库存变更，我们需要手动在 jeecg-module-wms 模块中创建以下新类：
文件名称	完整包路径	作用说明
WmsInventoryTransParam.java	org.jeecg.modules.wms.inventory.vo	库存变更的通用参数封装实体类
WmsInventoryTransByReceiving.java	org.jeecg.modules.wms.inventory.service.impl	继承 ServiceImpl 并实现 IWmsInventoryTransService，专用于收货场景下的库存增加与流水写入。标注为 @Service("wmsInventoryTransByReceiving")
WmsInventoryTransByPutway.java	org.jeecg.modules.wms.inventory.service.impl	继承 ServiceImpl 并实现 IWmsInventoryTransService，专用于上架场景下的库存转移（来源储位扣减，目标储位增加）。标注为 @Service("wmsInventoryTransByPutway")
3. 📝 【需要修改/重构】的已有文件
以下是需要将核心逻辑和新方法写入或替换的已有类：
文件名称	完整包路径	修改/重构重点
ReceiveTasksController.java	org.jeecg.modules.wms.inorder.controller	新增 @PostMapping("/addRecords")，转换前端传的 ID 并触发收货。
IWmsTasksService.java	org.jeecg.modules.wms.wmstask.service	声明 void receive(WmsTasksRecords wmsTasksRecords); 接口方法。
WmsTasksServiceImpl.java	org.jeecg.modules.wms.wmstask.service.impl	核心重构文件。需要重写 receive 总控方法，实现私有 execute（校验数量与不良品储位），引入 wmsInventoryTransByReceiving 执行库存同步，并实现 createPutawayTask 自动创建上架任务。
IWmsStockInOrderItemsService.java	org.jeecg.modules.wms.inorder.service	声明更新明细状态接口 void updateReceivedStatus(String stockInOrderItemId);。
WmsStockInOrderItemsServiceImpl.java	org.jeecg.modules.wms.inorder.service.impl	实现明细更新逻辑。通过 Stream 流累计已收良品/不良品数，满足条件后扭转明细状态。
IWmsStockInOrdersService.java	org.jeecg.modules.wms.inorder.service	声明更新主表状态接口 String updateReceivedStatus(String stockInOrderId);。
WmsStockInOrdersServiceImpl.java	org.jeecg.modules.wms.inorder.service.impl	实现主表更新。汇总所有明细完成数，判断是否全收完，同步修改单据状态为收货中或收货完成。
IWmsInventoryService.java	org.jeecg.modules.wms.inventory.service	声明根据商品+储位+批次唯一约束查询的方法 getInventoryByUniqueKey。
WmsInventoryServiceImpl.java	org.jeecg.modules.wms.inventory.service.impl	实现 getInventoryByUniqueKey 查询，需处理 batchNumber 为空字符串时的特殊逻辑。
PutawayTasksController.java	org.jeecg.modules.wms.inorder.controller	编写待上架任务分页查询接口 /list（过滤上架类型、仅查操作人为当前用户、关联 SQL 查出商品/储位名）以及查询上架明细记录接口 /records。