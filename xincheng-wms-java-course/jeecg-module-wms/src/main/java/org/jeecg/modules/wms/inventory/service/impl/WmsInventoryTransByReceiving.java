package org.jeecg.modules.wms.inventory.service.impl;

import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryTransMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryTransService;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.warehouse.service.IWmsStorageLocationsService;
import org.jeecg.modules.wms.warehouse.entity.WmsStorageLocations;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 收货库存变更实现
 */
@Slf4j
@Service("wmsInventoryTransByReceiving")
public class WmsInventoryTransByReceiving
        extends ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans>
        implements IWmsInventoryTransService {

    private final IWmsProductsService wmsProductsService;
    private final IWmsStorageLocationsService wmsStorageLocationsService;
    private final IWmsInventoryService wmsInventoryService;

    public WmsInventoryTransByReceiving(
            IWmsProductsService wmsProductsService,
            IWmsStorageLocationsService wmsStorageLocationsService,
            IWmsInventoryService wmsInventoryService) {
        this.wmsProductsService = wmsProductsService;
        this.wmsStorageLocationsService = wmsStorageLocationsService;
        this.wmsInventoryService = wmsInventoryService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(WmsInventoryTransParam inventoryTransParam) {
        // Step 1 - Validation
        String targetLocationCode = inventoryTransParam.getTargetLocationCode();
        String productId = inventoryTransParam.getProductId();
        Integer execQuantity = inventoryTransParam.getExecQuantity();
        String isSellable = inventoryTransParam.getIsSellable();
        String batchNumber = inventoryTransParam.getBatchNumber();
        String operator = inventoryTransParam.getOperator();
        Date operationTime = inventoryTransParam.getOperationTime();

        // Check required fields
        if (!StringUtils.hasText(targetLocationCode)) {
            throw new JeecgBootException("目的储位编码不能为空");
        }
        if (!StringUtils.hasText(productId)) {
            throw new JeecgBootException("商品ID不能为空");
        }
        if (execQuantity == null || execQuantity <= 0) {
            throw new JeecgBootException("执行数量必须大于0");
        }
        if (!StringUtils.hasText(isSellable)) {
            throw new JeecgBootException("是否可售不能为空");
        }

        // Check product exists
        WmsProducts product = wmsProductsService.getById(productId);
        if (product == null) {
            throw new JeecgBootException("商品不存在，productId=" + productId);
        }

        // Check location exists
        LambdaQueryWrapper<WmsStorageLocations> locationQueryWrapper = new LambdaQueryWrapper<>();
        locationQueryWrapper.eq(WmsStorageLocations::getLocationCode, targetLocationCode);
        WmsStorageLocations location = wmsStorageLocationsService.getOne(locationQueryWrapper);
        if (location == null) {
            throw new JeecgBootException("储位不存在，locationCode=" + targetLocationCode);
        }

        // Step 2 - Query existing inventory
        WmsInventory existingInventory = wmsInventoryService.getInventoryByUniqueKey(
                productId, targetLocationCode, batchNumber);

        // Step 3 - Add or update inventory
        if (existingInventory == null) {
            // New inventory record
            WmsInventory newInventory = new WmsInventory();
            BeanUtils.copyProperties(inventoryTransParam, newInventory);
            newInventory.setOwnerId(product.getOwnerId());
            newInventory.setStockQuantity(execQuantity);
            newInventory.setLocationCode(targetLocationCode);
            // available_quantity: only increase when isSellable == "1"
            newInventory.setAvailableQuantity("1".equals(isSellable) ? execQuantity : 0);
            newInventory.setAllocatedQuantity(0);
            newInventory.setIsSellable(isSellable);
            newInventory.setStockInTime(operationTime != null ? operationTime : new Date());
            wmsInventoryService.save(newInventory);
            log.info("新增库存记录成功，productId={}, locationCode={}, stockQuantity={}",
                    productId, targetLocationCode, execQuantity);
        } else {
            // Update existing inventory
            LambdaUpdateWrapper<WmsInventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WmsInventory::getId, existingInventory.getId());
            // Always increase stock_quantity
            updateWrapper.setSql("stock_quantity = stock_quantity + " + execQuantity);
            // Increase available_quantity only when isSellable == "1"
            if ("1".equals(isSellable)) {
                updateWrapper.setSql("available_quantity = available_quantity + " + execQuantity);
            }
            wmsInventoryService.update(updateWrapper);
            log.info("更新库存记录成功，inventoryId={}, 增加stockQuantity={}, 增加availableQuantity={}",
                    existingInventory.getId(), execQuantity,
                    "1".equals(isSellable) ? execQuantity : 0);
        }

        // Step 4 - Save trans log
        WmsInventoryTrans transRecord = new WmsInventoryTrans();
        BeanUtils.copyProperties(inventoryTransParam, transRecord);
        transRecord.setLocationCode(targetLocationCode);
        transRecord.setCreateBy(operator);
        transRecord.setCreateTime(new Date());
        transRecord.setUpdateBy(operator);
        transRecord.setUpdateTime(new Date());
        transRecord.setChangeQuantity(execQuantity);
        transRecord.setTransactionTime(operationTime != null ? operationTime : new Date());
        transRecord.setRemarks("收货,向库存新增记录,入库单:" + (inventoryTransParam.getRemarks() != null ? inventoryTransParam.getRemarks() : ""));
        this.save(transRecord);
        log.info("库存变更日志保存成功，transRecordId={}", transRecord.getId());
    }
}
