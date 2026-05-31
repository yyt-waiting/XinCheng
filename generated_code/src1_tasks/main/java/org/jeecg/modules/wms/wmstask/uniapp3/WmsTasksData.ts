import { render } from '@/common/renderUtils';
//列表数据
export const columns = [
    {
    title: '任务号',
    align:"center",
    dataIndex: 'taskNumber'
   },
   {
    title: '任务类型',
    align:"center",
    dataIndex: 'taskType'
   },
   {
    title: '任务状态',
    align:"center",
    dataIndex: 'taskStatus'
   },
   {
    title: '商品id',
    align:"center",
    dataIndex: 'productId'
   },
   {
    title: '数量',
    align:"center",
    dataIndex: 'quantity'
   },
   {
    title: '完成数量',
    align:"center",
    dataIndex: 'completedQuantity'
   },
   {
    title: '来源储位编码',
    align:"center",
    dataIndex: 'sourceLocationCode'
   },
   {
    title: '目的储位编码',
    align:"center",
    dataIndex: 'targetLocationCode'
   },
   {
    title: '来源容器编码',
    align:"center",
    dataIndex: 'sourceContainerCode'
   },
   {
    title: '目的容器编码',
    align:"center",
    dataIndex: 'targetContainerCode'
   },
   {
    title: '执行人',
    align:"center",
    dataIndex: 'operator'
   },
   {
    title: '执行时间',
    align:"center",
    dataIndex: 'operationTime'
   },
   {
    title: '完成时间',
    align:"center",
    dataIndex: 'completedAt'
   },
   {
    title: '入库单id',
    align:"center",
    dataIndex: 'stockInOrderId'
   },
   {
    title: '入库明细id',
    align:"center",
    dataIndex: 'stockInOrderItemId'
   },
   {
    title: '波次单id',
    align:"center",
    dataIndex: 'waveOrderId'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNumber'
   },
   {
    title: '保质期',
    align:"center",
    dataIndex: 'expiryDate',
   },
   {
    title: '出库单id',
    align:"center",
    dataIndex: 'outOrderId'
   },
   {
    title: '波次拣货明细id',
    align:"center",
    dataIndex: 'waveSkuSummaryId'
   },
   {
    title: '来源仓库',
    align:"center",
    dataIndex: 'sourceWarehouseId'
   },
   {
    title: '目的仓库',
    align:"center",
    dataIndex: 'targetWarehouseId'
   },
];