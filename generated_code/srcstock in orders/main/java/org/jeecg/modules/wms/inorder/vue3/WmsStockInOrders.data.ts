import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '入库单号',
    align:"center",
    dataIndex: 'orderNumber'
   },
   {
    title: '入库类型',
    align:"center",
    dataIndex: 'orderType_dictText'
   },
   {
    title: '来源单号',
    align:"center",
    dataIndex: 'sourceNumber'
   },
   {
    title: '货主id',
    align:"center",
    dataIndex: 'ownerId'
   },
   {
    title: '预计到货时间',
    align:"center",
    dataIndex: 'expectedArrivalTime'
   },
   {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'remarks'
   },
   {
    title: '预期总数量(冗余字段)',
    align:"center",
    dataIndex: 'totalExpectedQuantity'
   },
   {
    title: '实际收货总量',
    align:"center",
    dataIndex: 'totalReceivedQuantity'
   },
   {
    title: '已上架总量',
    align:"center",
    dataIndex: 'totalShelvedQuantity'
   },
   {
    title: '不良品总数量(冗余字段)',
    align:"center",
    dataIndex: 'totalDefectiveQuantity'
   },
   {
    title: '仓库',
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
    label: '入库单号',
    field: 'orderNumber',
    component: 'Input',
  },
  {
    label: '入库类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"asn_type"
     },
  },
  {
    label: '来源单号',
    field: 'sourceNumber',
    component: 'Input',
  },
  {
    label: '货主id',
    field: 'ownerId',
    component: 'Input',
  },
  {
    label: '预计到货时间',
    field: 'expectedArrivalTime',
    component: 'DatePicker',
    componentProps: {
       showTime:true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"asn_status"
     },
  },
  {
    label: '备注',
    field: 'remarks',
    component: 'Input',
  },
  {
    label: '预期总数量(冗余字段)',
    field: 'totalExpectedQuantity',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '实际收货总量',
    field: 'totalReceivedQuantity',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '已上架总量',
    field: 'totalShelvedQuantity',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '不良品总数量(冗余字段)',
    field: 'totalDefectiveQuantity',
    component: 'InputNumber',
    dynamicDisabled:true
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入仓库!'},
          ];
     },
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];
//子表单数据
//子表表格配置
export const wmsStockInOrderItemsColumns: JVxeColumn[] = [
    {
      title: '入库单id',
      key: 'orderId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '商品id',
      key: 'productId',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '采购数量',
      key: 'expectedQuantity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '实际收货数量',
      key: 'receivedQuantity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '上架数量',
      key: 'shelvedQuantity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '不良品数量',
      key: 'defectiveQuantity',
      type: JVxeTypes.inputNumber,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '备注',
      key: 'remarks',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '状态',
      key: 'status',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]


// 高级查询数据
export const superQuerySchema = {
  orderNumber: {title: '入库单号',order: 0,view: 'text', type: 'string',},
  orderType: {title: '入库类型',order: 1,view: 'list', type: 'string',dictCode: 'asn_type',},
  sourceNumber: {title: '来源单号',order: 2,view: 'text', type: 'string',},
  ownerId: {title: '货主id',order: 3,view: 'text', type: 'string',},
  expectedArrivalTime: {title: '预计到货时间',order: 4,view: 'datetime', type: 'string',},
  status: {title: '状态',order: 5,view: 'list', type: 'string',dictCode: 'asn_status',},
  remarks: {title: '备注',order: 6,view: 'text', type: 'string',},
  totalExpectedQuantity: {title: '预期总数量(冗余字段)',order: 7,view: 'number', type: 'number',},
  totalReceivedQuantity: {title: '实际收货总量',order: 8,view: 'number', type: 'number',},
  totalShelvedQuantity: {title: '已上架总量',order: 9,view: 'number', type: 'number',},
  totalDefectiveQuantity: {title: '不良品总数量(冗余字段)',order: 10,view: 'number', type: 'number',},
  warehouseId: {title: '仓库',order: 11,view: 'text', type: 'string',},
  //子表高级查询
  wmsStockInOrderItems: {
    title: '入库单明细',
    view: 'table',
    fields: {
        orderId: {title: '入库单id',order: 0,view: 'text', type: 'string',},
        productId: {title: '商品id',order: 1,view: 'text', type: 'string',},
        expectedQuantity: {title: '采购数量',order: 2,view: 'number', type: 'number',},
        receivedQuantity: {title: '实际收货数量',order: 3,view: 'number', type: 'number',},
        shelvedQuantity: {title: '上架数量',order: 4,view: 'number', type: 'number',},
        defectiveQuantity: {title: '不良品数量',order: 5,view: 'number', type: 'number',},
        remarks: {title: '备注',order: 6,view: 'text', type: 'string',},
        status: {title: '状态',order: 7,view: 'text', type: 'string',},
    }
  },
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
// 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}