<template>
<view class="container">
  <view class="login-box">
    <!-- #ifdef MP-WEIXIN -->
    <button v-if="canIUseGetUserProfile" type="primary" class="wx-login-btn" @tap="getUserProfile">微信直接登录</button>
    <button v-else open-type="getUserInfo" type="primary" class="wx-login-btn" @getuserinfo="wxLogin">微信直接登录</button>
    <!-- #endif -->
    <button type="primary" class="account-login-btn" @tap="accountLogin">账号登录</button>
  </view>
</view>
</template>

<script>
var api = require("../../../config/api.js");
var util = require("../../../utils/util.js");
var user = require("../../../utils/user.js");
var app = getApp();

export default {
  data() {
    return {
      canIUseGetUserProfile: false // 用于向前兼容
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    if (uni.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true
      });
    }
  },
  onReady: function () {},
  onShow: function () {// 页面显示
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    getUserProfile(e) {
      uni.getUserProfile({
        desc: '用于完善会员资料',
        success: res => {
          user.checkLogin().catch(() => {
            user.loginByWeixin(res.userInfo).then(res => {
              app.globalData.hasLogin = true;
              uni.navigateBack({
                delta: 1
              });
            }).catch(err => {
              app.globalData.hasLogin = false;
              util.showErrorToast('微信登录失败');
            });
          });
        },
        fail: res => {
          app.globalData.hasLogin = false;
          util.showErrorToast('微信登录失败');
        }
      });
    },

    wxLogin: function (e) {
      if (e.detail.userInfo == undefined) {
        app.globalData.hasLogin = false;
        util.showErrorToast('登录失败');
        return;
      }

      user.checkLogin().catch(() => {
        user.loginByWeixin(e.detail.userInfo).then(res => {
          app.globalData.hasLogin = true;
          uni.navigateBack({
            delta: 1
          });
        }).catch(err => {
          app.globalData.hasLogin = false;
          util.showErrorToast('登录失败');
        });
      });
    },
    accountLogin: function () {
      uni.navigateTo({
        url: "/pages/auth/accountLogin/accountLogin"
      });
    }
  }
};
</script>
<style>
.login-box {
  width: 100%;
  height: auto;
  overflow: hidden;
  padding: 0 40rpx;
  margin-top: 200rpx;
  background: #f4f4f4;
}

.wx-login-btn {
  margin: 60rpx 0 40rpx 0;
  height: 96rpx;
  line-height: 96rpx;
  font-size: 30rpx;
  border-radius: 6rpx;
  width: 90%;
  color: #fff;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: flex;
  bottom: 0;
  left: 0;
  padding: 0;
  margin-left: 5%;
  text-align: center;
  /* padding-left: -5rpx; */
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
}

.account-login-btn {
  width: 90%;
  margin: 0 auto;
  color: #fff;
  font-size: 30rpx;
  height: 96rpx;
  line-height: 96rpx;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: flex;
  bottom: 0;
  left: 0;
  border-radius: 0;
  padding: 0;
  margin-left: 5%;
  text-align: center;
  /* padding-left: -5rpx; */
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #388ceb 0%, #388ceb 100%);
}

</style>