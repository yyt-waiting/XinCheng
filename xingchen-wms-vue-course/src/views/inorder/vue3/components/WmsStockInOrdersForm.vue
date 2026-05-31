<template>
  <div>
    <BasicForm @register="registerForm" ref="formRef"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated  @change="handleChangeTabs">
      <a-tab-pane tab="入库单明细" key="wmsStockInOrderItems" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="wmsStockInOrderItems"
          v-if="wmsStockInOrderItemsTable.show"
          :loading="wmsStockInOrderItemsTable.loading"
          :columns="wmsStockInOrderItemsTable.columns"
          :dataSource="wmsStockInOrderItemsTable.dataSource"
          :height="340"
          :rowNumber="true"
          :rowSelection="true"
          :disabled="formDisabled"
          :toolbar="true"
        />
      </a-tab-pane>
    </a-tabs>

    <div style="width: 100%;text-align: center" v-if="!formDisabled">
      <a-button @click="handleSubmit" pre-icon="ant-design:check" type="primary">提 交</a-button>
    </div>
  </div>
</template>

<script lang="ts">

  import {BasicForm, useForm} from '/@/components/Form/index';
  import { computed, defineComponent, reactive, ref, unref } from 'vue';
  import {defHttp} from '/@/utils/http/axios';
  import { propTypes } from '/@/utils/propTypes';
  import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods';
  import { VALIDATE_FAILED } from '/@/utils/common/vxeUtils';
  import {getBpmFormSchema,wmsStockInOrderItemsColumns} from '../WmsStockInOrders.data';
  import {saveOrUpdate,wmsStockInOrderItemsList} from '../WmsStockInOrders.api';

  export default defineComponent({
    name: "WmsStockInOrdersForm",
    components:{
      BasicForm,
    },
    props:{
      formData: propTypes.object.def({}),
      formBpm: propTypes.bool.def(true),
    },
    setup(props){
      const [registerForm, { setFieldsValue, setProps }] = useForm({
        labelWidth: 150,
        schemas: getBpmFormSchema(props.formData),
        showActionButtonGroup: false,
        baseColProps: {span: 12}
      });

      const formDisabled = computed(()=>{
        if(props.formData.disabled === false){
          return false;
        }
        return true;
      });

      const refKeys = ref(['wmsStockInOrderItems', ]);
      const activeKey = ref('wmsStockInOrderItems');
      const wmsStockInOrderItems = ref();
      const tableRefs = {wmsStockInOrderItems, };
      const wmsStockInOrderItemsTable = reactive({
        loading: false,
        dataSource: [],
        columns:wmsStockInOrderItemsColumns,
        show: false
      })

      const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys,validateSubForm);

      function classifyIntoFormData(allValues) {
        let main = Object.assign({}, allValues.formValue)
        return {
          ...main, // 展开
          wmsStockInOrderItemsList: allValues.tablesValue[0].tableData,
        }
      }

      //表单提交事件
      async function requestAddOrEdit(values) {
        await saveOrUpdate(values, true);
      }

      const queryByIdUrl = '/inorder/wmsStockInOrders/queryById';
      async function initFormData(){
        let params = {id: props.formData.dataId};
        const data = await defHttp.get({url: queryByIdUrl, params});
        //设置表单的值
        await setFieldsValue({...data});
        requestSubTableData(wmsStockInOrderItemsList, {id: data.id}, wmsStockInOrderItemsTable, ()=>{
          wmsStockInOrderItemsTable.show = true;
        });
        //默认是禁用
        await setProps({disabled: formDisabled.value})
      }

      initFormData();

      return {
        registerForm,
        formDisabled,
        formRef,
        handleSubmit,
        activeKey,
        handleChangeTabs,
        wmsStockInOrderItems,
        wmsStockInOrderItemsTable,
      }
    }
  });
</script>