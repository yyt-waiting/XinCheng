2. 库存管理
仓储系统的核心是 "账实一致"—— 系统里的库存数据必须和仓库里的实物完全匹配。
而库存表就是 "记录实物状态" 的核心载体：
• 它要回答：什么商品、在哪个储位、有多少、是什么状态（可售 / 不可售）、
哪个批次。
• 所有操作（收货、上架、出库）最终都会同步到库存表，所以库存表的设计直
接决定了系统的准确性。
2.1 库存表设计
库存表：存储仓库中商品在储位的存放情况
库存变更表：存储库存数量变更的记录
2.2 表结构分析
SQL
create table wms_inventory
(
 id                 
录ID（唯一标识）
 create_by     
的这条库存记录
 create_time        
创建时间
 update_by     
更新了这条记录
 update_time        
更新时间
 sys_org_code       
varchar(36) not null primary key, -- 库存记
     varchar(50) null comment '创建人', -- 谁创建
datetime   
 null comment '创建日期', -- 记录
     varchar(50) null comment '更新人', -- 谁最后
datetime   
 null comment '更新日期', -- 最后
varchar(64) null comment '所属部门', -- 业务
部门（多部门共用仓库时区分）
 product_id    
     varchar(32) not null comment '商品 id', -- 
关联商品表（是什么商品）
 location_code      
商品放在哪个储位
 container_code     
varchar(32) not null comment '储位编码', -- 
varchar(32) null comment '容器编码', -- 商品
所在的容器（比如托盘、货架层）
 stock_quantity     
int        
 not null comment '在库数量', -- 
这个储位下该商品的实际总数
 allocated_quantity int        
订单锁定的数量（不能再用于其他订单）
 available_quantity int        
 null comment '分配数量', -- 已被
 null comment '可用数量', -- 能用
于销售/出库的数量（=在库-分配）
 batch_number       
varchar(32) null comment '批号 ', -- 商品批
次（食品/医药必须，区分不同批次）
 stock_in_time      
datetime   
商品什么时候进的仓库
 expiry_date        
date      
商品过期时间（食品/医药必须）
 owner_id           
 not null comment '入库时间', -- 
  null comment '保质期到期日', -- 
varchar(32) null comment '货主', -- 商品属于
哪个客户（第三方仓储场景）
 is_sellable    
    varchar(32) null comment '是否可售', -- 1=可
售（良品），0=不可售（不良品）
 warehouse_id       
varchar(32) null comment '仓库 id', -- 商品
在哪个仓库
 constraint wms_inventory_unique01
unique (product_id, location_code, batch_number) -- 唯一约
束：同一商品+储位+批次只能有一条记录
) comment '库存表';
关键字段：
1.
2.
3.
unique (product_id, location_code, batch_number)：
￮ 业务意义：同一商品，放在同一个储位，且是同一个批次，只能有一条库存记
录。
￮ 举例："可乐（商品 ID=1）+A1 储位 + 202501 批次" 是唯一的，不能有两条记
录。
stock_quantity/allocated_quantity/available_quantity：
￮ 逻辑关系：可用数量 = 在库数量 - 分配数量（良品场景）。
￮ 举例：在库 100 件，被订单锁定 30 件（分配数量 = 30），则可用数量 = 70 
件。
is_sellable：
￮ 业务意义：区分良品（1）和不良品（0），不良品的available_quantity
永远为 0（不能销售）。
收货是库存的"源头"，我们要实现：收货完成后，自动将货物信息同步到库存表。
收货库存变更逻辑：增加库存时先根据唯一标识查询库存，如果存在则在更新原有库
存记录，不存在则向库存表增加记录。
2.3 收货存储库存
2.3.1 生成库存表代码
通过 JeecgBoot 代码生成器生成wms_inventory（库存表）和
wms_inventory_trans（库存变更表）的代码，包名设为inventory，并删除无关的
WmsInventoryTransServiceImpl 和 TransController（后续自定义库存变更逻辑
）
将生成的代码中下边的类删除：
• 删除 
org.jeecg.modules.wms.inventory.service.impl.WmsInventoryTransServiceImpl
• 删除 
org.jeecg.modules.wms.inventory.controller.WmsInventoryTransController
2.3.2 参数实体类
库存变更是个通用操作（收货、上架、出库都需要），所以先定义一个参数类，封装
所有需要的信息：
Python
package org.jeecg.modules.wms.inventory.vo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
/**
 * @version 1.0
 * @description 库存变更参数类
 */
@Data
public class WmsInventoryTransParam {
    private static final long serialVersionUID = 1L;
    /**商品id*/
    @Excel(name = "商品id", width = 15)
    @Schema(description = "商品id")
    private String productId;
    /**执行数量*/
    @Excel(name = "执行数量", width = 15)
    @Schema(description = "执行数量")
    private Integer execQuantity;
    /**仓库id*/
    @Excel(name = "仓库id", width = 15)
    @Schema(description = "仓库id")
    private java.lang.String warehouseId;
    /**来源储位编码*/
    @Excel(name = "来源储位编码", width = 15)
    @Schema(description = "来源储位编码")
    private String sourceLocationCode;
    /**目的储位编码*/
    @Excel(name = "目的储位编码", width = 15)
    @Schema(description = "目的储位编码")
    private String targetLocationCode;
    /**batchNumber批次号*/
    @Excel(name = "batchNumber批次号", width = 15)
    @Schema(description = "batchNumber批次号")
    private String batchNumber;
    @Excel(name = "保质期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expiryDate;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remarks;
    /**变更类型*/
    @Excel(name = "变更类型", width = 15)
    @Schema(description = "变更类型")
    private String transactionType;
    /**执行人*/
    @Excel(name = "执行人", width = 15)
    @Schema(description = "执行人")
    private String operator;
    /**执行时间*/
    @Excel(name = "执行时间", width = 20, format = "yyyy-MM-dd 
HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd 
HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "执行时间")
    private Date operationTime;
    
    /**是否可售 is_sellable*/
    @Excel(name = "是否可售 is_sellable", width = 15)
    @Schema(description = "是否可售 is_sellable")
    private String isSellable;
}
2.3.3 根据唯一约束查库存
因为库存表有product_id+location_code+batch_number的唯一约束，所以每次
操作前必须先查 "这个组合是否已存在库存记录"：
TypeScript
代码块
// Service接口定义
public interface IWmsInventoryService extends 
IService<WmsInventory> {
    // 根据"商品ID+储位+批次"查库存
    WmsInventory getInventoryByUniqueKey(String productId, String 
locationCode, String batchNumber);
}
// Service实现
@Service
public class WmsInventoryServiceImpl extends 
ServiceImpl<WmsInventoryMapper, WmsInventory> implements 
IWmsInventoryService {
    @Override
    public WmsInventory getInventoryByUniqueKey(String productId, 
String locationCode, String batchNumber) {
        // 构造查询条件：严格匹配"商品ID+储位+批次"
        LambdaQueryWrapper<WmsInventory> query = new 
LambdaQueryWrapper<WmsInventory>()
            .eq(WmsInventory::getProductId, productId)
            .eq(WmsInventory::getLocationCode, locationCode);
        
        // 批次号可能为空，空时匹配空字符串（避免null导致查询失败）
        if (StringUtils.isNotEmpty(batchNumber)) {
            query.eq(WmsInventory::getBatchNumber, batchNumber);
        } else {
            query.eq(WmsInventory::getBatchNumber, "");
        }
        
        // 返回唯一的库存记录（因为有唯一约束，最多一条）
        return getOne(query);
    }
}
2.3.4 收货进行库存变更
创建WmsInventoryTransByReceiving类，专门处理收货场景的库存变更，逻辑分
为"新增库存"和"更新库存"两种情况：
Java
/**
 * @Description: 库存变更表
 * @Author: jeecg-boot
 * @Date:   2026-01-08
 * @Version: V1.0
 */
public interface IWmsInventoryTransService extends 
IService<WmsInventoryTrans> {
      /**
       * 库存变更（通用：收货、上架、拣货等）
       * @param inventoryTransParam
       */
      public void transfer(WmsInventoryTransParam 
inventoryTransParam);
}
Java
@Service
public class WmsInventoryTransByReceiving extends 
ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans> implements 
IWmsInventoryTransService {
   @Autowired
   private IWmsProductsService wmsProductsService;
   @Autowired
   private IWmsStorageLocationsService 
wmsStorageLocationsService;
   @Autowired
   private IWmsInventoryService wmsInventoryService;
   @Override
   @Transactional(rollbackFor = Exception.class)
   public void transfer(WmsInventoryTransParam 
inventoryTransParam) {
    // -------------------------- 第一步：参数合法性校验 -------------------------
    // 1. 非空校验：目标储位、商品ID、执行数量、是否可售必须传
    if 
(StringUtils.isEmpty(inventoryTransParam.getTargetLocationCode())
  || 
StringUtils.isEmpty(inventoryTransParam.getProductId())
  || inventoryTransParam.getExecQuantity() == 
null
  || 
StringUtils.isEmpty(inventoryTransParam.getIsSellable())) {
throw new JeecgBootException("目标储位、商品id、
执行数量、是否可售不能为空");
    }
    // 2. 校验商品和储位的合法性（防止传不存在的ID）
    WmsProducts product = 
wmsProductsService.getById(inventoryTransParam.getProductId());
    if (product == null) {
throw new JeecgBootException("商品 ID[" + 
inventoryTransParam.getProductId() + "]不存在");
            }
            WmsStorageLocations location = 
wmsStorageLocationsService.getOne(
                    new 
LambdaQueryWrapper<WmsStorageLocations>().eq(WmsStorageLocations::
getLocationCode, inventoryTransParam.getTargetLocationCode())
            );
            if (location == null) {
                  throw new JeecgBootException("储位编码[" + 
inventoryTransParam.getTargetLocationCode() + "]不存在");
            }
            // -------------------------- 第二步：查询是否已有库存 -------------------------
            // 根据唯一约束查询库存
            WmsInventory existingInventory = 
wmsInventoryService.getInventoryByUniqueKey(
                    inventoryTransParam.getProductId(), 
inventoryTransParam.getTargetLocationCode(), 
inventoryTransParam.getBatchNumber()
            );
            // -------------------------- 第三步：新增/更新库存 -------------------------
            if (existingInventory == null) {
                  // 情况1：没有库存 → 新增库存记录
                  WmsInventory newInventory = new WmsInventory();
                  // 1. 复制参数中的基础信息（商品、仓库、储位、批次等
）
                  BeanUtils.copyProperties(inventoryTransParam, 
newInventory);
                  // 2. 补充商品关联的货主（从商品表获取）
                  newInventory.setOwnerId(product.getOwnerId());
                  // 3. 设置库存数量：
                  
newInventory.setStockQuantity(inventoryTransParam.getExecQuantity(
));// 在库数量=本次收货数量
                  
newInventory.setLocationCode(inventoryTransParam.getTargetLocation
Code());
                  //如果储位是可售库存，则设置可用库存为数量
                  
newInventory.setAvailableQuantity("1".equals(inventoryTransParam.g
etIsSellable()) ? inventoryTransParam.getExecQuantity() : 0);
                  newInventory.setAllocatedQuantity(0);
                  // 4. 设置入库时间和是否可售
                  
newInventory.setIsSellable(inventoryTransParam.getIsSellable());
                  
newInventory.setStockInTime(inventoryTransParam.getOperationTime()
);
                  // 5. 保存新库存记录
                  wmsInventoryService.save(newInventory);
            } else {
                  // 情况2：已有库存 → 更新库存数量（累加）
                  LambdaUpdateWrapper<WmsInventory> updateWrapper 
= new LambdaUpdateWrapper<WmsInventory>()
                          .eq(WmsInventory::getId, 
existingInventory.getId())// 只更新这条库存记录
                          // 1. 在库数量累加：原数量 + 本次收货数量
                          .setSql("stock_quantity = stock_quantity 
+ " + inventoryTransParam.getExecQuantity())
                          // 2. 可用数量：只有良品才累加，不良品不更
新
                          
.setSql("1".equals(inventoryTransParam.getIsSellable()), 
"available_quantity = available_quantity + " + 
inventoryTransParam.getExecQuantity());
                  // 执行更新
                  wmsInventoryService.update(updateWrapper);
            }
            
            // -------------------------- 第四步：写入库存变更表 -------------------------
            // 1. 构建库存变更记录对象
            WmsInventoryTrans transRecord = new 
WmsInventoryTrans();
            // 2. 复制参数中的基础信息（创建人、商品、储位、批次、变更
类型等）
            BeanUtils.copyProperties(inventoryTransParam, 
transRecord);
            // 3. 补充必要字段（根据wms_inventory_trans表结构）
            BeanUtils.copyProperties(inventoryTransParam, 
transRecord);
            transRecord.setLocationCode( 
inventoryTransParam.getTargetLocationCode());
            
transRecord.setCreateBy(inventoryTransParam.getOperator()); // 执
行人作为创建人
            transRecord.setCreateTime(new Date()); // 当前时间作为
创建时间
            
transRecord.setUpdateBy(inventoryTransParam.getOperator());
            transRecord.setUpdateTime(new Date());
            
transRecord.setChangeQuantity(inventoryTransParam.getExecQuantity(
)); // 变更数量=本次执行数量
            
transRecord.setTransactionTime(inventoryTransParam.getOperationTim
e()); // 变更时间=执行时间
            
transRecord.setTransactionType(inventoryTransParam.getTransactionT
ype()); // 类型
            transRecord.setRemarks("收货,向库存新增记录,入库单:" + 
inventoryTransParam.getRemarks()); //备注
            // 4. 保存库存变更记录
            this.save(transRecord);
      }
}
2.3.5 收货流程中调用库存变更
回到WmsTasksServiceImpl的receive方法（收货的核心方法），在收货完成后，
触发库存同步：
Java
@Service
public class WmsTasksServiceImpl extends 
ServiceImpl<WmsTasksMapper, WmsTasks> implements IWmsTasksService 
{
   
      @Autowired
      @Qualifier("wmsInventoryTransByReceiving")
      private IWmsInventoryTransService 
wmsInventoryTransByReceiving;
      /**
       * 收货
       * @param wmsTasksRecords
       */
      @Override
      @Transactional(rollbackFor = Exception.class)
      public void receive(WmsTasksRecords wmsTasksRecords) {
            // 1.执行收货任务（记录 + 更新任务）
            WmsTasks wmsTasks = execute(wmsTasksRecords);
            // 2.更新入库单明细的数量和状态
            
stockInOrderItemsService.updateReceivedStatus(wmsTasks.getStockInO
rderItemId());
            // 3.更新入库单的数量和状态
            String inOrderStatus = 
stockInOrdersService.updateReceivedStatus(wmsTasks.getStockInOrder
Id());
            // -------------------------- 新增：同步库存 -----------------------
            // 4.更新库存
            WmsInventoryTransParam inventoryTransParam = new 
WmsInventoryTransParam();
            
inventoryTransParam.setProductId(wmsTasksRecords.getProductId());
            
inventoryTransParam.setExecQuantity(wmsTasksRecords.getExecQuantit
y());
            
inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWareho
useId());
            
inventoryTransParam.setTargetLocationCode(wmsTasksRecords.getTarge
tLocationCode());
            
inventoryTransParam.setBatchNumber(wmsTasksRecords.getBatchNumber(
));
            
inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate())
;
            
inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY
_RECEIVING.getCode());
            inventoryTransParam.setRemarks("收货,向库存新增记录,入库
单:"+wmsTasksRecords.getStockInOrderId());
    //保质期到期日
inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate())
;
    //入库时间
inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationT
ime());
    // 
良品→可售（1）；不良品→不可售（0）
inventoryTransParam.setIsSellable(WarehouseDictEnum.INVENTORY_ATTR
IBUTE_GOOD.getCode().equals(wmsTasksRecords.getInventoryAttribute(
)) ? "1" : "0");
wmsInventoryTransByReceiving.transfer(inventoryTransParam);
      }
   