package org.jeecg.modules.wms.inorder.vo;

import lombok.Data;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrders;
import org.jeecg.modules.wms.inorder.entity.WmsStockInOrderItems;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 *
 *
 * @Description: 入库单主表 VO（用于表单页面，含一对多子表数据）
 * @Author: jeecg-boot
 * @Date: 2026-05-31
 * @Version: V1.0
 */
@Data
public class WmsStockInOrdersPage {

	/** 主键 */
	private String id;
	/** 创建人 */
	private String createBy;
	/** 创建日期 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/** 更新人 */
	private String updateBy;
	/** 更新日期 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	/** 所属部门 */
	private String sysOrgCode;
	/** 入库单号 */
	private String orderNumber;
	/** 入库类型 */
	private String orderType;
	/** 来源单号 */
	private String sourceNumber;
	/** 货主id */
	private String ownerId;
	/** 预计到货时间 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expectedArrivalTime;
	/** 状态 */
	private String status;
	/** 备注 */
	private String remarks;
	/** 预期总数量(冗余字段) */
	private Integer totalExpectedQuantity;
	/** 实际收货总量 */
	private Integer totalReceivedQuantity;
	/** 已上架总量 */
	private Integer totalShelvedQuantity;
	/** 不良品总数量(冗余字段) */
	private Integer totalDefectiveQuantity;
	/** 仓库 */
	private String warehouseId;

	/** 入库单明细列表（一对多） */
	private List<WmsStockInOrderItems> wmsStockInOrderItemsList;
}
