<template>
<view class="container">
  <view class="profile-info" @tap="goLogin">
    <image class="avatar" :src="userInfo.avatarUrl"></image>
    <view class="info">
      <text class="name">{{userInfo.nickName}}</text>
      <text class="level" v-if="hasLogin">VIP用户</text>
    </view>
     <view class="brokerage" v-if="hasLogin" @tap="goBrokerage">
      <text class="name"> 总收益 </text>
      <text class="money">¥ {{totalAmount}}</text>
    </view>
  </view>

  <view class="separate"></view>

  <view class="user_area">
    <view class="user_row" @tap="goOrder">
      <view class="user_row_left">我的订单</view>
      <image class="user_row_right" src="/static/static/images/goright.png"></image>
    </view>
    <view class="user_column">
      <view class="user_column_item" @tap="goOrderIndex" data-index="1" data-route="/pages/ucenter/order/order">
        <text class="user_column_item_badge" v-if="order.unpaid != 0">{{order.unpaid}}</text>
        <image class="user_column_item_image" src="/static/static/images/pendpay.png">
        </image>
        <view class="user_column_item_text">待付款</view>
      </view>
      <view class="user_column_item" @tap="goOrderIndex" data-index="2" data-route="/pages/ucenter/order/order">
        <text class="user_column_item_badge" v-if="order.unship != 0">{{order.unship}}</text>
        <image class="user_column_item_image" src="/static/static/images/send.png"></image>
        <view class="user_column_item_text">待发货</view>
      </view>
      <view class="user_column_item" @tap="goOrderIndex" data-index="3" data-route="/pages/ucenter/order/order">
        <text class="user_column_item_badge" v-if="order.unrecv != 0">{{order.unrecv}}</text>
        <image class="user_column_item_image" src="/static/static/images/receive.png"></image>
        <view class="user_column_item_text">待收货</view>
      </view>
      <view class="user_column_item" @tap="goOrderIndex" data-index="4" data-route="/pages/ucenter/order/order">
        <text class="user_column_item_badge" v-if="order.uncomment != 0">{{order.uncomment}}</text>
        <image class="user_column_item_image" src="/static/static/images/comment.png"></image>
        <view class="user_column_item_text">待评价</view>
      </view>
    </view>
  </view>

  <view class="separate"></view>

  <view class="user_row">
    <view class="user_row_left">核心服务</view>
  </view>
  <view class="user_column">
    <view class="user_column_item" @tap="goCollect">
      <image class="user_column_item_image" src="/static/static/images/icon_collect.png"></image>
      <view class="user_column_item_text">图书收藏</view>
    </view>
    <view class="user_column_item" @tap="goFootprint">
      <image class="user_column_item_image" src="/static/static/images/footprint.png"></image>
      <view class="user_column_item_text">浏览足迹</view>
    </view>
    <view class="user_column_item" @tap="goAfterSale">
      <image class="user_column_item_image" src="/static/static/images/aftersale.png"></image>
      <view class="user_column_item_text">售后</view>
    </view>
  </view>
  <view class="separate"></view>

  <view class="user_row">
    <view class="user_row_left">必备工具</view>
  </view>
  <view class="user_column">
    <view class="user_column_item" @tap="goAddress">
      <image class="user_column_item_image" src="/static/static/images/address.png"></image>
      <view class="user_column_item_text">地址管理</view>
    </view>
    <button class="user_column_item_phone" open-type="getPhoneNumber" @getphonenumber="bindPhoneNumber">
      <image class="user_column_item_image" src="/static/static/images/mobile.png"></image>
      <view class="user_column_item_text">绑定手机</view>
    </button>
    <view class="user_column_item" @tap="goFeedback">
      <image class="user_column_item_image" src="/static/static/images/feedback.png"></image>
      <view class="user_column_item_text">意见反馈</view>
    </view>
    <view class="user_column_item">
      <contact-button style="opacity:0;position:absolute;" type="default-dark" session-from="weapp" size="27">
      </contact-button>
      <image class="user_column_item_image" src="/static/static/images/customer.png"></image>
      <view class="user_column_item_text">联系客服</view>
    </view>
    <view class="user_column_item" @tap="aboutUs">
      <image class="user_column_item_image" src="/static/static/images/about_us.png"></image>
      <view class="user_column_item_text">关于我们</view>
    </view>
  </view>

  <view class="logout" v-if="hasLogin" @tap="exitLogin">退出登录</view>
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
      userInfo: {
        nickName: '点击登录',
        avatarUrl: 'http://yanxuan.nosdn.127.net/8945ae63d940cc42406c3f67019c5cb6.png'
      },
      order: {
        unpaid: 0,
        unship: 0,
        unrecv: 0,
        uncomment: 0
      },
      hasLogin: false,
      totalAmount: 0.00
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {// 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {},
  onShow: function () {
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = uni.getStorageSync('userInfo');
      this.setData({
        userInfo: userInfo,
        hasLogin: true
      });
      let that = this;
      util.request(api.UserIndex).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            order: res.data.order,
            totalAmount: res.data.totalAmount
          });
        }
      });
    }
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    goLogin() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }
    },

    goOrder() {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/ucenter/order/order"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }
    },

    goOrderIndex(e) {
      if (this.hasLogin) {
        let tab = e.currentTarget.dataset.index;
        let route = e.currentTarget.dataset.route;

        try {
          uni.setStorageSync('tab', tab);
        } catch (e) {}

        uni.navigateTo({
          url: route,
          success: function (res) {},
          fail: function (res) {},
          complete: function (res) {}
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    goCollect() {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/ucenter/collect/collect"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    goFeedback(e) {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/ucenter/feedback/feedback"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    goFootprint() {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/ucenter/footprint/footprint"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    goAddress() {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/ucenter/address/address"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    bindPhoneNumber: function (e) {
      if (e.detail.errMsg !== "getPhoneNumber:ok") {
        // 拒绝授权
        return;
      }

      if (!this.hasLogin) {
        uni.showToast({
          title: '绑定失败：请先登录',
          icon: 'none',
          duration: 2000
        });
        return;
      }

      util.request(api.AuthBindPhone, {
        iv: e.detail.iv,
        encryptedData: e.detail.encryptedData
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          uni.showToast({
            title: '绑定手机号码成功',
            icon: 'success',
            duration: 2000
          });
        }
      });
    },
    goAfterSale: function () {
      uni.showToast({
        title: '目前不支持',
        icon: 'none',
        duration: 2000
      });
    },
    aboutUs: function () {
      uni.navigateTo({
        url: '/pages/about/about'
      });
    },

    goBrokerage() {
      if (this.hasLogin) {
        uni.navigateTo({
          url: "/pages/brokerage/main/main"
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      ;
    },

    exitLogin: function () {
      uni.showModal({
        title: '',
        confirmColor: '#b4282d',
        content: '退出登录？',
        success: function (res) {
          if (!res.confirm) {
            return;
          }

          util.request(api.AuthLogout, {}, 'POST');
          app.globalData.hasLogin = false;
          uni.removeStorageSync('token');
          uni.removeStorageSync('userInfo');
          uni.reLaunch({
            url: '/pages/index/index'
          });
        }
      });
    }
  }
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

.profile-info {
  background-color: #388ceb;
  color: #f4f4f4;
  padding: 30rpx;
  font-size: 28rpx;
  overflow: hidden;
}

.profile-info .avatar {
  height: 150rpx;
  width: 150rpx;
  border-radius:50%;
  text-align: center;
  float: left;
}

.profile-info .info {
  padding-top: 32rpx;
  height: 85rpx;
  padding-left: 21.25rpx;
  width:40%;
  float: left;
}

.profile-info .brokerage {
  padding-top: 32rpx;
  text-align: center;
  height: 85rpx;
  padding-left: 11.25rpx;
  width:35%;
  float: right;
}

.profile-info .name {
  display: block;
  height: 45rpx;
  line-height: 45rpx;
  color: #fff;
  font-size: 35.5rpx;
  margin-bottom: 15rpx;
}

.profile-info .level {
  display: block;
  height: 30rpx;
  line-height: 30rpx;
  margin-bottom: 10rpx;
  color: #fff;
  font-size: 26rpx;
}

.profile-info .money {
  display: block;
  height: 30rpx;
  line-height: 30rpx;
  margin-bottom: 10rpx;
  color: #ffe600;
  font-size: 34rpx;
}

.user_area {
  /* border: 1px solid black; */
  width: 100%;
  height: 226rpx;
  /* margin: 0 auto; */
  margin-top: -8rpx;
  background: #fff;
  /* border-top: 1px solid #f4f4f4; */
}

.user_row {
  /* border: 1px solid black; */
  height: 86rpx;
  line-height: 86rpx;
  border-bottom: 1px solid #fafafa;
}

.user_row_left {
  /* border: 1px solid #757575; */
  float: left;
  height: 86rpx;
  font-weight: 550;
  line-height: 86rpx;
  margin-left: 35rpx;
  font-size: 26rpx;
  letter-spacing: 1rpx;
}

.user_row_right {
  /* border: 1px solid #757575; */
  float: right;
  height: 40rpx;
  width: 40rpx;
  font-weight: 550;
  line-height: 86rpx;
  margin-top: 28rpx;
  margin-right: 30rpx;
}

.user_column {
  /* border: 1px solid black; */
  height: 140rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.user_column_item {
  width: 30%;
  height: 140rpx;
  background: #fff;
  text-align: center;
  position: relative;
}

.user_column_item_badge {
  height: 28rpx;
  width: 28rpx;
  position: absolute;
  background: #b4282d;
  color: #fff;
  border-radius: 50%;
  margin-top: 20rpx;
  margin-left: 40rpx;
}

.user_column_item_image {
  width: 50rpx;
  height: 50rpx;
  margin-top: 30rpx;
}

.user_column_item_text {
  /* border: 1px solid black; */
  margin-top: 5rpx;
  font-size: 24rpx;
  color: #555;
}

.separate {
  background: #e0e3da;
  width: 100%;
  height: 6rpx;
}

.user_column_item_phone {
  width: 30%;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  float: left;
  background: #fff;
  border-bottom: 1px solid #fafafa;
}

.user_column_item_phone::after {
  border: none;
}

.logout {
  margin-top: 30rpx;
  height: 100rpx;
  width: 100%;
  line-height: 100rpx;
  text-align: center;
  background: #fff;
  color: red;
  font-size: 30rpx;
}

</style>
