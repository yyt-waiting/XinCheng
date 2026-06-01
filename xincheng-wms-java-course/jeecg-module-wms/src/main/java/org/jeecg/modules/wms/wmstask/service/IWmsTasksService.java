package org.jeecg.modules.wms.wmstask.service;

import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 任务表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
public interface IWmsTasksService extends IService<WmsTasks> {

	/**
	 * 收货
	 * @param wmsTasksRecords 收货记录
	 */
	void receive(WmsTasksRecords wmsTasksRecords);

	/**
	 * 执行上架
	 * @param wmsTasksRecords 上架执行记录
	 */
	void putaway(WmsTasksRecords wmsTasksRecords);

}
