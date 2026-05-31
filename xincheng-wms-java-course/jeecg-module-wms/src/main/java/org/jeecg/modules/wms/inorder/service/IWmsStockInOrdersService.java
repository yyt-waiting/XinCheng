package org.jeecg.modules.wms.inorder.service;

import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
public interface IWmsStockInOrdersService extends IService<WmsStockInOrders> {

	/**
	 * 添加一对多
	 *
	 * @param wmsStockInOrders
	 * @param wmsStockInOrderItemsList
	 */
	public void saveMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList) ;
	
	/**
	 * 修改一对多
	 *
   * @param wmsStockInOrders
   * @param wmsStockInOrderItemsList
	 */
	public void updateMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 提交审核：状态 0(初始) -> 1(已提交)
	 * @param id 入库单id
	 */
	public void submitAudit(String id);

	/**
	 * 审核通过：状态 1(已提交) -> 2(审核通过)
	 * @param id 入库单id
	 */
	public void audit(String id);

	/**
	 * 创建收货任务（从入库单生成）
	 * @param id 入库单id
	 * @param warehouseId 仓库id
	 */
	public void createReceiveTask(String id, String warehouseId);

}
