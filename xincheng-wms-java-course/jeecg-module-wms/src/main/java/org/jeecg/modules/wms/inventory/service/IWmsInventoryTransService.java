package org.jeecg.modules.wms.inventory.service;

import org.jeecg.modules.wms.inventory.entity.WmsInventoryTrans;
import org.jeecg.modules.wms.inventory.vo.WmsInventoryTransParam;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 库存变更服务接口
 */
public interface IWmsInventoryTransService extends IService<WmsInventoryTrans> {
    /**
     * 库存变更（通用：收货、上架、拣货等）
     * @param inventoryTransParam 变更参数
     */
    void transfer(WmsInventoryTransParam inventoryTransParam);
}
