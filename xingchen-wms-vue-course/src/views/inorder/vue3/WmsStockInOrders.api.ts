import {defHttp} from '/@/utils/http/axios';

enum Api {
  list = '/inorder/wmsStockInOrders/list',
  save = '/inorder/wmsStockInOrders/add',
  edit = '/inorder/wmsStockInOrders/edit',
  deleteOne = '/inorder/wmsStockInOrders/delete',
  deleteBatch = '/inorder/wmsStockInOrders/deleteBatch',
  importExcel = '/inorder/wmsStockInOrders/importExcel',
  exportXls = '/inorder/wmsStockInOrders/exportXls',
  wmsStockInOrderItemsList = '/inorder/wmsStockInOrders/queryWmsStockInOrderItemsByMainId',
}

export const getExportUrl = Api.exportXls;
export const getImportUrl = Api.importExcel;
export const wmsStockInOrderItemsList = (params) =>
  defHttp.get({url: Api.wmsStockInOrderItemsList, params}, {isTransformResponse: false});

export const list = (params) => defHttp.get({url: Api.list, params});

export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({url: Api.deleteOne, params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
};

export const batchDelete = (params, handleSuccess) => {
  return defHttp.delete({url: Api.deleteBatch, data: params}, {joinParamsToUrl: true}).then(() => {
    handleSuccess();
  });
};

export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({url, params});
};
