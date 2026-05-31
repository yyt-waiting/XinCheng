<route lang="json5" type="page">
{
layout: 'default',
style: {
navigationStyle: 'custom',
navigationBarTitleText: '储位表',
},
}
</route>

<template>
  <PageLayout :navTitle="navTitle" :backRouteName="backRouteName">
    <scroll-view class="scrollArea" scroll-y>
      <view class="form-container">
        <wd-form ref="form" :model="myFormData">
          <wd-cell-group border>
          <view class="{ 'mt-14px': 0 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationCode']"
                  :label="get4Label('库位编码')"
                  name='locationCode'
                  prop='locationCode'
                  placeholder="请选择库位编码"
                  :rules="[
                                  { required: true, message: '请输入库位编码!'},
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationCategory']"
                  :label="get4Label('储位类别')"
                  name='locationCategory'
                  prop='locationCategory'
                  placeholder="请选择储位类别"
                  :rules="[
                                  { required: true, message: '请输入储位类别!'},
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
              <online-select
                :label="get4Label('库位类型')"
                 labelWidth="100px"
                 type="list"
                 name='locationType'
                 dict="location_type"
                v-model="myFormData['locationType']"
              ></online-select>
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
              <online-select
                :label="get4Label('状态')"
                 labelWidth="100px"
                 type="list"
                 name='status'
                 dict="dict_item_status"
                v-model="myFormData['status']"
              ></online-select>
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
             <online-popup-link-record
                :label="get4Label('所属仓库')"
                labelWidth="100px"
                 name='warehouseId'
                :formSchema="getFormSchema('wms_warehouses','id','warehouse_name')"
                v-model:value="myFormData['warehouseId']"
              ></online-popup-link-record>
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
             <online-popup-link-record
                :label="get4Label('所属库区')"
                labelWidth="100px"
                 name='zoneId'
                :formSchema="getFormSchema('wms_storage_zones','id','zone_name,warehouse_id')"
                v-model:value="myFormData['zoneId']"
              ></online-popup-link-record>
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationAisle']"
                  :label="get4Label('巷道')"
                  name='locationAisle'
                  prop='locationAisle'
                  placeholder="请选择巷道"
                  :rules="[
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationLine']"
                  :label="get4Label('排')"
                  name='locationLine'
                  prop='locationLine'
                  placeholder="请选择排"
                  :rules="[
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationRank']"
                  :label="get4Label('列')"
                  name='locationRank'
                  prop='locationRank'
                  placeholder="请选择列"
                  :rules="[
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
              <wd-input
                  label-width="100px"
                  v-model="myFormData['locationLayer']"
                  :label="get4Label('层')"
                  name='locationLayer'
                  prop='locationLayer'
                  placeholder="请选择层"
                  :rules="[
                  ]"
                  clearable
              />
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
               <wd-input
                   label-width="100px"
                   v-model="myFormData['locationLength']"
                   :label="get4Label('长')"
                   name='locationLength'
                   prop='locationLength'
                   placeholder="请选择长"
                   inputMode="numeric"
                   :rules="[
                   ]"
                   clearable
              />
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
               <wd-input
                   label-width="100px"
                   v-model="myFormData['locationWidth']"
                   :label="get4Label('宽')"
                   name='locationWidth'
                   prop='locationWidth'
                   placeholder="请选择宽"
                   inputMode="numeric"
                   :rules="[
                   ]"
                   clearable
              />
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
               <wd-input
                   label-width="100px"
                   v-model="myFormData['locationCapacity']"
                   :label="get4Label('容积')"
                   name='locationCapacity'
                   prop='locationCapacity'
                   placeholder="请选择容积"
                   inputMode="numeric"
                   :rules="[
                   ]"
                   clearable
              />
        </view>
          <view class="{ 'mt-14px': 1 == 0 }">
               <wd-input
                   label-width="100px"
                   v-model="myFormData['loadCapacity']"
                   :label="get4Label('承重')"
                   name='loadCapacity'
                   prop='loadCapacity'
                   placeholder="请选择承重"
                   inputMode="numeric"
                   :rules="[
                   ]"
                   clearable
              />
        </view>
          <view class="{ 'mt-14px': 0 == 0 }">
              <online-select
                :label="get4Label('是否可售')"
                 labelWidth="100px"
                 type="list"
                 name='isSellable'
                 dict="yn"
                v-model="myFormData['isSellable']"
              ></online-select>
        </view>
          </wd-cell-group>
        </wd-form>
      </view>
    </scroll-view>
    <view class="footer">
      <wd-button :disabled="loading" block :loading="loading" @click="handleSubmit">提交</wd-button>
    </view>
  </PageLayout>
</template>

<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app'
import { http } from '@/utils/http'
import { useToast } from 'wot-design-uni'
import { useRouter } from '@/plugin/uni-mini-router'
import { ref, onMounted, computed,reactive } from 'vue'
import OnlineImage from '@/components/online/view/online-image.vue'
import OnlineFile from '@/components/online/view/online-file.vue'
import OnlineFileCustom from '@/components/online/view/online-file-custom.vue'
import OnlineSelect from '@/components/online/view/online-select.vue'
import OnlineTime from '@/components/online/view/online-time.vue'
import OnlineDate from '@/components/online/view/online-date.vue'
import OnlineRadio from '@/components/online/view/online-radio.vue'
import OnlineCheckbox from '@/components/online/view/online-checkbox.vue'
import OnlineMulti from '@/components/online/view/online-multi.vue'
import OnlinePopupLinkRecord from '@/components/online/view/online-popup-link-record.vue'
import SelectDept from '@/components/SelectDept/SelectDept.vue'
import SelectUser from '@/components/SelectUser/SelectUser.vue'
defineOptions({
  name: 'WmsStorageLocationsForm',
  options: {
    styleIsolation: 'shared',
  },
})
const toast = useToast()
const router = useRouter()
const form = ref(null)
// 定义响应式数据
const myFormData = reactive({})
const loading = ref(false)
const navTitle = ref('新增')
const dataId = ref('')
const backRouteName = ref('WmsStorageLocationsList')
// 定义 initForm 方法
const initForm = (item) => {
  console.log('initForm item', item)
  if(item?.dataId){
    dataId.value = item.dataId;
    navTitle.value = item.dataId?'编辑':'新增';
    initData();
  }
}
// 初始化数据
const initData = () => {
  http.get("/warehouse/wmsStorageLocations/queryById",{id:dataId.value}).then((res) => {
    if (res.success) {
      let obj = res.result
      Object.assign(myFormData, { ...obj })
    }else{
      toast.error(res?.message || '表单加载失败！')
    }
  })
}
const handleSuccess = () => {
  uni.$emit('refreshList');
  router.back()
}
// 提交表单
const handleSubmit = () => {
  let url = dataId.value?'/warehouse/wmsStorageLocations/edit':'/warehouse/wmsStorageLocations/add';
  form.value
    .validate()
    .then(({ valid, errors }) => {
      if (valid) {
        loading.value = true;
        http.post(url,myFormData).then((res) => {
          loading.value = false;
          if (res.success) {
            toast.success('保存成功');
            handleSuccess()
          }else{
            toast.error(res?.message || '表单保存失败！')
          }
        })
      }
    })
    .catch((error) => {
      console.log(error, 'error')
      loading.value = false;
    })
}
// 标题
const get4Label = computed(() => {
  return (label) => {
    return label && label.length > 4 ? label.substring(0, 4) : label;
  }
})

// 标题
const getFormSchema = computed(() => {
  return (dictTable,dictCode,dictText) => {
    return {
      dictCode,
      dictTable,
      dictText
    };
  }
})
/**
 * 获取日期控件的扩展类型
 * @param picker
 * @returns {string}
 */
const getDateExtendType = (picker: string) => {
    let mapField = {
      month: 'year-month',
      year: 'year',
      quarter: 'quarter',
      week: 'week',
      day: 'date',
    }
    return picker && mapField[picker]
      ? mapField[picker]
      : 'date'
}
//设置pop返回值
const setFieldsValue = (data) => {
   Object.assign(myFormData, {...data })
}
// onLoad 生命周期钩子
onLoad((option) => {
  initForm(option)
})
</script>

<style lang="scss" scoped>
.footer {
  width: 100%;
  padding: 10px 20px;
  padding-bottom: calc(constant(safe-area-inset-bottom) + 10px);
  padding-bottom: calc(env(safe-area-inset-bottom) + 10px);
}
</style>
