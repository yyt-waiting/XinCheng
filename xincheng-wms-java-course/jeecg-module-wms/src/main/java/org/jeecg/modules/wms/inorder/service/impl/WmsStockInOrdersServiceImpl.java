package org.jeecg.modules.wms.inorder.service.impl;

import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrderItemsMapper;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrdersMapper;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrdersService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksMapper;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 入库单主表
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
@Service
public class WmsStockInOrdersServiceImpl extends ServiceImpl<WmsStockInOrdersMapper, WmsStockInOrders> implements IWmsStockInOrdersService {

	@Autowired
	private WmsStockInOrdersMapper wmsStockInOrdersMapper;
	@Autowired
	private WmsStockInOrderItemsMapper wmsStockInOrderItemsMapper;
	@Autowired
	private WmsTasksMapper wmsTasksMapper;
	@Autowired
	private RedisUtil redisUtil;

	private static final String REDIS_INCR_KEY = "WMS_STOCK_IN_ORDER_NUMBER";
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(WmsStockInOrders wmsStockInOrders, List<WmsStockInOrderItems> wmsStockInOrderItemsList) {
		if (oConvertUtils.isEmpty(wmsStockInOrders.getStatus())) {
			wmsStockInOrders.setStatus("0");
		}
		if (oConvertUtils.isEmpty(wmsStockInOrders.getOrderNumber())) {
			wmsStockInOrders.setOrderNumber(generateOrderNumber());
		}
		if (wmsStockInOrderItemsList != null && !wmsStockInOrderItemsList.isEmpty()) {
			int total = wmsStockInOrderItemsList.stream()
					.filter(i -> i.getExpectedQuantity() != null)
					.mapToInt(WmsStockInOrderItems::getExpectedQuantity).sum();
			wmsStockInOrders.setTotalExpectedQuantity(total);
		}
		wmsStockInOrdersMapper.insert(wmsStockInOrders);
		if(wmsStockInOrderItemsList!=null && wmsStockInOrderItemsList.size()>0) {
			for(WmsStockInOrderItems entity:wmsStockInOrderItemsList) {
				//外键设置
				entity.setOrderId(wmsStockInOrders.getId());
				wmsStockInOrderItemsMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(WmsStockInOrders wmsStockInOrders,List<WmsStockInOrderItems> wmsStockInOrderItemsList) {
		if (wmsStockInOrderItemsList!=null && !wmsStockInOrderItemsList.isEmpty()) {
			int total = wmsStockInOrderItemsList.stream()
					.filter(i -> i.getExpectedQuantity() != null)
					.mapToInt(WmsStockInOrderItems::getExpectedQuantity).sum();
			wmsStockInOrders.setTotalExpectedQuantity(total);
		}
		wmsStockInOrdersMapper.updateById(wmsStockInOrders);
		
		//1.先删除子表数据
		wmsStockInOrderItemsMapper.deleteByMainId(wmsStockInOrders.getId());
		
		//2.子表数据重新插入
		if(wmsStockInOrderItemsList!=null && wmsStockInOrderItemsList.size()>0) {
			for(WmsStockInOrderItems entity:wmsStockInOrderItemsList) {
				//外键设置
				entity.setOrderId(wmsStockInOrders.getId());
				wmsStockInOrderItemsMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		wmsStockInOrderItemsMapper.deleteByMainId(id);
		wmsStockInOrdersMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			wmsStockInOrderItemsMapper.deleteByMainId(id.toString());
			wmsStockInOrdersMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submitAudit(String id) {
		WmsStockInOrders order = wmsStockInOrdersMapper.selectById(id);
		if (order == null) {
			throw new RuntimeException("入库单不存在");
		}
		if (!"0".equals(order.getStatus())) {
			throw new RuntimeException("只有初始状态的单据才能提交审核");
		}
		WmsStockInOrders update = new WmsStockInOrders();
		update.setId(id);
		update.setStatus("1");
		wmsStockInOrdersMapper.updateById(update);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void audit(String id) {
		WmsStockInOrders order = wmsStockInOrdersMapper.selectById(id);
		if (order == null) {
			throw new RuntimeException("入库单不存在");
		}
		if (!"1".equals(order.getStatus())) {
			throw new RuntimeException("只有已提交的单据才能审核");
		}
		WmsStockInOrders update = new WmsStockInOrders();
		update.setId(id);
		update.setStatus("2");
		wmsStockInOrdersMapper.updateById(update);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createReceiveTask(String id, String warehouseId) {
		WmsStockInOrders order = wmsStockInOrdersMapper.selectById(id);
		if (order == null) {
			throw new RuntimeException("入库单不存在");
		}
		if (!"2".equals(order.getStatus())) {
			throw new RuntimeException("只有审核通过的单据才能创建收货任务");
		}
		List<WmsStockInOrderItems> items = wmsStockInOrderItemsMapper.selectByMainId(id);
		if (items == null || items.isEmpty()) {
			throw new RuntimeException("入库单无明细，无法创建收货任务");
		}
		for (WmsStockInOrderItems item : items) {
			WmsTasks task = new WmsTasks();
			task.setTaskNumber("TASK-" + System.currentTimeMillis() + "-" + item.getId());
			task.setTaskType("receive");
			task.setTaskStatus("0");
			task.setStockInOrderId(id);
			task.setStockInOrderItemId(item.getId());
			task.setTargetWarehouseId(warehouseId != null ? warehouseId : order.getWarehouseId());
			task.setQuantity(item.getExpectedQuantity());
			task.setCompletedQuantity(0);
			wmsTasksMapper.insert(task);
		}
	}

	/**
	 * 并发安全入库单号发号器
	 */
	private String generateOrderNumber() {
		try {
			while (true) {
				long seq = redisUtil.incr(REDIS_INCR_KEY, 1);
				return "IN" + String.format("%08d", seq);
			}
		} catch (Exception e) {
			throw new JeecgBootException("入库单号自动生成失败：" + e.getMessage());
		}
	}

}
