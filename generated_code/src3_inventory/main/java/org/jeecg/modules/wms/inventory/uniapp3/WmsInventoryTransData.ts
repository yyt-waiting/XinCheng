import { render } from '@/common/renderUtils';
//列表数据
export const columns = [
    {
    title: '商品id',
    align:"center",
    dataIndex: 'productId'
   },
   {
    title: '储位编码',
    align:"center",
    dataIndex: 'locationCode'
   },
   {
    title: '容器编码',
    align:"center",
    dataIndex: 'containerCode'
   },
   {
    title: '变更数量',
    align:"center",
    dataIndex: 'changeQuantity'
   },
   {
    title: '变更类型',
    align:"center",
    dataIndex: 'transactionType'
   },
   {
    title: '关联单据号',
    align:"center",
    dataIndex: 'referenceNumber'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remarks'
   },
   {
    title: '变更时间',
    align:"center",
    dataIndex: 'transactionTime'
   },
   {
    title: '批次号',
    align:"center",
    dataIndex: 'batchNumber'
   },
];