package org.jeecg.modules.wms.goods.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 商品表
 * @Author: jeecg-boot
 * @Date: 2026-05-31
 * @Version: V1.0
 */
@Data
@TableName("wms_products")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "商品表")
public class WmsProducts implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private Date createTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;

    /** 所属部门 */
    @Schema(description = "所属部门")
    private String sysOrgCode;

    /** 商品编码 */
    @Excel(name = "商品编码", width = 15)
    @Schema(description = "商品编码")
    private String productCode;

    /** 商品名称 */
    @Excel(name = "商品名称", width = 15)
    @Schema(description = "商品名称")
    private String productName;

    /** 商品规格 */
    @Excel(name = "商品规格", width = 15)
    @Schema(description = "商品规格")
    private String productSpec;

    /** 商品单位 */
    @Excel(name = "商品单位", width = 15)
    @Schema(description = "商品单位")
    private String productUnit;

    /** 货主 */
    @Excel(name = "货主", width = 15)
    @Schema(description = "货主")
    private String ownerId;

    /** 货主名称 */
    @Excel(name = "货主名称", width = 15)
    @Schema(description = "货主名称")
    private String ownerName;

    /** 状态 */
    @Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
}
