package org.jeecg.modules.wms.goods.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.wms.goods.entity.WmsCargoOwners;
import org.jeecg.modules.wms.goods.mapper.WmsCargoOwnersMapper;
import org.jeecg.modules.wms.goods.service.IWmsCargoOwnersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;


@Service
public class WmsCargoOwnersServiceImpl extends ServiceImpl<WmsCargoOwnersMapper, WmsCargoOwners> implements IWmsCargoOwnersService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WmsCargoOwnersMapper wmsCargoOwnersMapper;

    // Redis 中存储自增序号的 Key
    private static final String REDIS_INCR_KEY = "WMS_CARGO_OWNERS_CODE";

    /**
     * 新增货主（重写新增逻辑，实现自动发号）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(WmsCargoOwners cargoOwners) {
        // 1. 自动生成货主编码
        String ownerCode = generateOwnerCode();
        // 2. 给实体设置编码
        cargoOwners.setOwnerCode(ownerCode);
        // 3. 保存到数据库
        this.save(cargoOwners);
    }

    /**
     * 并发安全编码发号器
     */
    private String generateOwnerCode() {
        try {
            while (true) {
                // 第一步：Redis 自增生成基础编码序号
                long seq = redisUtil.incr(REDIS_INCR_KEY, 1);
                String seqStr = String.format("%05d", seq);
                String code = "C" + seqStr;

                // 第二步：查询数据库，判断该编码是否已存在
                Long count = wmsCargoOwnersMapper.checkCodeExists(code);
                if (count == null || count == 0) {
                    // 编码不存在，可以安全使用，直接返回
                    return code;
                }

                // 第三步：若编码存在，说明 Redis 序列号落后了，开始自动校准
                String maxOwnerCode = wmsCargoOwnersMapper.selectMaxOwnerCode();
                String seqStrDB = maxOwnerCode.substring("C".length());
                Long maxSeq = Long.parseLong(seqStrDB);
                if (maxSeq == null) {
                    throw new JeecgBootException("数据库中存在非法货主编码：" + maxOwnerCode);
                }

                // 将 Redis 的序列校准更新为数据库中的当前最大值
                redisUtil.set(REDIS_INCR_KEY, maxSeq);
            }
        } catch (Exception e) {
            throw new JeecgBootException("货主编码自动生成失败：" + e.getMessage());
        }
    }
}