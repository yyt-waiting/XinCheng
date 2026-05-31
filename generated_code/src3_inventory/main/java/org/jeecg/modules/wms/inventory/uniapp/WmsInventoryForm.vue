<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">库存表</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">商品id：</text></view>
                  <input  placeholder="请输入商品id" v-model="model.productId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">储位编码：</text></view>
                  <input  placeholder="请输入储位编码" v-model="model.locationCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">容器编码：</text></view>
                  <input  placeholder="请输入容器编码" v-model="model.containerCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">在库数量：</text></view>
                  <input type="number" placeholder="请输入在库数量" v-model="model.stockQuantity"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">分配数量：</text></view>
                  <input type="number" placeholder="请输入分配数量" v-model="model.allocatedQuantity"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">可用数量：</text></view>
                  <input type="number" placeholder="请输入可用数量" v-model="model.availableQuantity"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">批号 ：</text></view>
                  <input  placeholder="请输入批号 " v-model="model.batchNumber"/>
                </view>
              </view>
              <my-date label="入库时间：" v-model="model.stockInTime" placeholder="请输入入库时间"></my-date>
              <my-date label="保质期到期日：" fields="day" v-model="model.expiryDate" placeholder="请输入保质期到期日"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">货主：</text></view>
                  <input  placeholder="请输入货主" v-model="model.ownerId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否可售：</text></view>
                  <input  placeholder="请输入是否可售" v-model="model.isSellable"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">仓库id：</text></view>
                  <input  placeholder="请输入仓库id" v-model="model.warehouseId"/>
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
        name: "WmsInventoryForm",
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
                  queryById: "/inventory/wmsInventory/queryById",
                  add: "/inventory/wmsInventory/add",
                  edit: "/inventory/wmsInventory/edit",
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
