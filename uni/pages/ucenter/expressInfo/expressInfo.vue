<template>
<view class="container">

  <view class="topExpress">
    <view class="topExpress-left">
       <image class="avatar" :src="userInfo.avatarUrl"></image>
    </view>
    <view class="topExpress-right">
      <view class="topExpress-right-top">{{shipperName}}</view>
      <view class="topExpress-right-middle">运单号：{{shipperCode}}</view>
      <view class="topExpress-right-middle">收货地址：{{address}}</view>
    </view>
  </view>


  <!-- 物流时间轴 -->
  <view class="expressRecord">
      <!-- 顶部收货地址 -->
      <view class="expressRecord-getAddress">
          <view class="expressRecord-top">
            <view class="getAddress-icon unfinish" v-if="state != 3"></view>
            <view class="getAddress-icon finish" v-if="state == 3"></view>
            <view class="getAddress-text" v-if="state == 2">运输中</view>
            <view class="getAddress-text" v-if="state == 3">已签收</view>
            <view class="getAddress-text" v-if="state == 4">快递如有疑问请联系客服</view>
          </view>
      </view>

    <!-- 顶部收货地址半个时间轴线 -->
    <view class="noReach-online-top-close"></view>


    <!-- 单个物流记录点时间轴：当前正在进行的物流状态 -->
    <view v-for="(item, index) in traces" :key="index" class="expressRecord-single-close">

      <!-- 左边子容器 -->
      <view class="expressRecord-single-noReach-online-top-close">
        <!-- 正在进行的时间轴上半个时间线 -->
        <view class="online-top-closing"></view>
        <!-- 正在进行的时间轴点 -->
        <view class="dot-close"></view>
        <!-- 正在进行的时间轴下半个时间线 -->
        <view class="online-bottom"></view>
      </view>

      <!-- 右边子容器 -->
      <view class="expressRecord-text">
        <view class="expressRecord-status-address">{{item.acceptStation}}</view>
      </view>

      <!-- 相对父级容器绝对定位的日期 -->
      <view class="expressRecord-date">
        <view class="expressRecord-date-text">
         {{item.date}}
        </view>
        <view class="expressRecord-date-time">
          {{item.time}}
        </view>
      </view>
    </view>

    <view class="expressRecord-single-close">
      <view class="expressRecord-single-noReach-online-top-close">
        <view class="online-top-close"></view>
        <view class="dot-close"></view>
        <!-- 起始位置，下半个时间轴线不用 -->
        <view class="online-bottom-start"></view>
      </view>

      <view class="expressRecord-text">
        <view class="expressRecord-status-address">卖家发货</view>
      </view>

      <view class="expressRecord-date">
        <view class="expressRecord-date-text">
          {{shipDate}}
        </view>
        <view class="expressRecord-date-time">
          {{shipTime}}
        </view>
      </view>

    </view>

  </view>
</view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var user = require("../../../utils/user.js");
var app = getApp();

export default {
  data() {
    return {
      orderId: 0,
      userInfo: {
        nickName: '点击登录',
        avatarUrl: 'http://yanxuan.nosdn.127.net/8945ae63d940cc42406c3f67019c5cb6.png'
      },
      traces: [],
      hasLogin: false,
      state: "",
      shipperName: "",
      shipDate: "",
      shipTime: "",
      shipperCode: "",
      address: ""
    };
  },

  components: {},
  props: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      orderId: options.orderId
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {},

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = uni.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      let that = this;
      util.request(api.ExpressTrace, {
        orderId: that.orderId
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            traces: res.data.traces,
            state: res.data.state,
            shipperName: res.data.shipperName,
            shipDate: res.data.shipDate,
            shipTime: res.data.shipTime,
            shipperCode: res.data.shipperCode,
            address: res.data.address
          });
        }
      });
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {},

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {},

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {},

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {},

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {},
  methods: {}
};
</script>
<style>
page {
  height: 100%;
  width: 100%;
  background: #f4f4f4;
}

.container {
  background: #f4f4f4;
  height: auto;
  overflow: hidden;
  width: 100%;
}

.topExpress {
  width: 710rpx;
  height: 155rpx;
  background: #fff;
  margin: 10rpx auto;
  display: flex;
  border-radius: 10rpx;
}

.topExpress-left {
  width: 150rpx;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.topExpress-left .avatar {
  height: 100rpx;
  width: 100rpx;
  border-radius:50%;
  text-align: center;
  float: left;
}


.topExpress-right {
  font-size: 26rpx;
  color: #333;
  display: flex;
  justify-content: space-around;
  flex-direction: column;
  align-items: flex-start;
  padding: 20rpx 0;
}

.topExpress-right-middle {
  font-size: 28rpx;
}

.topExpress-right-bottom {
  font-size: 28rpx;
  color: #666;
}

.expressRecord {
  width: 710rpx;
  padding-top: 30rpx;
  padding-bottom: 200rpx;
  background: #fff;
  margin: 0 auto;
  border-radius: 10rpx;
}

.expressRecord-getAddress {
  width: 100%;
  font-size: 28rpx;
  color: #999;
  display: flex;
}

.expressRecord-top {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.getAddress-icon {
  width: 30rpx;
  height: 30rpx;
  border-radius: 50%;
  font-size: 28rpx;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 152rpx;
}
.finish {
  background: #45dd09;
}
.unfinish {
  background: #f7f303;
}
.getAddress-text {
  margin-left: 20rpx;
}

.noReach-online-top-close {
  width: 1rpx;
  height: 50rpx;
  background: #999;
  margin-left: 165rpx;
}

.expressRecord-single-close {
  width: 100%;
  height: 122rpx;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  position: relative;
}

.expressRecord-single-noReach-online-top-close {
  display: flex;
  flex-direction: column;
}

.online-top-closing {
  width: 2rpx;
  height: 50rpx;
  background: #999;
  margin-left: 165rpx;
}

.online-top-close {
  width: 2rpx;
  height: 50rpx;
  background: #999;
  margin-left: 165rpx;
}

.dot-close {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  margin-left: 155rpx;
  margin-top: 6rpx;
  margin-bottom: 6rpx;
  background: rgb(21, 216, 4);
}

.online-bottom {
  width: 2rpx;
  height: 50rpx;
  background: #999;
  margin-left: 165rpx;
}

.online-bottom-start {
  width: 2rpx;
  height: 50rpx;
  /* background: #999; */
  margin-left: 165rpx;
}

.expressRecord-text {
  margin-left: 30rpx;
}

.expressRecord-statusing {
  font-size: 28rpx;
  color: #999;
}

.expressRecord-status-address {
  font-size: 28rpx;
  color: #666;
}


.expressRecord-status {
  font-size: 35rpx;
  color: #999;
}

.expressRecord-date {
  position: absolute;
  height: 100%;
  /* top: 0;
  bottom: 0; */
  left: 15rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  color: #666;
}

.expressRecord-date-text {
  font-size: 28rpx;
}

.expressRecord-date-time {
  font-size: 28rpx;
}
</style>