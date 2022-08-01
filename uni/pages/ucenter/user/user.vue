<template>
<form @submit="formSubmit">
  <view class="personal-data">
    <view class="list">
        <view class="item acea-row row-between-wrapper">
          <view>头像</view>
          <view class="pictrue"><image :src="userInfo.avatarUrl"></image></view>
        </view>
         <!--  #ifndef  MP-WEIXIN -->
        <view class="item acea-row row-between-wrapper">
          <view>用户名</view>
          <view class="input"><input type="text" disabled="true" name="username" :value="userInfo.userName"></input></view>
        </view>
        <!--  #endif -->
        <view class="item acea-row row-between-wrapper">
          <view>昵称</view>
          <view class="input"><input type="text" disabled="true" name="nickname" :value="userInfo.nickName"></input></view>
        </view>

         <view class="item acea-row row-between-wrapper">
          <view>所在校区</view>
          <view class="input"><input type="text" disabled="true" name="compusname" :value="userInfo.compusname"></input></view>
        </view>



        <!--  #ifdef  MP-WEIXIN -->
        <view class="item acea-row row-between-wrapper">
          <view>手机号码</view>
          <button name="phone" class="phone" :value="userInfo.phone" v-if="!userInfo.phone && userInfo.openId!='-1'" @getphonenumber="getPhoneNumber" hover-class="none" open-type="getPhoneNumber">
             点击获取
          </button>
          <view class="input acea-row row-between-wrapper" v-if="userInfo.phone!='-1'" >
            <input type="text" disabled="true" name="phone" :value="userInfo.phone" class="id"></input>
            <text class="iconfont icon-suozi"></text>
          </view>
        </view>
        <!--  #endif -->

        <!--  #ifndef  MP-WEIXIN -->
        <view class="item acea-row row-between-wrapper">
          <view>手机号码</view>
          <button name="phone" class="phone" :value="userInfo.phone" v-if="!userInfo.phone && userInfo.openId!='-1'" @getphonenumber="getPhoneNumber" hover-class="none" open-type="getPhoneNumber">
             点击获取
          </button>
          <view class="input acea-row row-between-wrapper" v-if="userInfo.phone" >
            <input type="text" disabled="true" name="phone" :value="userInfo.phone" class="id"></input>
            <text class="iconfont icon-suozi"></text>
          </view>
        </view>
        <!--  #endif -->

        <!--  #ifdef  MP-WEIXIN -->
        <view class="item acea-row row-between-wrapper">
          <view>代理申请</view>
          <view class="input acea-row row-between-wrapper" v-if="userInfo.userLevel==2">
            <input type="text" disabled="true" :value="userInfo.userLevelDesc" class="id"></input>
            <text class="iconfont icon-huangguan"></text>
          </view>
          <view class="input acea-row row-between-wrapper" v-else-if="userInfo.status == 3">
            <input type="text" disabled="true" value="首次登录用户名密码都是昵称" class="id"></input>
            <text class="iconfont icon-huangguan"></text>
          </view>
          <button name="phone" class="phone" v-else hover-class="none" @tap="applyAgency">
             点击申请
          </button>
        </view>
        <!--  #endif -->

        <!--  #ifndef  MP-WEIXIN -->
        <view class="item acea-row row-between-wrapper">
          <view>代理申请</view>
          <view class="input acea-row row-between-wrapper" v-if="userInfo.userLevel==2">
            <input type="text" disabled="true" :value="userInfo.userLevelDesc" class="id"></input>
            <text class="iconfont icon-huangguan"></text>
          </view>
          <view class="input acea-row row-between-wrapper" v-else-if="userInfo.status == 3">
            <input type="text" disabled="true" value="使用现有用户名密码登录即可" class="id"></input>
            <text class="iconfont icon-huangguan"></text>
          </view>
          <button name="phone" class="phone" v-else hover-class="none" @tap="applyAgency">
             点击申请
          </button>
        </view>
        <!--  #endif -->

        <view class="item acea-row row-between-wrapper">
          <view>ID号</view>
          <view class="input acea-row row-between-wrapper">
            <input type="text" :value="'1000' + userInfo.userId" disabled="true" class="id"></input>
            <text class="iconfont icon-suozi"></text>
          </view>
        </view>

    </view>
    <button class="editBnt" @tap="goPages" >修    改    信    息</button>
    <button class="modifyBnt" @tap="exitLogin">退    出    登    录</button>
  </view>
</form>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var user = require("../../../utils/user.js");
var app = getApp();

export default {
  data() {
    return {
      userInfo: {},
      hasLogin: false
    };
  },

  components: {},
  props: {},

  /**
      * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {},
  onShow: function () {
    let that = this; //获取用户的登录信息
    let userInfo = uni.getStorageSync('userInfo');
    this.setData({
      userInfo: userInfo,
      hasLogin: true
    });
    console.log(userInfo);
  },
  methods: {
    /**
     * 页面跳转
     */
    goPages: function () {
      console.log("yt");
      if (this.hasLogin) {
        console.log("yt1");
        uni.navigateTo({
          url: "/pages/ucenter/editInfo/editInfo",
        });
      } else {
        console.log("yt2");
        uni.navigateTo({

          url: "/pages/auth/login/login",
        });
      }
    },

    applyAgency: function () {
      let that = this;

      if (!this.hasLogin) {
        uni.showToast({
          title: '绑定失败：请先登录',
          icon: 'none',
          duration: 2000
        });
        return;
      }

      util.request(api.ApplyAgency, {
        desc: "代理申请"
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          let userInfo = uni.getStorageSync('userInfo');
          userInfo.status = 3; //设置用户的状态为申请代理中...

          uni.setStorageSync('userInfo', userInfo);
          that.setData({
            userInfo: userInfo,
            hasLogin: true
          });
          uni.showToast({
            title: '申请成功，等待管理员审批',
            icon: 'success',
            duration: 2000
          });
        }
      });
    },
    getPhoneNumber: function (e) {
      let that = this;

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
          let userInfo = uni.getStorageSync('userInfo');
          userInfo.phone = res.data.phone; //设置手机号码

          uni.setStorageSync('userInfo', userInfo);
          that.setData({
            userInfo: userInfo,
            hasLogin: true
          });
          uni.showToast({
            title: '绑定手机号码成功',
            icon: 'success',
            duration: 2000
          });
        }
      });
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
.personal-data .list{margin-top:15rpx;background-color:#fff;}
.personal-data .list .item{padding:30rpx 30rpx 30rpx 0;border-bottom:1rpx solid #f2f2f2;margin-left:30rpx;font-size:32rpx;color:#282828;}
.personal-data .list .item .phone{background-color:#388ceb;width:160rpx;height:56rpx;font-size:24rpx;color:#fff;line-height:56rpx;border-radius: 32rpx}
.personal-data .list .item .pictrue{width:88rpx;height:88rpx;}
.personal-data .list .item .pictrue image{width:100%;height:100%;border-radius:50%;}
.personal-data .list .item .input{width:415rpx;text-align:right;color:#868686;}
.personal-data .list .item .input .id{width:365rpx;}
.personal-data .list .item .input .iconfont{font-size:35rpx;}
.personal-data .modifyBnt{background-color:#FF0000;font-size:32rpx;color:#fff;width:690rpx;height:90rpx;border-radius:50rpx;text-align:center;line-height:90rpx;margin:76rpx auto 0 auto;}
.personal-data .editBnt{background-color:#388ceb;font-size:32rpx;color:#fff;width:690rpx;height:90rpx;border-radius:50rpx;text-align:center;line-height:90rpx;margin:76rpx auto 0 auto;}
</style>
