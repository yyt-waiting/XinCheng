<template>
  <BasicModal v-bind="$attrs" @register="registerModal" destroyOnClose :title="title" :width="896" @ok="handleSubmit">
      <BasicForm @register="registerForm" name="WmsTasksForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
    import {ref, computed, unref} from 'vue';
    import {BasicModal, useModalInner} from '/@/components/Modal';
    import {BasicForm, useForm} from '/@/components/Form/index';
    import {receiveFormSchema} from '../WmsTasks.data';
    import {receive} from '../WmsTasks.api';
    // Emits声明
    const emit = defineEmits(['register','success']);
    const isUpdate = ref(true);
    const isDetail = ref(false);
    // 保存原始record，用于提交时补充隐藏字段
    const originalRecord = ref<Recordable>({});
    //表单配置
    const [registerForm, { setProps,resetFields, setFieldsValue, validate, scrollToField }] = useForm({
        schemas: receiveFormSchema,
        showActionButtonGroup: false,
        baseColProps: {span: 12}
    });
    //表单赋值
    const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
        //重置表单
        await resetFields();
        setModalProps({confirmLoading: false,showCancelBtn:!!data?.showFooter,showOkBtn:!!data?.showFooter});
        isUpdate.value = !!data?.isUpdate;
        isDetail.value = !!data?.showFooter;
        // 保存原始record
        originalRecord.value = data?.record || {};
        if (unref(isUpdate) && data?.record) {
            await setFieldsValue({
                ...data.record,
                // 显式注入，避免 show:false 字段被表单丢弃
                productId: data.record.productId,
                ownerId: data.record.ownerId,
            });
        }
        // 隐藏底部时禁用整个表单
       setProps({ disabled: !data?.showFooter })
    });
    //设置标题
    const title = computed(() => '收货');
    //表单提交事件
    async function handleSubmit(v) {
        try {
            let values = await validate();
            setModalProps({confirmLoading: true});
            // 手动验证用户输入的关键字段
            if (!values.execQuantity || values.execQuantity <= 0) {
              createMessage.warn('请输入有效的收货数量');
              setModalProps({confirmLoading: false});
              return;
            }
            if (!values.inventoryAttribute) {
              createMessage.warn('请选择库存属性');
              setModalProps({confirmLoading: false});
              return;
            }
            if (!values.targetLocationCode) {
              createMessage.warn('请选择储位编码');
              setModalProps({confirmLoading: false});
              return;
            }
            if (!values.receivingDate) {
              createMessage.warn('请选择收货日期');
              setModalProps({confirmLoading: false});
              return;
            }
            // 从 originalRecord 提取所有ID字段，用户输入的覆盖同名字段
            const submitData = {
              id: originalRecord.value.id,
              taskNumber: values.taskNumber,
              taskType: values.taskType,
              taskStatus: values.taskStatus,
              ownerName: values.ownerName,
              ownerId: originalRecord.value.ownerId,
              productName: values.productName,
              productId: originalRecord.value.productId,
              quantity: values.quantity,
              completedQuantity: values.completedQuantity,
              execQuantity: values.execQuantity,
              inventoryAttribute: values.inventoryAttribute,
              warehouseName: values.warehouseName,
              targetWarehouseId: originalRecord.value.targetWarehouseId,
              targetLocationCode: values.targetLocationCode,
              batchNumber: values.batchNumber,
              receivingDate: values.receivingDate,
              expiryDate: values.expiryDate,
            };
            console.log('===== 弹窗提交数据 =====');
            console.log(JSON.stringify(submitData, null, 2));
            console.log('productId in submitData:', submitData.productId);
            console.log('ownerId in submitData:', submitData.ownerId);
            console.log('========================');
            await receive(submitData);
            closeModal();
            emit('success');
        } catch ({ errorFields }) {
           if (errorFields) {
             const firstField = errorFields[0];
             if (firstField) {
               scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
             }
           }
           return Promise.reject(errorFields);
        } finally {
            setModalProps({confirmLoading: false});
        }
    }
</script>

<style lang="less" scoped>
	/** 时间和数字输入框样式 */
  :deep(.ant-input-number) {
    width: 100%;
  }

  :deep(.ant-calendar-picker) {
    width: 100%;
  }
</style>
