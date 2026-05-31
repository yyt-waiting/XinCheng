import { render } from '@/common/renderUtils';
//列表数据
export const columns = [
    {
    title: '仓库代码',
    align:"center",
    dataIndex: 'warehouseCode'
   },
   {
    title: '仓库名称',
    align:"center",
    dataIndex: 'warehouseName'
   },
   {
    title: '仓库属性',
    align:"center",
    dataIndex: 'warehouseAttr_dictText'
   },
   {
    title: '状态: 创建,启动,禁用',
    align:"center",
    dataIndex: 'status_dictText'
   },
];