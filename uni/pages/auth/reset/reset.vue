<template>
<view class="container">
  <view class="form-box">

     <view class="form-item">
      <input class="email" :value="email" @input="bindEmailInput" placeholder="邮箱"></input>
       <image class="clear" id="clear-email" v-if="email.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item-code">
      <view class="form-item code-item">
        <input class="code" :value="code" @input="bindCodeInput" placeholder="验证码"></input>
        <image class="clear" id="clear-code" v-if="code.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
      </view>
      <view class="code-btn" @tap="sendCode">获取验证码</view>
    </view>

    <view class="form-item">
      <input class="password" :value="password" password @input="bindPasswordInput" placeholder="密码"></input>
      <image class="clear" id="clear-password" v-if="password.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item">
      <input class="password" :value="confirmPassword" password @input="bindConfirmPasswordInput" placeholder="确认密码"></input>
      <image class="clear" id="clear-confirm-password" v-if="confirmPassword.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <button type="default" class="reset-btn" @tap="startReset">密码重置</button>

  </view>
</view>
</template>

<script>
var api = require("../../../config/api.js");
var check = require("../../../utils/check.js");
var app = getApp();

export default {
  data() {
    return {
      email: '',
      code: '',
      password: '',
      confirmPassword: ''
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {// 页面初始化 options为页面跳转所带来的参数
    // 页面渲染完成
  },
  onReady: function () {},
  onShow: function () {// 页面显示
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    sendCode: function () {
      let that = this;
      uni.request({
        url: api.AuthRegisterCaptcha,
        data: {
          email: that.email
        },
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        success: function (res) {
          if (res.data.errno == 0) {
            uni.showModal({
              title: '发送成功',
              content: '验证码已发送',
              showCancel: false
            });
          } else {
            uni.showModal({
              title: '错误信息',
              content: res.data.errmsg,
              showCancel: false
            });
          }
        }
      });
    },
    startReset: function () {
      var that = this;

      if (this.email.length == 0 || this.code.length == 0) {
        uni.showModal({
          title: '错误信息',
          content: '手机号和验证码不能为空',
          showCancel: false
        });
        return false;
      }

      if (!check.isValidEmail(this.email)) {
        uni.showModal({
          title: '错误信息',
          content: '手机号输入不正确',
          showCancel: false
        });
        return false;
      }

      if (this.password.length < 3) {
        uni.showModal({
          title: '错误信息',
          content: '用户名和密码不得少于3位',
          showCancel: false
        });
        return false;
      }

      if (this.password != this.confirmPassword) {
        uni.showModal({
          title: '错误信息',
          content: '确认密码不一致',
          showCancel: false
        });
        return false;
      }

      uni.request({
        url: api.AuthReset,
        data: {
          email: that.email,
          code: that.code,
          password: that.password
        },
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        success: function (res) {
          uni.reLaunch ({
              url: "/pages/ucenter/index/index"
            });
          if (res.data.errno == 0) {

          } else {
            uni.showModal({
              title: '密码重置失败',
              content: res.data.errmsg,
              showCancel: false
            });
          }
        }
      });
    },
    bindPasswordInput: function (e) {
      this.setData({
        password: e.detail.value
      });
    },
    bindConfirmPasswordInput: function (e) {
      this.setData({
        confirmPassword: e.detail.value
      });
    },
    bindEmailInput: function (e) {
      this.setData({
        email: e.detail.value
      });
    },
    bindCodeInput: function (e) {
      this.setData({
        code: e.detail.value
      });
    },
    clearInput: function (e) {
      switch (e.currentTarget.id) {
        case 'clear-password':
          this.setData({
            password: ''
          });
          break;

        case 'clear-confirm-password':
          this.setData({
            confirmPassword: ''
          });
          break;

        case 'clear-email':
          this.setData({
            email: ''
          });
          break;

        case 'clear-code':
          this.setData({
            code: ''
          });
          break;
      }
    }
  }
};
</script>
<style>
.form-box {
  width: 100%;
  height: auto;
  overflow: hidden;
  padding: 0 40rpx;
  margin-top: 96rpx;
  background: #fff;
}

.form-item {
  position: relative;
  background: #fff;
  height: 96rpx;
  border-bottom: 1px solid #d9d9d9;
}

.form-item .email, .form-item .password, .form-item .code {
  position: absolute;
  top: 26rpx;
  left: 0;
  display: block;
  width: 100%;
  height: 44rpx;
  background: #fff;
  color: #333;
  font-size: 30rpx;
}

.form-item-code {
  margin-top: 32rpx;
  height: auto;
  overflow: hidden;
  width: 100%;
}

.form-item-code .form-item {
  float: left;
  width: 350rpx;
}

.form-item-code .code-btn {
  float: right;
  padding: 20rpx 40rpx;
  border: 1px solid #d9d9d9;
  border-radius: 10rpx;
}

.form-item .clear {
  position: absolute;
  top: 26rpx;
  right: 18rpx;
  z-index: 2;
  display: block;
  background: #fff;
  height: 44rpx;
  width: 44rpx;
}

.reset-btn {
  margin: 60rpx 0 40rpx 0;
  height: 96rpx;
  line-height: 96rpx;
  color: #fff;
  font-size: 30rpx;
  width: 100%;
  background: #b4282d;
  border-radius: 6rpx;
}

</style>