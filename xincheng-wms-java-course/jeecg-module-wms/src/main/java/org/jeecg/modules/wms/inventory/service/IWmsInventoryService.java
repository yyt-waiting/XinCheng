package org.jeecg.modules.wms.inventory.service;

import org.jeecg.modules.wms.inventory.entity.WmsInventory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 库存表服务接口
 */
public interface IWmsInventoryService extends IService<WmsInventory> {

    /**
     * 根据唯一键查询库存（商品ID + 储位编码 + 批号）
     * @param productId 商品ID
     * @param locationCode 储位编码
     * @param batchNumber 批号
     * @return 库存记录，不存在返回null
     */
    WmsInventory getInventoryByUniqueKey(String productId, String locationCode, String batchNumber);
}
