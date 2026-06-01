package org.jeecg.modules.wms.inventory.service.impl;

import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import org.jeecg.modules.wms.inventory.mapper.WmsInventoryMapper;
import org.jeecg.modules.wms.inventory.service.IWmsInventoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
@Service
public class WmsInventoryServiceImpl extends ServiceImpl<WmsInventoryMapper, WmsInventory> implements IWmsInventoryService {

    @Override
    public WmsInventory getInventoryByUniqueKey(String productId, String locationCode, String batchNumber) {
        LambdaQueryWrapper<WmsInventory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmsInventory::getProductId, productId)
                .eq(WmsInventory::getLocationCode, locationCode);
        if (!StringUtils.hasText(batchNumber)) {
            queryWrapper.eq(WmsInventory::getBatchNumber, "");
        } else {
            queryWrapper.eq(WmsInventory::getBatchNumber, batchNumber);
        }
        return this.getOne(queryWrapper);
    }
}
