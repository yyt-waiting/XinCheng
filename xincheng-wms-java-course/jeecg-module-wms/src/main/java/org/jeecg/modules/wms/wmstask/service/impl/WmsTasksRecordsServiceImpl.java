package org.jeecg.modules.wms.wmstask.service.impl;

import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksRecordsMapper;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;

/**
 * @Description: 任务执行记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
@Service
public class WmsTasksRecordsServiceImpl extends ServiceImpl<WmsTasksRecordsMapper, WmsTasksRecords> implements IWmsTasksRecordsService {

    @Override
    public IPage<WmsTasksRecords> queryPutawayRecords(String taskId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<WmsTasksRecords> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmsTasksRecords::getTaskId, taskId);
        queryWrapper.eq(WmsTasksRecords::getTaskType, WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode());
        queryWrapper.orderByDesc(WmsTasksRecords::getOperationTime);
        Page<WmsTasksRecords> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public IPage<WmsTasksRecords> queryReceiveRecords(String taskNumber, String taskType, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<WmsTasksRecords> queryWrapper = new LambdaQueryWrapper<>();
        if (taskType != null && !taskType.isEmpty()) {
            queryWrapper.eq(WmsTasksRecords::getTaskType, taskType);
        }
        if (taskNumber != null && !taskNumber.isEmpty()) {
            queryWrapper.like(WmsTasksRecords::getTaskNumber, taskNumber);
        }
        queryWrapper.orderByDesc(WmsTasksRecords::getOperationTime);
        Page<WmsTasksRecords> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

}
