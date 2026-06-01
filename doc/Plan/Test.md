1.1.2 Controller 层
org.jeecg.modules.wms.inorder.controller.ReceiveTasksController
TypeScript
代码块
@PostMapping("/addRecords")
public Result<String> addRecords(@RequestBody WmsTasksRecords 
wmsTasksRecords) {
    // 前端传的"id"实际是任务ID，要转存到taskId字段
    String taskId = wmsTasksRecords.getId();
    wmsTasksRecords.setTaskId(taskId);
    wmsTasksRecords.setId(null); // 清空ID，避免覆盖已有记录
    wmsTasksService.receive(wmsTasksRecords);
    return Result.OK("添加成功！");
}
1.1.3 Service层
org.jeecg.modules.wms.wmstask.service.IWmsTasksService
Java
public interface IWmsTasksService extends IService<WmsTasks> {
      /**
       * 收货
       * @param wmsTasksRecords
       */
      void receive(WmsTasksRecords wmsTasksRecords);
}
1.1.4 Service实现类层
org.jeecg.modules.wms.wmstask.service.impl.WmsTasksServiceImpl
receive方法（总控流程）
Java
@Transactional(rollbackFor = Exception.class)
public void receive(WmsTasksRecords wmsTasksRecords) {
    // 步骤1：执行收货任务（记录+更新任务）
    execute(wmsTasksRecords);
    // 步骤2：更新入库单明细的数量和状态
    
stockInOrderItemsService.updateReceivedStatus(wmsTasks.getStockInO
rderItemId());
    // 步骤3：更新入库单的数量和状态
    String inOrderStatus = 
stockInOrdersService.updateReceivedStatus(wmsTasks.getStockInOrder
Id());
    // 步骤4：todo 更新库存
    // 步骤5：若入库单收完，创建上架任务（todo后续实现）
       
if(WarehouseDictEnum.INBOUND_RECEIVED.getCode().equals(inOrderStat
us)){
          createPutawayTask(wmsTasksRecords.getStockInOrderId());
    }
}
步骤1：执行收货任务（记录+更新任务）：execute方法
Java
代码块
@Transactional(rollbackFor = Exception.class)
private void execute(WmsTasksRecords wmsTasksRecords) {
      //约束1：总执行数量（已经执行的数量 + 本次要执行的数量）不能大于
预期计划数
      // 任务对象
      String taskId = wmsTasksRecords.getTaskId();
      WmsTasks wmsTasks = this.getById(taskId);
      // 本次要执行的数量
      Integer execQuantity = wmsTasksRecords.getExecQuantity();
      // 预期收货数量
      Integer expectedQuantity = wmsTasks.getQuantity();
      // 已经执行的数量
      Integer completedQuantity = wmsTasks.getCompletedQuantity();
      // 已经执行的数量 + 本次要执行的数量 不能大于计划数量
      if (completedQuantity + execQuantity > expectedQuantity) {
            throw new JeecgBootException("总执行数量不能大于计划数量
");
      }
      //约束2：不良品不能放在可售储位
      // 获取前端传递的库存属性(良品还是不良品)
      String inventoryAttribute = 
wmsTasksRecords.getInventoryAttribute();
      // 获取前端传递的储位编码
      String locationCode = 
wmsTasksRecords.getTargetLocationCode();
      // 根据储位编码查询对应的储位记录（对象包含了是否可售）
      // select * from wms_storage_locations where location_code = 
#{locationCode}
   LambdaQueryWrapper<WmsStorageLocations> lambdaQueryWrapper = 
new LambdaQueryWrapper<WmsStorageLocations>()
 .eq(WmsStorageLocations::getLocationCode, 
locationCode);
   WmsStorageLocations wmsStorageLocations = 
wmsStorageLocationsService.getOne(lambdaQueryWrapper);
   // 当前储位是否可售 1可售 0不可售
   String isSellable = wmsStorageLocations.getIsSellable();
   if ("1".equals(isSellable) && 
WarehouseDictEnum.INVENTORY_ATTRIBUTE_DEFECTIVE.getCode().equals(i
nventoryAttribute)) {
    throw new JeecgBootException("不能将不良品存放在可售储位
");
   }
   //1.添加任务执行记录
   //拷贝执行任务的信息到任务记录对象、商品id,任务号,任务类型
   //仓库 id
wmsTasksRecords.setTargetWarehouseId(wmsTasks.getTargetWarehouseId
());
   //入库单 id
wmsTasksRecords.setStockInOrderId(wmsTasks.getStockInOrderId());
   //入库单明细 id
wmsTasksRecords.setStockInOrderItemId(wmsTasks.getStockInOrderItem
Id());
   //波次 id 用于波次管理
   wmsTasksRecords.setWaveOrderId(wmsTasks.getWaveOrderId());
   //波次拣货明细id 用于波次管理
wmsTasksRecords.setWaveSkuSummaryId(wmsTasks.getWaveSkuSummaryId()
);
   //任务 id
   wmsTasksRecords.setTaskId(wmsTasks.getId());
   //商品 id
   wmsTasksRecords.setProductId(wmsTasks.getProductId());
   //任务编码
   wmsTasksRecords.setTaskNumber(wmsTasks.getTaskNumber());
   //任务类型
   wmsTasksRecords.setTaskType(wmsTasks.getTaskType());
      //执行时间
      wmsTasksRecords.setOperationTime(new Date());
      //执行人
      wmsTasksRecords.setOperator(wmsTasks.getOperator());
      boolean save = wmsTasksRecordsService.save(wmsTasksRecords);
      if (!save) {
            throw new JeecgBootException("添加任务执行记录失败");
      }
      //2.在任务表增加完成收货数量
      boolean update = update(new LambdaUpdateWrapper<WmsTasks>()
              .eq(WmsTasks::getId, taskId)
              .setSql("completed_quantity = completed_quantity + " 
+ execQuantity));
      if (!update) {
            throw new JeecgBootException("更新任务完成数量失败");
      }
      //3.更新任务状态，如果完成数量等于计划数量则任务状态更新为完成
      WmsTasks taskDb = getById(taskId);
      if 
(taskDb.getCompletedQuantity().equals(taskDb.getQuantity())) {
            update(new LambdaUpdateWrapper<WmsTasks>()
                    .eq(WmsTasks::getId, taskId)
                    .set(WmsTasks::getTaskStatus, 
WarehouseDictEnum.TASK_STATUS_COMPLETED));
      }
}
步骤2：更新入库单明细的数量和状态
org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService
Java
public interface IWmsStockInOrderItemsService extends 
IService<WmsStockInOrderItems> {
     /**
      * 更新收货完成状态
      * @param stockInOrderItemId
      */
      void updateReceivedStatus(String stockInOrderItemId);
}
org.jeecg.modules.wms.inorder.service.impl.WmsStockInOrderItemsServiceImpl
Java
/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2025-12-05
 * @Version: V1.0
 */
@Service
public class WmsStockInOrderItemsServiceImpl extends 
ServiceImpl<WmsStockInOrderItemsMapper, WmsStockInOrderItems> 
implements IWmsStockInOrderItemsService {
  @Autowired
  private IWmsTasksRecordsService wmsTasksRecordsService;
  @Override
  public void updateReceivedStatus(String stockInOrderItemId) {
  // 1. 查明细，查询该明细所有的收货记录
  WmsStockInOrderItems stockInOrderItems = 
this.getById(stockInOrderItemId);
  List<WmsTasksRecords> records  = 
wmsTasksRecordsService.list(new 
LambdaQueryWrapper<>(WmsTasksRecords.class)
  .eq(WmsTasksRecords::getStockInOrderItemId, 
stockInOrderItemId));
  // 2.根据收货记录，汇总良品、不良品的总数量
  int badQuantity  = records.stream().filter(record -> 
record.getInventoryAttribute().equals(WarehouseDictEnum.INVENTORY_
ATTRIBUTE_DEFECTIVE.getCode()))
  .mapToInt(WmsTasksRecords::getExecQuantity).sum();
  // 汇总良品数量
  int goodQuantity  = records.stream().filter(record -> 
record.getInventoryAttribute().equals(WarehouseDictEnum.INVENTORY_
ATTRIBUTE_GOOD.getCode()))
  .mapToInt(WmsTasksRecords::getExecQuantity).sum();
  // 3.更新明细数量，如果收完，把状态改成收货完成
          stockInOrderItems.setReceivedQuantity(goodQuantity);
          stockInOrderItems.setDefectiveQuantity(badQuantity);
          
if(stockInOrderItems.getExpectedQuantity().equals(goodQuantity + 
badQuantity)){
               
stockInOrderItems.setStatus(WarehouseDictEnum.INBOUND_DETAIL_RECEI
VED.getCode());
          }
          this.updateById(stockInOrderItems);
     }
}
步骤3：更新入库单的数量和状态
org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService
Java
public interface IWmsStockInOrdersService extends 
IService<WmsStockInOrders> {
     /**
      * 更新入库单的数量和状态
      */
      String updateReceivedStatus(String stockInOrderId);
}
org.jeecg.modules.wms.inorder.service.impl.WmsStockInOrdersServiceImpl
Java
代码块
@Override
public String updateReceivedStatus(String stockInOrderId) {
    WmsStockInOrders wmsStockInOrders = getById(stockInOrderId);
    //当前状态只有是收货中或审核通过时方可更新收货状态
    
if(!(WarehouseDictEnum.INBOUND_RECEIVING.getCode().equals(wmsStock
InOrders .getStatus())
    || 
WarehouseDictEnum.INBOUND_APPROVED.getCode().equals(wmsStockInOrde
rs .getStatus()))){
    throw new JeecgBootException("非收货中、审核通过状态入库单不允
许更新收货状态");
 }
  // 1.查询该入库单的所有明细
  List<WmsStockInOrderItems> stockInOrderItemsList = 
wmsStockInOrderItemsMapper.selectByMainId(stockInOrderId);
  // 2.汇总数量
  int totalCompletedQuantity   
= 
stockInOrderItemsList.stream().mapToInt(WmsStockInOrderItems::getR
eceivedQuantity).sum();
  int totalBadQuantity  = 
stockInOrderItemsList.stream().mapToInt(WmsStockInOrderItems::getD
efectiveQuantity).sum();
 // anyMatch：只要有一个明细没收完，入库单就还是"收货中"
  boolean hasUnfinished   
= 
stockInOrderItemsList.stream().anyMatch(item -> 
!item.getStatus().equals(WarehouseDictEnum.INBOUND_DETAIL_RECEIVED
.getCode()));
  String status = hasUnfinished ? 
WarehouseDictEnum.INBOUND_RECEIVING.getCode() : 
WarehouseDictEnum.INBOUND_RECEIVED.getCode();
  // 3.更新入库单的不良品数量，良品数量，状态
  //更新收货总量
  WmsStockInOrders stockInOrdersUpdate = 
getById(stockInOrderId);
stockInOrdersUpdate.setTotalReceivedQuantity(totalCompletedQuantit
y);
  //更新不良品数量
stockInOrdersUpdate.setTotalDefectiveQuantity(totalBadQuantity);
  stockInOrdersUpdate.setStatus(status);
  updateById(stockInOrdersUpdate);
  return status;
}