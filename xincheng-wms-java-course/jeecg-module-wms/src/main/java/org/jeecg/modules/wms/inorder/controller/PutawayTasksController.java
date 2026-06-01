package org.jeecg.modules.wms.inorder.controller;

import org.jeecg.modules.wms.wmstask.service.IWmsTasksService;
import org.jeecg.modules.wms.wmstask.entity.WmsTasksRecords;
import org.jeecg.modules.wms.wmstask.entity.WmsTasks;
import org.jeecg.modules.wms.wmstask.mapper.WmsTasksMapper;
import org.jeecg.modules.wms.wmstask.service.IWmsTasksRecordsService;
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

@Tag(name = "上架任务管理")
@RestController
@RequestMapping("/inorder/putawayTasks")
@Slf4j
public class PutawayTasksController {

    @Autowired
    private IWmsTasksService wmsTasksService;

    @Autowired
    private IWmsTasksRecordsService wmsTasksRecordsService;

    @Autowired
    private WmsTasksMapper wmsTasksMapper;

    @Operation(summary = "待上架任务查询")
    @GetMapping(value = "/list")
    public Result<IPage<WmsTasks>> list(WmsTasks wmsTasks,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        wmsTasks.setTaskType(WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode());
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

    @Operation(summary = "执行上架")
    @PostMapping(value = "/addRecords")
    public Result<String> execute(@RequestBody WmsTasksRecords wmsTasksRecords) {
        wmsTasksService.putaway(wmsTasksRecords);
        return Result.OK("上架成功！");
    }

    @Operation(summary = "查询上架记录")
    @GetMapping("/records")
    public Result<IPage<WmsTasksRecords>> records(
            @RequestParam(name = "taskId", required = false) String taskId,
            @RequestParam(name = "taskNumber", required = false) String taskNumber,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<WmsTasksRecords> records = wmsTasksRecordsService.queryReceiveRecords(
                taskNumber,
                WarehouseDictEnum.TASK_TYPE_PUTAWAY.getCode(),
                pageNo,
                pageSize);
        return Result.OK(records);
    }
}
