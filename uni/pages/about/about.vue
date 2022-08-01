<template>
<view class="container">

  <view class="about-item">

    <view class="item-left">
      <label class="jnbook_f12">项目名称: </label>
       <text>{{Info.name}}</text>
    </view>
    <view class="item-right" @tap="showLocation">
      <image src="/static/static/images/ico-addr.png" class="right-icon"></image>
    </view>
  </view>

  <view class="about-item">
    <view class="item-left">
      <label class="jnbook_f12">项目地址: </label>
       <text>{{Info.address}}</text>
    </view>
  </view>

  <view class="about-item">
    <view class="item-left">
      <label class="jnbook_f12">电话号码:    </label>
       <text>{{Info.linkPhone}}</text>
    </view>
    <view class="item-right" @tap="callPhone">
      <image src="/static/static/images/telephone.png" class="right-icon"></image>
    </view>
  </view>

  <view class="about-item">
    <view class="item-left">
      <label class="jnbook_f12">QQ: </label>
       <text>{{Info.qqNumber}}</text>
    </view>
    <view class="item-right">
      <image src="/static/static/images/mobile.png" class="right-icon"></image>
    </view>
  </view>

  <view class="about-item">
    <view class="item-left">
      <label class="jnbook_f12">当前版本: </label>
       <text>1.0.0</text>
    </view>
  </view>
</view>
</template>

<script>
var app = getApp();
var util = require("../../utils/util.js");
var api = require("../../config/api.js");

export default {
  data() {
    return {
      load_statue: true,
      Info: {
        name: '江南E书',
        address: 'https://gitee.com/mindfaker/jnbook',
        latitude:  31.4895504844,
        longitude: 120.2675724214,
        linkPhone: '15877777777',
        qqNumber: '2743420496'
      }
    };
  },

  components: {},
  props: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {},
  methods: {
    showLocation: function (e) {
      var that = this;
      uni.openLocation({
        latitude: that.Info.latitude,
        longitude: that.Info.longitude,
        name: that.Info.name,
        address: that.Info.address
      });
    },
    callPhone: function (e) {
      var that = this;
      uni.makePhoneCall({
        phoneNumber: that.Info.linkPhone
      });
    },
    reLoad: function (e) {
      this.loadInfo();
    }
  }
};
</script>
<style>
/* about.wxss */

page{
  height: 100%;
  background-color: #F2f2f2;
}
.page-view{
  height: 100%;
}
.banner-image{
  width: 100%;
  height: 350rpx;
  background: #ee1;
  margin-bottom: 30rpx;
  border-bottom: solid #f2f2f2 0.5dp;
}

.about-item{
  background: white;
  border-top: solid #f2f2f2 0.2rpx;
  border-bottom: solid #f2f2f2 0.2rpx;
  width: 100%;
  height: 100rpx;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.item-left{
  font-size: 30rpx;
  margin-left: 15rpx;
  margin-top: auto;
  margin-bottom: auto;
}
.item-right{

  margin-right: 15rpx;
  margin-top: auto;
  margin-bottom: auto;
}
.right-icon{
  width: 40rpx;
  height: 40rpx;
}
</style>
