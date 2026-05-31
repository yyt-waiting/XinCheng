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
    title: '在库数量',
    align:"center",
    dataIndex: 'stockQuantity'
   },
   {
    title: '分配数量',
    align:"center",
    dataIndex: 'allocatedQuantity'
   },
   {
    title: '可用数量',
    align:"center",
    dataIndex: 'availableQuantity'
   },
   {
    title: '批号 ',
    align:"center",
    dataIndex: 'batchNumber'
   },
   {
    title: '入库时间',
    align:"center",
    dataIndex: 'stockInTime'
   },
   {
    title: '保质期到期日',
    align:"center",
    dataIndex: 'expiryDate',
   },
   {
    title: '货主',
    align:"center",
    dataIndex: 'ownerId'
   },
   {
    title: '是否可售',
    align:"center",
    dataIndex: 'isSellable'
   },
   {
    title: '仓库id',
    align:"center",
    dataIndex: 'warehouseId'
   },
];