package org.jeecg.modules.wms.goods.service;

import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2026-05-30
 * @Version: V1.0
 */
public interface IWmsCargoOwnersService extends IService<WmsCargoOwners> {

    @Transactional(rollbackFor = Exception.class)
    void add(WmsCargoOwners cargoOwners);
}
