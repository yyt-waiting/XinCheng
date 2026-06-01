package org.jeecg.modules.wms.wmstask.service.impl;

import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksMapper;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryTransService;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
@Service
@Slf4j
public class WmsTasksServiceImpl extends ServiceImpl<WmsTasksMapper, WmsTasks> implements IWmsTasksService {

    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;
    @Autowired
    private IWmsStockInOrdersService stockInOrdersService;
    @Autowired
    private IWmsStockInOrderItemsService stockInOrderItemsService;
    @Autowired
    private IWmsStorageLocationsService wmsStorageLocationsService;
    @Autowired
    private IWmsInventoryTransService wmsInventoryTransByReceiving;
    @Autowired
    @Qualifier("wmsInventoryTransByPutway")
    private IWmsInventoryTransService wmsInventoryTransByPutway;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(WmsTasksRecords wmsTasksRecords) {
        // 1. 执行收货任务（记录 + 更新任务）
        WmsTasks wmsTasks = execute(wmsTasksRecords);
        // 2. 更新入库单明细的数量和状态
        stockInOrderItemsService.updateReceivedStatus(wmsTasks.getStockInOrderItemId());
        // 3. 更新入库单的数量和状态
        String inOrderStatus = stockInOrdersService.updateReceivedStatus(wmsTasks.getStockInOrderId());
        // 4. 更新库存
        WmsInventoryTransParam inventoryTransParam = new WmsInventoryTransParam();
        inventoryTransParam.setProductId(wmsTasksRecords.getProductId());
        inventoryTransParam.setExecQuantity(wmsTasksRecords.getExecQuantity());
        inventoryTransParam.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
        inventoryTransParam.setTargetLocationCode(wmsTasksRecords.getTargetLocationCode());
        inventoryTransParam.setBatchNumber(wmsTasksRecords.getBatchNumber());
        inventoryTransParam.setExpiryDate(wmsTasksRecords.getExpiryDate());
        inventoryTransParam.setTransactionType(WarehouseDictEnum.INVENTORY_RECEIVING.getCode());
        inventoryTransParam.setRemarks(wmsTasksRecords.getStockInOrderId());
        inventoryTransParam.setOperationTime(wmsTasksRecords.getOperationTime());
        // 良品→可售（1）；不良品→不可售（0）
        inventoryTransParam.setIsSellable(WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode()
            .equals(wmsTasksRecords.getInventoryAttribute()) ? "1" : "0");
        wmsInventoryTransByReceiving.transfer(inventoryTransParam);
        // 5. 若入库单收完，创建上架任务
        if ("4".equals(inOrderStatus)) {
            createPutawayTask(wmsTasksRecords.getStockInOrderId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private WmsTasks execute(WmsTasksRecords wmsTasksRecords) {
        String taskId = wmsTasksRecords.getTaskId();
        WmsTasks wmsTasks = this.getById(taskId);
        Integer execQuantity = wmsTasksRecords.getExecQuantity();
        Integer expectedQuantity = wmsTasks.getQuantity();
        Integer completedQuantity = wmsTasks.getCompletedQuantity();
        if (expectedQuantity == null || expectedQuantity == 0) {
            throw new JeecgBootException("任务计划数量异常");
        }
        // 约束1：总执行数量不能大于预期计划数
        if (completedQuantity == null) {
            completedQuantity = 0;
        }
        if (completedQuantity + execQuantity > expectedQuantity) {
            throw new JeecgBootException("总执行数量不能大于计划数量");
        }
        // 约束2：不良品不能放在可售储位
        String inventoryAttribute = wmsTasksRecords.getInventoryAttribute();
        String locationCode = wmsTasksRecords.getTargetLocationCode();
        WmsStorageLocations wmsStorageLocations = wmsStorageLocationsService.getOne(
            new LambdaQueryWrapper<WmsStorageLocations>()
                .eq(WmsStorageLocations::getLocationCode, locationCode)
        );
        String isSellable = wmsStorageLocations.getIsSellable();
        if ("1".equals(isSellable) && WarehouseDictEnum.INVENTORY_ATTRIBUTE_DEFECTIVE.getCode().equals(inventoryAttribute)) {
            throw new JeecgBootException("不能将不良品存放在可售储位");
        }
        // 1. 添加任务执行记录
        wmsTasksRecords.setTargetWarehouseId(wmsTasks.getTargetWarehouseId());
        wmsTasksRecords.setStockInOrderId(wmsTasks.getStockInOrderId());
        wmsTasksRecords.setStockInOrderItemId(wmsTasks.getStockInOrderItemId());
        wmsTasksRecords.setWaveOrderId(wmsTasks.getWaveOrderId());
        wmsTasksRecords.setWaveSkuSummaryId(wmsTasks.getWaveSkuSummaryId());
        wmsTasksRecords.setTaskId(wmsTasks.getId());
        wmsTasksRecords.setProductId(wmsTasks.getProductId());
        wmsTasksRecords.setTaskNumber(wmsTasks.getTaskNumber());
        wmsTasksRecords.setTaskType(wmsTasks.getTaskType());
        wmsTasksRecords.setOperationTime(new Date());
        wmsTasksRecords.setOperator(wmsTasks.getOperator());
        boolean save = wmsTasksRecordsService.save(wmsTasksRecords);
        if (!save) {
            throw new JeecgBootException("添加任务执行记录失败");
        }
        // 2. 在任务表增加完成收货数量
        boolean update = update(new LambdaUpdateWrapper<WmsTasks>()
            .eq(WmsTasks::getId, taskId)
            .setSql("completed_quantity = completed_quantity + " + execQuantity));
        if (!update) {
            throw new JeecgBootException("更新任务完成数量失败");
        }
        // 3. 更新任务状态，如果完成数量等于计划数量则任务状态更新为完成
        WmsTasks taskDb = getById(taskId);
        if (taskDb.getCompletedQuantity() != null
                && taskDb.getQuantity() != null
                && taskDb.getCompletedQuantity().equals(taskDb.getQuantity())) {
            update(new LambdaUpdateWrapper<WmsTasks>()
                .eq(WmsTasks::getId, taskId)
                .set(WmsTasks::getTaskStatus, WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode()));
        }
        return taskDb;
    }

    private void createPutawayTask(String stockInOrderId) {
        // 查询入库单所有良品收货记录
        List<WmsTasksRecords> records = wmsTasksRecordsService.list(
            new LambdaQueryWrapper<>(WmsTasksRecords.class)
                .eq(WmsTasksRecords::getStockInOrderId, stockInOrderId)
                .eq(WmsTasksRecords::getInventoryAttribute, WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode())
        );
        for (WmsTasksRecords record : records) {
            WmsTasks putawayTask = new WmsTasks();
            putawayTask.setId(UUID.randomUUID().toString());
            putawayTask.setTaskNumber("PA" + (System.currentTimeMillis() / 1000) + (int)(Math.random() * 10000));
            putawayTask.setTaskType(WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode());
            putawayTask.setTaskStatus(WarehouseDictEnum.TASK_STATUS_CREATED.getCode());
            putawayTask.setSourceLocationCode(record.getTargetLocationCode());
            putawayTask.setProductId(record.getProductId());
            putawayTask.setQuantity(record.getExecQuantity());
            putawayTask.setCompletedQuantity(0);
            putawayTask.setStockInOrderId(stockInOrderId);
            putawayTask.setStockInOrderItemId(record.getStockInOrderItemId());
            putawayTask.setSourceWarehouseId(record.getTargetWarehouseId());
            putawayTask.setTargetWarehouseId(record.getTargetWarehouseId());
            putawayTask.setOperator(record.getOperator());
            putawayTask.setOperationTime(new Date());
            putawayTask.setBatchNumber(record.getBatchNumber());
            putawayTask.setExpiryDate(record.getExpiryDate());
            this.save(putawayTask);
        }
    }

    /**
     * 执行上架
     * 1. 校验任务存在
     * 2. 校验库存充足
     * 3. 执行库存转移（来源储位扣减，目标储位增加）
     * 4. 记录任务执行记录
     * 5. 更新任务完成数量
     * 6. 更新任务状态（完成则标记完成）
     * @param wmsTasksRecords 上架执行记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void putaway(WmsTasksRecords wmsTasksRecords) {
        // 1. 获取上架任务
        String taskId = wmsTasksRecords.getTaskId();
        WmsTasks putawayTask = this.getById(taskId);
        if (putawayTask == null) {
            throw new JeecgBootException("上架任务不存在");
        }

        // 2. 参数设置到记录
        wmsTasksRecords.setTaskId(taskId);
        wmsTasksRecords.setProductId(putawayTask.getProductId());
        wmsTasksRecords.setTaskNumber(putawayTask.getTaskNumber());
        wmsTasksRecords.setTaskType(putawayTask.getTaskType());
        wmsTasksRecords.setSourceLocationCode(putawayTask.getSourceLocationCode());
        wmsTasksRecords.setStockInOrderId(putawayTask.getStockInOrderId());
        wmsTasksRecords.setStockInOrderItemId(putawayTask.getStockInOrderItemId());
        wmsTasksRecords.setWaveOrderId(putawayTask.getWaveOrderId());
        wmsTasksRecords.setWaveSkuSummaryId(putawayTask.getWaveSkuSummaryId());
        wmsTasksRecords.setSourceWarehouseId(putawayTask.getSourceWarehouseId());
        wmsTasksRecords.setTargetWarehouseId(putawayTask.getTargetWarehouseId());
        wmsTasksRecords.setOperator(putawayTask.getOperator());
        wmsTasksRecords.setOperationTime(new Date());
        wmsTasksRecords.setBatchNumber(putawayTask.getBatchNumber());
        wmsTasksRecords.setExpiryDate(putawayTask.getExpiryDate());

        // 3. 执行库存转移
        WmsInventoryTransParam param = new WmsInventoryTransParam();
        param.setProductId(wmsTasksRecords.getProductId());
        param.setExecQuantity(wmsTasksRecords.getExecQuantity());
        param.setSourceLocationCode(wmsTasksRecords.getSourceLocationCode());
        param.setTargetLocationCode(wmsTasksRecords.getTargetLocationCode());
        param.setBatchNumber(wmsTasksRecords.getBatchNumber());
        param.setWarehouseId(wmsTasksRecords.getTargetWarehouseId());
        param.setTransactionType(WarehouseDictEnum.INVENTORY_PUTAWAY.getCode());
        param.setRemarks("上架," + wmsTasksRecords.getStockInOrderId());
        param.setOperationTime(wmsTasksRecords.getOperationTime());
        param.setIsSellable("1"); // 上架的是良品
        param.setOperator(wmsTasksRecords.getOperator());

        wmsInventoryTransByPutway.transfer(param);

        // 4. 添加任务执行记录
        wmsTasksRecordsService.save(wmsTasksRecords);

        // 5. 更新任务完成数量
        this.update(new LambdaUpdateWrapper<WmsTasks>()
            .eq(WmsTasks::getId, taskId)
            .setSql("completed_quantity = completed_quantity + " + wmsTasksRecords.getExecQuantity()));

        // 6. 检查是否完成，更新状态
        WmsTasks taskDb = this.getById(taskId);
        if (taskDb.getCompletedQuantity() != null
                && taskDb.getQuantity() != null
                && taskDb.getCompletedQuantity().equals(taskDb.getQuantity())) {
            this.update(new LambdaUpdateWrapper<WmsTasks>()
                .eq(WmsTasks::getId, taskId)
                .set(WmsTasks::getTaskStatus, WarehouseDictEnum.TASK_STATUS_COMPLETED.getCode()));
        }
    }

}
