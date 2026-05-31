import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
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
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
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
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '商品id',
    field: 'productId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入商品id!'},
          ];
     },
  },
  {
    label: '储位编码',
    field: 'locationCode',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入储位编码!'},
          ];
     },
  },
  {
    label: '容器编码',
    field: 'containerCode',
    component: 'Input',
  },
  {
    label: '在库数量',
    field: 'stockQuantity',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入在库数量!'},
          ];
     },
  },
  {
    label: '分配数量',
    field: 'allocatedQuantity',
    component: 'InputNumber',
  },
  {
    label: '可用数量',
    field: 'availableQuantity',
    component: 'InputNumber',
  },
  {
    label: '批号 ',
    field: 'batchNumber',
    component: 'Input',
  },
  {
    label: '入库时间',
    field: 'stockInTime',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入入库时间!'},
          ];
     },
  },
  {
    label: '保质期到期日',
    field: 'expiryDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '货主',
    field: 'ownerId',
    component: 'Input',
  },
  {
    label: '是否可售',
    field: 'isSellable',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入是否可售!'},
          ];
     },
  },
  {
    label: '仓库id',
    field: 'warehouseId',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];

// 高级查询数据
export const superQuerySchema = {
  productId: {title: '商品id',order: 0,view: 'text', type: 'string',},
  locationCode: {title: '储位编码',order: 1,view: 'text', type: 'string',},
  containerCode: {title: '容器编码',order: 2,view: 'text', type: 'string',},
  stockQuantity: {title: '在库数量',order: 3,view: 'number', type: 'number',},
  allocatedQuantity: {title: '分配数量',order: 4,view: 'number', type: 'number',},
  availableQuantity: {title: '可用数量',order: 5,view: 'number', type: 'number',},
  batchNumber: {title: '批号 ',order: 6,view: 'text', type: 'string',},
  stockInTime: {title: '入库时间',order: 7,view: 'datetime', type: 'string',},
  expiryDate: {title: '保质期到期日',order: 8,view: 'date', type: 'string',},
  ownerId: {title: '货主',order: 9,view: 'text', type: 'string',},
  isSellable: {title: '是否可售',order: 10,view: 'text', type: 'string',},
  warehouseId: {title: '仓库id',order: 11,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}