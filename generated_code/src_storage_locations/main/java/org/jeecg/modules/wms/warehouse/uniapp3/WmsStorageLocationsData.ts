import { render } from '@/common/renderUtils';
//列表数据
export const columns = [
    {
    title: '库位编码',
    align:"center",
    dataIndex: 'locationCode'
   },
   {
    title: '储位类别',
    align:"center",
    dataIndex: 'locationCategory'
   },
   {
    title: '库位类型',
    align:"center",
    dataIndex: 'locationType_dictText'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '所属仓库',
    align:"center",
    dataIndex: 'warehouseId'
   },
   {
    title: '所属库区',
    align:"center",
    dataIndex: 'zoneId'
   },
   {
    title: '巷道',
    align:"center",
    dataIndex: 'locationAisle'
   },
   {
    title: '排',
    align:"center",
    dataIndex: 'locationLine'
   },
   {
    title: '列',
    align:"center",
    dataIndex: 'locationRank'
   },
   {
    title: '层',
    align:"center",
    dataIndex: 'locationLayer'
   },
   {
    title: '长',
    align:"center",
    dataIndex: 'locationLength'
   },
   {
    title: '宽',
    align:"center",
    dataIndex: 'locationWidth'
   },
   {
    title: '容积',
    align:"center",
    dataIndex: 'locationCapacity'
   },
   {
    title: '承重',
    align:"center",
    dataIndex: 'loadCapacity'
   },
   {
    title: '是否可售',
    align:"center",
    dataIndex: 'isSellable_dictText'
   },
];