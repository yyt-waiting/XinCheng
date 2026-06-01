package org.jeecg.modules.wms.inorder.controller;

import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksMapper;
import org.jeecg.modules.wms.config.WarehouseDictEnum;
import org.jeecg.common.api.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "收货任务执行")
@RestController
@RequestMapping("/inorder/receiveTasks")
@Slf4j
public class ReceiveTasksController {

    @Autowired
    private IWmsTasksService wmsTasksService;

    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;

    @Autowired
    private WmsTasksMapper wmsTasksMapper;

    @Operation(summary = "待收货任务查询")
    @GetMapping(value = "/list")
    public Result<IPage<WmsTasks>> list(WmsTasks wmsTasks,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        wmsTasks.setTaskType(WarehouseDictEnum.TASK_TYPE_RECEIVING.getCode());
        Page<WmsTasks> page = new Page<>(pageNo, pageSize);
        IPage<WmsTasks> wmsTasksIPage = wmsTasksMapper.selectTaskPageWithJoin(
                page,
                wmsTasks.getTaskType(),
                wmsTasks.getTaskNumber(),
                wmsTasks.getTaskStatus(),
                null,
                wmsTasks.getStockInOrderNumber()
        );
        return Result.OK(wmsTasksIPage);
    }

    @Operation(summary = "收货执行记录查询")
    @GetMapping("/records")
    public Result<IPage<WmsTasksRecords>> records(
            @RequestParam(name = "taskNumber", required = false) String taskNumber,
            @RequestParam(name = "taskType", required = false) String taskType,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<WmsTasksRecords> records = wmsTasksRecordsService.queryReceiveRecords(taskNumber, taskType, pageNo, pageSize);
        return Result.OK(records);
    }

    @Operation(summary = "执行收货任务")
    @PostMapping("/addRecords")
    public Result<String> addRecords(@RequestBody WmsTasksRecords wmsTasksRecords) {
        log.info("===== 收货请求接收到的数据 =====");
        log.info("id: {}", wmsTasksRecords.getId());
        log.info("productId: {}", wmsTasksRecords.getProductId());
        log.info("ownerId: {}", wmsTasksRecords.getOwnerId());
        log.info("execQuantity: {}", wmsTasksRecords.getExecQuantity());
        log.info("taskId: {}", wmsTasksRecords.getTaskId());
        log.info("targetWarehouseId: {}", wmsTasksRecords.getTargetWarehouseId());
        log.info("targetLocationCode: {}", wmsTasksRecords.getTargetLocationCode());
        log.info("batchNumber: {}", wmsTasksRecords.getBatchNumber());
        log.info("inventoryAttribute: {}", wmsTasksRecords.getInventoryAttribute());
        log.info("================================");
        String taskId = wmsTasksRecords.getId();
        wmsTasksRecords.setTaskId(taskId);
        wmsTasksRecords.setId(null);
        wmsTasksService.receive(wmsTasksRecords);
        return Result.OK("添加成功！");
    }
}
