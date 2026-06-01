package org.jeecg.modules.wms.wmstask.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
public interface WmsTasksMapper extends BaseMapper<WmsTasks> {

    IPage<WmsTasks> selectTaskPageWithJoin(Page<WmsTasks> page, @Param("taskType") String taskType,
            @Param("taskNumber") String taskNumber, @Param("taskStatus") String taskStatus,
            @Param("operator") String operator, @Param("stockInOrderNumber") String stockInOrderNumber);

}
