import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { defHttp } from '/@/utils/http/axios';

//列表数据
export const columns: BasicColumn[] = [
   {
    title: '入库单号',
    align: "center",
    dataIndex: 'orderNumber'
   },
   {
    title: '入库类型',
    align: "center",
    dataIndex: 'orderType_dictText',
   },
   {
    title: '来源单号',
    align: "center",
    dataIndex: 'sourceNumber'
   },
   {
    title: '货主',
    align: "center",
    dataIndex: 'ownerName'
   },
   {
    title: '仓库',
    align: "center",
    dataIndex: 'warehouseName'
   },
   {
    title: '预计到货时间',
    align: "center",
    dataIndex: 'expectedArrivalTime'
   },
   {
    title: '预期总数量',
    align: "center",
    dataIndex: 'totalExpectedQuantity'
   },
   {
    title: '实际收货总量',
    align: "center",
    dataIndex: 'totalReceivedQuantity'
   },
   {
    title: '已上架总量',
    align: "center",
    dataIndex: 'totalShelvedQuantity'
   },
   {
    title: '不良品总量',
    align: "center",
    dataIndex: 'totalDefectiveQuantity'
   },
   {
    title: '状态',
    align: "center",
    dataIndex: 'status_dictText',
    customRender: ({text}) => {
      return render.renderDict(text, 'asn_status');
    }
   },
   {
    title: '备注',
    align: "center",
    dataIndex: 'remarks'
   },
];

//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: '入库单号',
    field: 'orderNumber',
    component: 'Input',
    colProps: {span: 6},
  },
  {
    label: '入库类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_type"
    },
    colProps: {span: 6},
  },
  {
    label: '货主',
    field: 'ownerId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_cargo_owners,name,owner_id',
      pidField: 'pid',
    },
    colProps: {span: 6},
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_warehouse,name,warehouse_id',
      pidField: 'pid',
    },
    colProps: {span: 6},
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_status"
    },
    colProps: {span: 6},
  },
];

//主表表单数据
export const formSchema: FormSchema[] = [
  {
    label: '入库单号',
    field: 'orderNumber',
    component: 'Input',
    componentProps: {
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '入库类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_type"
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择入库类型!' }];
    },
  },
  {
    label: '来源单号',
    field: 'sourceNumber',
    component: 'Input',
    colProps: {span: 12},
  },
  {
    label: '货主',
    field: 'ownerId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_cargo_owners,name,owner_id',
      pidField: 'pid',
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择货主!' }];
    },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_warehouse,name,warehouse_id',
      pidField: 'pid',
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择仓库!' }];
    },
  },
  {
    label: '预计到货时间',
    field: 'expectedArrivalTime',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      format: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: {span: 12},
  },
  {
    label: '预期总数量',
    field: 'totalExpectedQuantity',
    component: 'InputNumber',
    componentProps: {
      disabled: true,
    },
    colProps: {span: 12},
  },
  {
    label: '实际收货总量',
    field: 'totalReceivedQuantity',
    component: 'InputNumber',
    componentProps: {
      disabled: true,
    },
    colProps: {span: 12},
  },
  {
    label: '已上架总量',
    field: 'totalShelvedQuantity',
    component: 'InputNumber',
    componentProps: {
      disabled: true,
    },
    colProps: {span: 12},
  },
  {
    label: '不良品总量',
    field: 'totalDefectiveQuantity',
    component: 'InputNumber',
    componentProps: {
      disabled: true,
    },
    colProps: {span: 12},
  },
  {
    label: '状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_status",
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '备注',
    field: 'remarks',
    component: 'Input',
    colProps: {span: 24},
  },
  // 主键隐藏
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
];

//新增表单数据（不含子表）
export const formAddSchema: FormSchema[] = [
  {
    label: '入库类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_type"
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择入库类型!' }];
    },
  },
  {
    label: '来源单号',
    field: 'sourceNumber',
    component: 'Input',
    colProps: {span: 12},
  },
  {
    label: '货主',
    field: 'ownerId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_cargo_owners,name,owner_id',
      pidField: 'pid',
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择货主!' }];
    },
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_warehouse,name,warehouse_id',
      pidField: 'pid',
    },
    colProps: {span: 12},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择仓库!' }];
    },
  },
  {
    label: '预计到货时间',
    field: 'expectedArrivalTime',
    component: 'DatePicker',
    componentProps: {
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      format: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: {span: 12},
  },
  {
    label: '备注',
    field: 'remarks',
    component: 'Input',
    colProps: {span: 24},
  },
];

//审核表单数据
export const auditFormSchema: FormSchema[] = [
  {
    label: '入库单号',
    field: 'orderNumber',
    component: 'Input',
    componentProps: {
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '入库类型',
    field: 'orderType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: "asn_type",
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '来源单号',
    field: 'sourceNumber',
    component: 'Input',
    componentProps: {
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '货主',
    field: 'ownerId',
    component: 'Input',
    componentProps: {
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'Input',
    componentProps: {
      disabled: true
    },
    colProps: {span: 12},
  },
  {
    label: '备注',
    field: 'remarks',
    component: 'Input',
    colProps: {span: 24},
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
];

//创建收货任务表单数据
export const receiveTaskAdd: FormSchema[] = [
  {
    label: '仓库',
    field: 'warehouseId',
    component: 'JTreeSelect',
    componentProps: {
      dict: 'wms_warehouse,name,warehouse_id',
      pidField: 'pid',
    },
    colProps: {span: 24},
    dynamicRules: ({model, schema}) => {
      return [{ required: true, message: '请选择仓库!' }];
    },
  },
];

// JVxe 子表列定义 - 入库单明细
export const createWmsStockInOrderItemsJVxeColumns = (getFieldsValue) => {
  return [
    {
      title: '商品',
      key: 'productName',
      type: 'input',
      width: 180,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '采购数量',
      key: 'expectedQuantity',
      type: 'number',
      width: 120,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '实际收货数量',
      key: 'quantity',
      type: 'number',
      width: 140,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '库存属性',
      key: 'inventoryAttribute',
      type: 'select',
      width: 120,
      options: [], // 数据字典动态加载
      placeholder: '请选择${title}',
      defaultValue: '',
    },
    {
      title: '储位编码',
      key: 'locationCode',
      type: 'input',
      width: 140,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: 'LPN',
      key: 'lpn',
      type: 'input',
      width: 120,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '批次号',
      key: 'batchNumber',
      type: 'input',
      width: 140,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '保质期到期时间',
      key: 'expiryDate',
      type: 'date',
      width: 160,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
    {
      title: '收货类型',
      key: 'receivingType',
      type: 'select',
      width: 120,
      options: [],
      placeholder: '请选择${title}',
      defaultValue: '',
    },
    {
      title: '备注',
      key: 'remarks',
      type: 'input',
      width: 180,
      placeholder: '请输入${title}',
      defaultValue: '',
    },
  ];
};

// 高级查询数据
export const superQuerySchema = {
  orderNumber: {title: '入库单号', order: 0, view: 'text', type: 'string',},
  orderType: {title: '入库类型', order: 1, view: 'text', type: 'string',},
  sourceNumber: {title: '来源单号', order: 2, view: 'text', type: 'string',},
  ownerId: {title: '货主id', order: 3, view: 'text', type: 'string',},
  warehouseId: {title: '仓库', order: 4, view: 'text', type: 'string',},
  expectedArrivalTime: {title: '预计到货时间', order: 5, view: 'datetime', type: 'string',},
  totalExpectedQuantity: {title: '预期总数量', order: 6, view: 'number', type: 'number',},
  totalReceivedQuantity: {title: '实际收货总量', order: 7, view: 'number', type: 'number',},
  totalShelvedQuantity: {title: '已上架总量', order: 8, view: 'number', type: 'number',},
  totalDefectiveQuantity: {title: '不良品总量', order: 9, view: 'number', type: 'number',},
  status: {title: '状态', order: 10, view: 'text', type: 'string',},
  remarks: {title: '备注', order: 11, view: 'text', type: 'string',},
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param _formData
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  return formSchema;
}
