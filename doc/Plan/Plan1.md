我正在开发一个基于 JeecgBoot（Spring Boot 3 + Vue 3 + MyBatis-Plus + Redis）的仓储管理系统（WMS）。目前由于之前代码生成与手动修改存在不一致，我执行了回退操作。现在请你作为核心开发者，协助我补齐业务逻辑并修复所有 Bug。
1. 项目基础信息
后端包路径：org.jeecg.modules.wms（下分 goods, warehouse, inorder, wmstask, inventory 等模块）。
前端路径：src/views 目录下对应的功能文件夹。
核心模块：货主、仓库、储位、入库单（主附表）、收货任务、库存、上架任务。
2. 你当前的任务目标
严格按照 PDF 指导文件中的逻辑，重构 Service 层以实现“收货 -> 入库状态扭转 -> 库存同步 -> 自动创建上架任务”的全链路业务。

请协助我完成 WMS 仓储管理系统的核心业务开发。由于我执行了回退操作，目前的inorder、inventory、wmstask 模块的实现类里只有默认的空接口, ServiceImpl 类中缺乏业务逻辑。请你严格按照以下步骤和顺序进行代码编写，并确保与 PDF 指导文件中的逻辑完全一致。



第一阶段：库存变更体系开发（基础逻辑）
创建参数对象：在 inventory.vo 包下补齐 WmsInventoryTransParam 类。[对照 PDF 15-16 页]，确保包含 productId, execQuantity, targetLocationCode, batchNumber, isSellable 等核心字段。
增强库存查询：在 WmsInventoryServiceImpl 中实现 getInventoryByUniqueKey 方法。[对照 PDF 17-18 页]，必须实现基于“商品+储位+批次”的唯一性查询，注意处理批次号为空字符串的情况。
实现收货库存变更：创建/重构 WmsInventoryTransByReceiving 类，实现 transfer 方法。[对照 PDF 19-22 页]，核心逻辑必须包含：
校验商品和储位合法性。
有则累加：若库存记录已存在，使用 SQL 累加 stock_quantity。
无则新增：若不存在，初始化并保存新记录。
写入流水：保存 WmsInventoryTrans 变更日志。
第二阶段：入库单状态联动开发（状态机逻辑）
实现明细状态更新：在 WmsStockInOrderItemsServiceImpl 中实现 updateReceivedStatus 方法。[对照 PDF 6-8 页]，逻辑必须包含：汇总该明细下所有收货记录，若累计数量达标，将状态更新为“收货完成”。
实现主表状态更新：在 WmsStockInOrdersServiceImpl 中实现 updateReceivedStatus 方法。[对照 PDF 8-9 页]，逻辑必须包含：检查所有明细状态，若全部完成，将主表状态变更为“收货完成”，并返回该状态值。
第三阶段：收货任务总控开发（核心触发逻辑）
实现收货接口：在 ReceiveTasksController 中实现 addRecords 接口。[对照 PDF 2 页]。
实现收货执行逻辑：在 WmsTasksServiceImpl 中实现 receive 和 execute 方法。[对照 PDF 3-6 页]。
execute 方法需包含：总数量超限校验、不良品储位存放校验、任务进度累加。
receive 总控方法需依次调用：execute -> 入库明细更新 -> 入库主表更新 -> 库存变更同步 [对照 PDF 23 页]。
第四阶段：上架任务实战开发（实战进阶逻辑）
实现自动创建上架任务：在收货完成后（即入库单状态变为“收货完成”时），自动为良品记录创建上架任务。[对照 PDF 25-27 页]，设置 task_type 为上架，source_location_code 为收货暂存位。
实现上架库存转移：创建 WmsInventoryTransByPutway 类。[对照 PDF 28-29 页]，实现来源储位扣减、目标储位增加的原子操作。
第五阶段：前端 UI 与数据对齐（集成修复）
修正 dictCode 命名：扫描 inorder 和 wmstask 下的所有 .data.ts 文件。将所有 wms_warehouse 修正为 wms_warehouses，并确保货主和仓库的显示字段分别为 owner_name 和 warehouse_name。
修复接口引用：将 WmsTasks.data.ts 中缺失的 listByPickType 引用修改为调用通用的 list 接口，并传递正确的过滤参数。

开始执行,最后简要说明你如何确保与 PDF 逻辑一致。





