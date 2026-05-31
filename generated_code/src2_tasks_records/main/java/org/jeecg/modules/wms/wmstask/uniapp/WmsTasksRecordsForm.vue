<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">任务执行记录表</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">任务号：</text></view>
                  <input  placeholder="请输入任务号" v-model="model.taskNumber"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">任务类型：</text></view>
                  <input  placeholder="请输入任务类型" v-model="model.taskType"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">商品id：</text></view>
                  <input  placeholder="请输入商品id" v-model="model.productId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">执行数量：</text></view>
                  <input type="number" placeholder="请输入执行数量" v-model="model.execQuantity"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">来源储位编码：</text></view>
                  <input  placeholder="请输入来源储位编码" v-model="model.sourceLocationCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">目的储位编码：</text></view>
                  <input  placeholder="请输入目的储位编码" v-model="model.targetLocationCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">来源容器编码：</text></view>
                  <input  placeholder="请输入来源容器编码" v-model="model.sourceContainerCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">目的容器编码：</text></view>
                  <input  placeholder="请输入目的容器编码" v-model="model.targetContainerCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">执行人：</text></view>
                  <input  placeholder="请输入执行人" v-model="model.operator"/>
                </view>
              </view>
              <my-date label="执行时间：" v-model="model.operationTime" placeholder="请输入执行时间"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">入库单id：</text></view>
                  <input  placeholder="请输入入库单id" v-model="model.stockInOrderId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">入库明细id：</text></view>
                  <input  placeholder="请输入入库明细id" v-model="model.stockInOrderItemId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">波次单id：</text></view>
                  <input  placeholder="请输入波次单id" v-model="model.waveOrderId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">库存属性：</text></view>
                  <input  placeholder="请输入库存属性" v-model="model.inventoryAttribute"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">任务id：</text></view>
                  <input  placeholder="请输入任务id" v-model="model.taskId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">批次号：</text></view>
                  <input  placeholder="请输入批次号" v-model="model.batchNumber"/>
                </view>
              </view>
              <my-date label="保质期：" fields="day" v-model="model.expiryDate" placeholder="请输入保质期"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">出库单id：</text></view>
                  <input  placeholder="请输入出库单id" v-model="model.outOrderId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">波次拣货明细id：</text></view>
                  <input  placeholder="请输入波次拣货明细id" v-model="model.waveSkuSummaryId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">来源仓库：</text></view>
                  <input  placeholder="请输入来源仓库" v-model="model.sourceWarehouseId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">目的仓库：</text></view>
                  <input  placeholder="请输入目的仓库" v-model="model.targetWarehouseId"/>
                </view>
              </view>
				<view class="padding">
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onSubmit">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>提交
					</button>
				</view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "WmsTasksRecordsForm",
        components:{ myDate },
        props:{
          formData:{
              type:Object,
              default:()=>{},
              required:false
          }
        },
        data(){
            return {
				CustomBar: this.CustomBar,
				NavBarColor: this.NavBarColor,
				loading:false,
                model: {},
                backRouteName:'index',
                url: {
                  queryById: "/wmstask/wmsTasksRecords/queryById",
                  add: "/wmstask/wmsTasksRecords/add",
                  edit: "/wmstask/wmsTasksRecords/edit",
                },
            }
        },
        created(){
             this.initFormData();
        },
        methods:{
           initFormData(){
               if(this.formData){
                    let dataId = this.formData.dataId;
                    this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>{
                        if(res.data.success){
                            console.log("表单数据",res);
                            this.model = res.data.result;
                        }
                    })
                }
            },
            onSubmit() {
                let myForm = {...this.model};
                this.loading = true;
                let url = myForm.id?this.url.edit:this.url.add;
				this.$http.post(url,myForm).then(res=>{
				   console.log("res",res)
				   this.loading = false
				   this.$Router.push({name:this.backRouteName})
				}).catch(()=>{
					this.loading = false
				});
            }
        }
    }
</script>
