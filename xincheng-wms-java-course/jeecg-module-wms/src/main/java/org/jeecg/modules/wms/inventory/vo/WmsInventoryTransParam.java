package org.jeecg.modules.wms.inventory.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class WmsInventoryTransParam implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品 id")
    @Excel(name = "商品 id", width = 15)
    private String productId;

    @Schema(description = "执行数量")
    @Excel(name = "执行数量", width = 15)
    private Integer execQuantity;

    @Schema(description = "仓库 id")
    @Excel(name = "仓库 id", width = 15)
    private String warehouseId;

    @Schema(description = "来源储位编码")
    @Excel(name = "来源储位编码", width = 20)
    private String sourceLocationCode;

    @Schema(description = "目的储位编码")
    @Excel(name = "目的储位编码", width = 20)
    private String targetLocationCode;

    @Schema(description = "批次号")
    @Excel(name = "批次号", width = 20)
    private String batchNumber;

    @Schema(description = "保质期到期日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "保质期到期日", width = 20, format = "yyyy-MM-dd")
    private Date expiryDate;

    @Schema(description = "备注")
    @Excel(name = "备注", width = 30)
    private String remarks;

    @Schema(description = "变更类型")
    @Excel(name = "变更类型", width = 15)
    private String transactionType;

    @Schema(description = "执行人")
    @Excel(name = "执行人", width = 15)
    private String operator;

    @Schema(description = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @Schema(description = "是否可售")
    @Excel(name = "是否可售", width = 10)
    private String isSellable;
}
