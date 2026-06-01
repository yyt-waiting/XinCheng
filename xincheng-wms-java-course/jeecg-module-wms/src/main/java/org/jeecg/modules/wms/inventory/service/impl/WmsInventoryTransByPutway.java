package org.jeecg.modules.wms.inventory.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryTransMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryTransService;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import org.jeecg.modules.wms.goods.entity.WmsProducts;
import org.jeecg.modules.wms.goods.service.IWmsProductsService;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.util.Date;

/**
 * 上架库存变更实现
 */
@Slf4j
@Service("wmsInventoryTransByPutway")
public class WmsInventoryTransByPutway
        extends ServiceImpl<WmsInventoryTransMapper, WmsInventoryTrans>
        implements IWmsInventoryTransService {

    private final IWmsInventoryService wmsInventoryService;
    private final IWmsProductsService wmsProductsService;

    public WmsInventoryTransByPutway(
            IWmsInventoryService wmsInventoryService,
            IWmsProductsService wmsProductsService) {
        this.wmsInventoryService = wmsInventoryService;
        this.wmsProductsService = wmsProductsService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(WmsInventoryTransParam param) {
        // Step 1 - Validation
        String productId = param.getProductId();
        Integer execQuantity = param.getExecQuantity();
        String sourceLocationCode = param.getSourceLocationCode();
        String targetLocationCode = param.getTargetLocationCode();
        String batchNumber = param.getBatchNumber();
        String isSellable = param.getIsSellable();
        Date operationTime = param.getOperationTime();

        if (!StringUtils.hasText(productId)) {
            throw new JeecgBootException("商品ID不能为空");
        }
        if (execQuantity == null || execQuantity <= 0) {
            throw new JeecgBootException("执行数量必须大于0");
        }
        if (!StringUtils.hasText(sourceLocationCode)) {
            throw new JeecgBootException("来源储位编码不能为空");
        }
        if (!StringUtils.hasText(targetLocationCode)) {
            throw new JeecgBootException("目的储位编码不能为空");
        }

        // Check product exists
        WmsProducts product = wmsProductsService.getById(productId);
        if (product == null) {
            throw new JeecgBootException("商品不存在，productId=" + productId);
        }

        // Step 2 - Deduct source inventory
        deductSourceInventory(productId, sourceLocationCode, batchNumber, execQuantity, isSellable);

        // Step 3 - Add target inventory
        addTargetInventory(productId, targetLocationCode, batchNumber, execQuantity, isSellable, product, operationTime);

        // Step 4 - Record trans log
        recordTransLog(param, targetLocationCode);
    }

    /**
     * 来源储位扣减库存
     */
    private void deductSourceInventory(String productId, String sourceLocationCode,
                                       String batchNumber, Integer execQuantity, String isSellable) {
        WmsInventory sourceInventory = wmsInventoryService.getInventoryByUniqueKey(
                productId, sourceLocationCode, batchNumber);
        if (sourceInventory == null) {
            throw new JeecgBootException("来源储位库存不存在，locationCode=" + sourceLocationCode + ", productId=" + productId);
        }

        Integer currentStock = sourceInventory.getStockQuantity();
        if (currentStock == null || currentStock < execQuantity) {
            throw new JeecgBootException("来源储位库存不足，当前库存=" + currentStock + ", 需要扣减=" + execQuantity);
        }

        LambdaUpdateWrapper<WmsInventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(WmsInventory::getId, sourceInventory.getId());
        // Always deduct stock_quantity
        updateWrapper.setSql("stock_quantity = stock_quantity - " + execQuantity);
        // Deduct available_quantity only when isSellable == "1"
        if ("1".equals(isSellable)) {
            updateWrapper.setSql("available_quantity = available_quantity - " + execQuantity);
        }
        // Sync update allocated_quantity
        updateWrapper.setSql("allocated_quantity = allocated_quantity - " + execQuantity);
        wmsInventoryService.update(updateWrapper);
        log.info("来源储位库存扣减成功，inventoryId={}, 扣减stockQuantity={}, 扣减availableQuantity={}",
                sourceInventory.getId(), execQuantity, "1".equals(isSellable) ? execQuantity : 0);
    }

    /**
     * 目标储位增加库存
     */
    private void addTargetInventory(String productId, String targetLocationCode,
                                    String batchNumber, Integer execQuantity,
                                    String isSellable, WmsProducts product, Date operationTime) {
        WmsInventory existingInventory = wmsInventoryService.getInventoryByUniqueKey(
                productId, targetLocationCode, batchNumber);

        if (existingInventory == null) {
            // No existing inventory - create new record
            WmsInventory newInventory = new WmsInventory();
            newInventory.setOwnerId(product.getOwnerId());
            newInventory.setProductId(productId);
            newInventory.setLocationCode(targetLocationCode);
            newInventory.setBatchNumber(batchNumber);
            newInventory.setStockQuantity(execQuantity);
            // available_quantity: only increase when isSellable == "1"
            newInventory.setAvailableQuantity("1".equals(isSellable) ? execQuantity : 0);
            newInventory.setAllocatedQuantity(0);
            newInventory.setIsSellable(isSellable);
            newInventory.setStockInTime(operationTime != null ? operationTime : new Date());
            wmsInventoryService.save(newInventory);
            log.info("目标储位新增库存记录成功，productId={}, locationCode={}, stockQuantity={}",
                    productId, targetLocationCode, execQuantity);
        } else {
            // Existing inventory - accumulate
            LambdaUpdateWrapper<WmsInventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WmsInventory::getId, existingInventory.getId());
            // Always increase stock_quantity
            updateWrapper.setSql("stock_quantity = stock_quantity + " + execQuantity);
            // Increase available_quantity only when isSellable == "1"
            if ("1".equals(isSellable)) {
                updateWrapper.setSql("available_quantity = available_quantity + " + execQuantity);
            }
            wmsInventoryService.update(updateWrapper);
            log.info("目标储位更新库存记录成功，inventoryId={}, 增加stockQuantity={}, 增加availableQuantity={}",
                    existingInventory.getId(), execQuantity,
                    "1".equals(isSellable) ? execQuantity : 0);
        }
    }

    /**
     * 记录库存变更日志
     */
    private void recordTransLog(WmsInventoryTransParam param, String locationCode) {
        WmsInventoryTrans transRecord = new WmsInventoryTrans();
        BeanUtils.copyProperties(param, transRecord);
        transRecord.setLocationCode(locationCode);
        transRecord.setTransactionType(WarehouseDictEnum.INVENTORY_PUTAWAY.getCode());
        transRecord.setChangeQuantity(param.getExecQuantity());
        transRecord.setCreateBy(param.getOperator());
        transRecord.setCreateTime(new Date());
        transRecord.setUpdateBy(param.getOperator());
        transRecord.setUpdateTime(new Date());
        transRecord.setTransactionTime(param.getOperationTime() != null ? param.getOperationTime() : new Date());
        transRecord.setRemarks("上架: 从[" + param.getSourceLocationCode() + "]转移至[" + locationCode + "]");
        this.save(transRecord);
        log.info("上架库存变更日志保存成功，transRecordId={}", transRecord.getId());
    }
}
