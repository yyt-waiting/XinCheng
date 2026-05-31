<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896" @ok="handleSubmit">
    <BasicForm @register="registerForm" ref="formRef" name="WmsStockInOrdersAddForm"/>
    <!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated @change="handleChangeTabs">
      <a-tab-pane tab="入库单明细" key="wmsStockInOrderItems" :forceRender="true">
        <JVxeTable
          keep-source
          resizable
          ref="wmsStockInOrderItems"
          :loading="wmsStockInOrderItemsTable.loading"
          :columns="wmsStockInOrderItemsTable.columns"
          :dataSource="wmsStockInOrderItemsTable.dataSource"
          :height="300"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"
          />
      </a-tab-pane>
    </a-tabs>
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref, reactive} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import { JVxeTable } from '/@/components/jeecg/JVxeTable'
    import { useJvxeMethod } from '/@/hooks/system/useJvxeMethods.ts'
    import {formAddSchema, wmsStockInOrderItemsColumns} from '../WmsStockInOrders.data';
    import {saveOrUpdate, wmsStockInOrderItemsList} from '../WmsStockInOrders.api';
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(false);
    const formDisabled = ref(false);
    const refKeys = ref(['wmsStockInOrderItems', ]);
    const activeKey = ref('wmsStockInOrderItems');
    const wmsStockInOrderItems = ref();
    const tableRefs = {wmsStockInOrderItems, };
    const wmsStockInOrderItemsTable = reactive({
          loading: false,
          dataSource: [],
          columns: wmsStockInOrderItemsColumns
    });
    //表单配置
    const [registerForm, {setProps,resetFields, setFieldsValue, validate}] = useForm({
        schemas: formAddSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 12}
    });
     //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await reset();
        setModalProps({confirmLoading: false,showCancelBtn:data?.showFooter,showOkBtn:data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        formDisabled.value = !data?.showFooter;
        if (unref(isUpdate)) {
            //表单赋值
            await setFieldsValue({
                ...data.record,
            });
            requestSubTableData(wmsStockInOrderItemsList, {id:data?.record?.id}, wmsStockInOrderItemsTable)
        }
        setProps({ disabled: !data?.showFooter })
    });
    //方法配置
    const [handleChangeTabs,handleSubmit,requestSubTableData,formRef] = useJvxeMethod(requestAddOrEdit,classifyIntoFormData,tableRefs,activeKey,refKeys);

    //设置标题
    const title = computed(() => (!unref(isUpdate) ? '新增' : !unref(formDisabled) ? '编辑' : '详情'));

    async function reset(){
      await resetFields();
      activeKey.value = 'wmsStockInOrderItems';
      wmsStockInOrderItemsTable.dataSource = [];
    }

    function classifyIntoFormData(allValues) {
         let main = Object.assign({}, allValues.formValue)
         return {
           ...main,
           wmsStockInOrderItemsList: allValues.tablesValue[0].tableData,
         }
    }

    async function requestAddOrEdit(values) {
        try {
            setModalProps({confirmLoading: true});
            await saveOrUpdate(values, isUpdate.value);
            closeModal();
            emit('success');
        } finally {
            setModalProps({confirmLoading: false});
        }
    }
</script>

<style lang="less" scoped>
  :deep(.ant-input-number) {
    width: 100%;
  }
  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>
