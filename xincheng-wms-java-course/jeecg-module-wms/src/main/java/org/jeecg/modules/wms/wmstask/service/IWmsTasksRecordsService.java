package org.jeecg.modules.wms.wmstask.service;

import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 任务执行记录表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
public interface IWmsTasksRecordsService extends IService<WmsTasksRecords> {

    /**
     * 查询上架任务执行记录（分页）
     * @param taskId 任务ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 上架记录分页列表
     */
    IPage<WmsTasksRecords> queryPutawayRecords(String taskId, Integer pageNo, Integer pageSize);

    /**
     * 查询收货任务执行记录（分页）
     * @param taskNumber 任务号（模糊查询）
     * @param taskType 任务类型
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 收货记录分页列表
     */
    IPage<WmsTasksRecords> queryReceiveRecords(String taskNumber, String taskType, Integer pageNo, Integer pageSize);

}
