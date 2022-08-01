<template>
<view class="container">
    <view class="form-box">

    	<view class="form-item">
    		<input class="username" :value="username" @input="bindUsernameInput" placeholder="账号"></input>
    		<image v-if="username.length > 0" id="clear-username" class="clear" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    	</view>

    	<view class="form-item">
    		<input class="password" :value="password" password @input="bindPasswordInput" placeholder="密码"></input>
    		<image class="clear" id="clear-password" v-if="password.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    	</view>


    <button type="primary" class="login-btn" @tap="accountLogin">账号登录</button>

     <view class="form-item-text">
      <navigator url="/pages/auth/register/register" class="register">注册账号</navigator>
			<navigator url="/pages/auth/reset/reset" class="reset">忘记密码</navigator>
    </view>
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
      username: '',
      password: '',
      code: '',
      loginErrorCount: 0
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
    accountLogin: function () {
      var that = this;

      if (this.password.length < 1 || this.username.length < 1) {
        uni.showModal({
          title: '错误信息',
          content: '请输入用户名和密码',
          showCancel: false
        });
        return false;
      }

      uni.request({
        url: api.AuthLoginByAccount,
        data: {
          username: that.username,
          password: that.password
        },
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        success: function (res) {
          if (res.data.errno == 0) {
            that.setData({
              loginErrorCount: 0
            });
            app.globalData.hasLogin = true;
            uni.setStorageSync('userInfo', res.data.data.userInfo);
            uni.setStorage({
              key: "token",
              data: res.data.data.token,
              success: function () {
                uni.switchTab({
                  url: '/pages/ucenter/index/index'
                });
              }
            });
          } else {
            that.setData({
              loginErrorCount: that.loginErrorCount + 1
            });
            app.globalData.hasLogin = false;
            util.showErrorToast('账户登录失败');
          }
        }
      });
    },
    bindUsernameInput: function (e) {
      this.setData({
        username: e.detail.value
      });
    },
    bindPasswordInput: function (e) {
      this.setData({
        password: e.detail.value
      });
    },
    bindCodeInput: function (e) {
      this.setData({
        code: e.detail.value
      });
    },
    clearInput: function (e) {
      switch (e.currentTarget.id) {
        case 'clear-username':
          this.setData({
            username: ''
          });
          break;

        case 'clear-password':
          this.setData({
            password: ''
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
  margin-top: 200rpx;
  background: #f4f4f4;
}

.form-item {
  position: relative;
  background: #f4f4f4;
  height: 96rpx;
  border-bottom: 1px solid #d9d9d9;
}

.form-item .username, .form-item .password, .form-item .code {
  position: absolute;
  top: 26rpx;
  left: 0;
  display: block;
  width: 100%;
  height: 44rpx;
  background: #f4f4f4;
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

.form-item-code .code-img {
  float: right;
  margin-top: 4rpx;
  height: 88rpx;
  width: 236rpx;
}

.form-item .clear {
  position: absolute;
  top: 26rpx;
  right: 18rpx;
  z-index: 2;
  display: block;
  background: #f4f4f4;
  height: 44rpx;
  width: 44rpx;
}

.login-btn {
  margin: 60rpx 0 40rpx 0;
  height: 96rpx;
  line-height: 96rpx;
  font-size: 30rpx;
  border-radius: 6rpx;
  width: 90%;
  color: #f4f4f4;
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
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #388ceb 0%, #388ceb 100%);
}

.form-item-text {
  height: 35rpx;
  width: 100%;
}

.form-item-text .register {
  display: block;
  height: 34rpx;
  float: left;
  font-size: 28rpx;
}

.form-item-text .reset {
  display: block;
  height: 34rpx;
  float: right;
  font-size: 28rpx;
}

</style>