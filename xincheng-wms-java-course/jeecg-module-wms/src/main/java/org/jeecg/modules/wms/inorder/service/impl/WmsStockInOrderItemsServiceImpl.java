package org.jeecg.modules.wms.inorder.service.impl;

import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import org.jeecg.modules.wms.inorder.mapper.WmsStockInOrderItemsMapper;
import org.jeecg.modules.wms.inorder.service.IWmsStockInOrderItemsService;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @Description: 入库单明细
 * @Author: jeecg-boot
 * @Date:   2026-05-31
 * @Version: V1.0
 */
@Service
public class WmsStockInOrderItemsServiceImpl extends ServiceImpl<WmsStockInOrderItemsMapper, WmsStockInOrderItems> implements IWmsStockInOrderItemsService {

	@Autowired
	private WmsStockInOrderItemsMapper wmsStockInOrderItemsMapper;
	@Autowired
	private IWmsTasksRecordsService wmsTasksRecordsService;

	@Override
	public List<WmsStockInOrderItems> selectByMainId(String mainId) {
		return wmsStockInOrderItemsMapper.selectByMainId(mainId);
	}

	@Override
	public void updateReceivedStatus(String stockInOrderItemId) {
		// 1. 查明细，查询该明细所有的收货记录
		WmsStockInOrderItems stockInOrderItems = this.getById(stockInOrderItemId);
		List<WmsTasksRecords> records = wmsTasksRecordsService.list(
			new LambdaQueryWrapper<>(WmsTasksRecords.class)
				.eq(WmsTasksRecords::getStockInOrderItemId, stockInOrderItemId)
		);
		// 2. 汇总良品、不良品数量
		int badQuantity = records.stream()
			.filter(record -> WarehouseDictEnum.INVENTORY_ATTRIBUTE_DEFECTIVE.getCode().equals(record.getInventoryAttribute()))
			.mapToInt(WmsTasksRecords::getExecQuantity).sum();
		int goodQuantity = records.stream()
			.filter(record -> WarehouseDictEnum.INVENTORY_ATTRIBUTE_GOOD.getCode().equals(record.getInventoryAttribute()))
			.mapToInt(WmsTasksRecords::getExecQuantity).sum();
		// 3. 更新明细
		stockInOrderItems.setReceivedQuantity(goodQuantity);
		stockInOrderItems.setDefectiveQuantity(badQuantity);
		if (stockInOrderItems.getExpectedQuantity().equals(goodQuantity + badQuantity)) {
			stockInOrderItems.setStatus("1");
		}
		this.updateById(stockInOrderItems);
	}
}
