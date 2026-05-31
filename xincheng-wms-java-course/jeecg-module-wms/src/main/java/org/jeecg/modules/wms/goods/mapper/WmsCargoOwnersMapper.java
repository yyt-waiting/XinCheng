package org.jeecg.modules.wms.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * @Description: 货主表
 * @Author: jeecg-boot
 * @Date:   2026-05-30
 * @Version: V1.0
 */

@Repository
public interface WmsCargoOwnersMapper extends BaseMapper<WmsCargoOwners> {

    /**
     * 查询编码是否存在（返回数量）
     */
    @Select("SELECT COUNT(1) FROM wms_cargo_owners WHERE owner_code = #{code}")
    Long checkCodeExists(String code);

    /**
     * 查询数据库中最大的货主编码
     */
    @Select("select owner_code from wms_cargo_owners order by owner_code desc limit 1")
    String selectMaxOwnerCode();
}
