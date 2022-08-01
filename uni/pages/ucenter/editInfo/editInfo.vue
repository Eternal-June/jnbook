<template>
<view class="container">
  <view class="form-box">
    <view>
      <view class="weui-uploader">
        <view class="weui-uploader__hd">
          <view class="weui-uploader__title">修改头像</view>
          <view class="weui-uploader__info">{{picUrls.length}}/{{files.length}}</view>
        </view>
        <view class="weui-uploader__bd">
          <view class="weui-uploader__files" id="uploaderFiles">
              <block v-for="(item, index) in files" :key="index">
                <view class="weui-uploader__file" v-if="index==0" @tap="previewImage" :id="item">
                  <image class="weui-uploader__img" :src="item" mode="aspectFill"></image>
                </view>
              </block>
            <view class="weui-uploader__input-box">
              <view class="weui-uploader__input" @tap="chooseImage"></view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="form-item">
      <input class="nickname" :value="nickname" @input="bindNicknameInput" placeholder="昵称" auto-focus></input>
      <image v-if="nickname.length > 0" id="clear-nickname" class="clear" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item">
      <input class="nickname" :value="compusname" @input="bindCompusnameInput" placeholder="所在校区" auto-focus></input>
      <image v-if="compusname.length > 0" id="clear-compusname" class="clear" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item">
      <input class="password" :value="password" password @input="bindPasswordInput" placeholder="密码"></input>
      <image class="clear" id="clear-password" v-if="password.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item">
      <input class="password" :value="confirmPassword" password @input="bindConfirmPasswordInput" placeholder="确认密码"></input>
      <image class="clear" id="clear-confirm-password" v-if="confirmPassword.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <view class="form-item">
      <input class="mobile" :value="email" @input="bindEmailInput" placeholder="更改邮箱"></input>
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
      <input class="mobile" :value="mobile" @input="bindMobileInput" placeholder="手机号"></input>
     <image class="clear" id="clear-mobile" v-if="mobile.length > 0" src="/static/static/images/clear_input.png" @tap.stop="clearInput"></image>
    </view>

    <button type="primary" class="Edit-btn" @tap="startEdit">确定</button>

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
      nickname: '',
      compusname: '',
      picUrls: [],
      password: '',
      userInfo: {},
      confirmPassword: '',
      mobile: '',
      email: '',
      code: '',
      files: []
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {// 页面初始化 options为页面跳转所带来的参数
    // 页面渲染完成
  },
  onReady: function () {},
  onShow: function () {
    let that = this; //获取用户的登录信息
    let userInfo = uni.getStorageSync('userInfo');
    this.setData({
      userInfo: userInfo,
      hasLogin: true
    });
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
      chooseImage: function (e) {
      if (this.files.length > 1) {
        util.showErrorToast('只能上传一张图片');
        return false;
      }

      var that = this;
      uni.chooseImage({
        count: 1,
        sizeType: ['original', 'compressed'],
        sourceType: ['album', 'camera'],
        success: function (res) {
          that.setData({
            files: that.files.concat(res.tempFilePaths)
          });
          that.upload(res);
        }
      });
    },
    upload: function (res) {
      var that = this;
      const uploadTask = uni.uploadFile({
        url: api.StorageUpload,
        filePath: res.tempFilePaths[0],
        name: 'file',
        success: function (res) {
          var _res = JSON.parse(res.data);

          if (_res.errno === 0) {
            var url = _res.data.url;
            that.picUrls.push(url);
            that.setData({
              hasPicture: true,
              picUrls: that.picUrls
            });
          }
        },
        fail: function (e) {
          uni.showModal({
            title: '错误',
            content: '上传失败',
            showCancel: false
          });
        }
      });
      uploadTask.onProgressUpdate(res => {
        console.log('上传进度', res.progress);
        console.log('已经上传的数据长度', res.totalBytesSent);
        console.log('预期需要上传的数据总长度', res.totalBytesExpectedToSend);
      });
    },
    previewImage: function (e) {
      uni.previewImage({
        current: e.currentTarget.id,
        // 当前显示图片的http链接
        urls: this.files // 需要预览的图片http链接列表

      });
    },

    sendCode: function () {
      let that = this;

      if (this.email.length == 0) {
        uni.showModal({
          title: '错误信息',
          content: '邮箱能为空',
          showCancel: false
        });
        return false;
      }

      if (!check.isValidEmail(this.email) && this.email.length != 0) {
        uni.showModal({
          title: '错误信息',
          content: '邮箱输入不正确',
          showCancel: false
        });
        return false;
      }

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


    requestEdit: function (wxCode) {
      let that = this;
      uni.request({
        url: api.UserEditInfo,
        data: {
          nickname: that.nickname,
          password: that.password,
          avatar: that.avatar,
          mobile: that.mobile,
          email: that.email,
          compusname: that.compusname,
          code: that.code,
          username: that.userInfo.userName,
          picUrls: that.picUrls,
          wxCode: wxCode
        },
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        success: function (res) {
          if (res.data.errno == 0) {
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
            uni.showModal({
              title: '错误信息',
              content: res.data.errmsg,
              showCancel: false
            });
          }
        }
      });
    },

    requestEditOther: function () {
      let that = this;
      uni.request({
        url: api.UserEditInfo,
        data: {
          compusname: that.compusname,
          nickname: that.nickname,
          avatar: that.avatar,
          password: that.password,
          mobile: that.mobile,
          email: that.email,
          picUrls: that.picUrls,
          username: that.userInfo.userName,
          code: that.code,
          wxCode: '-1'
        },
        method: 'POST',
        header: {
          'content-type': 'application/json'
        },
        success: function (res) {
          if (res.data.errno == 0) {
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
            uni.showModal({
              title: '错误信息',
              content: res.data.errmsg,
              showCancel: false
            });
          }
        }
      });
    },


    startEdit: function () {
      var that = this;

      if (this.password.length < 6 && this.password.length != 0) {
        uni.showModal({
          title: '错误信息',
          content: '密码不得少于6位',
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

      if (!check.isValidEmail(this.email) && this.email.length != 0) {
        uni.showModal({
          title: '错误信息',
          content: '邮箱输入不正确',
          showCancel: false
        });
        return false;
      }

      if (this.mobile.length != 0 && !check.isValidPhone(this.mobile)) {
        uni.showModal({
          title: '错误信息',
          content: '手机号输入不正确',
          showCancel: false
        });
        return false;
      }

// #ifndef MP-WEIXIN
      that.requestEditOther();
// #endif


// #ifdef MP-WEIXIN
      uni.login({
        success: function (res) {
          if (!res.code) {
            uni.showModal({
              title: '错误信息',
              content: '修改失败',
              showCancel: false
            });
          }
          that.requestEdit(res.code);
        }
      });
// #endif
    },
    bindNicknameInput: function (e) {
      this.setData({
        nickname: e.detail.value
      });
    },
    bindCompusnameInput: function (e) {
      this.setData({
        compusname: e.detail.value
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
    bindMobileInput: function (e) {
      this.setData({
        mobile: e.detail.value
      });
    },
    bindCodeInput: function (e) {
      this.setData({
        code: e.detail.value
      });
    },
    clearInput: function (e) {
      switch (e.currentTarget.id) {
        case 'clear-nickname':
          this.setData({
            nickname: ''
          });
          break;

        case 'clear-compusname':
          this.setData({
            compusname: ''
          });
          break;

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

        case 'clear-mobile':
          this.setData({
            mobile: ''
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

.page, .container {
  height: 100%;
  background: #f4f4f4;
}

.post-comment {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 30rpx;
  background: #fff;
}

.post-comment .books {
  display: flex;
  align-items: center;
  height: 199rpx;
  margin-left: 31.25rpx;
}

.post-comment .books .img {
  height: 145.83rpx;
  width: 145.83rpx;
  background: #f4f4f4;
}

.post-comment .books .img image {
  height: 145.83rpx;
  width: 145.83rpx;
}

.post-comment .books .info {
  height: 145.83rpx;
  flex: 1;
  padding-left: 20rpx;
}

.post-comment .books .name {
  margin-top: 30rpx;
  display: block;
  height: 44rpx;
  line-height: 44rpx;
  color: #333;
  font-size: 30rpx;
}

.post-comment .books .number {
  display: block;
  height: 37rpx;
  line-height: 37rpx;
  color: #666;
  font-size: 25rpx;
}

.post-comment .books .status {
  width: 105rpx;
  color: #b4282d;
  font-size: 25rpx;
}

.post-comment .rater {
  display: flex;
  flex-direction: row;
  height: 55rpx;
}

.post-comment .rater .rater-title {
  font-size: 29rpx;
  padding-right: 10rpx;
}

.post-comment .rater image {
  padding-left: 5rpx;
  height: 50rpx;
  width: 50rpx;
}

.post-comment .rater .rater-desc {
  font-size: 29rpx;
  padding-left: 10rpx;
}

.post-comment .input-box {
  height: 337.5rpx;
  width: 690rpx;
  position: relative;
  background: #fff;
}

.post-comment .input-box .content {
  position: absolute;
  top: 0;
  left: 0;
  display: block;
  background: #fff;
  font-size: 29rpx;
  border: 5px solid #f4f4f4;
  height: 300rpx;
  width: 650rpx;
  padding: 20rpx;
}

.post-comment .input-box .count {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  display: block;
  height: 30rpx;
  width: 50rpx;
  font-size: 29rpx;
  color: #999;
}

.post-comment .btns {
  height: 108rpx;
}

.post-comment .close {
  float: left;
  height: 108rpx;
  line-height: 108rpx;
  text-align: left;
  color: #666;
  padding: 0 30rpx;
}

.post-comment .post {
  float: right;
  height: 108rpx;
  line-height: 108rpx;
  text-align: right;
  padding: 0 30rpx;
}

.weui-uploader {
  margin-top: 50rpx;
}

.weui-uploader__hd {
  display: -webkit-box;
  display: -webkit-flex;
  display: flex;
  padding-bottom: 10px;
  -webkit-box-align: center;
  -webkit-align-items: center;
  align-items: center;
}

.weui-uploader__title {
  -webkit-box-flex: 1;
  -webkit-flex: 1;
  flex: 1;
}

.weui-uploader__info {
  color: #b2b2b2;
}

.weui-uploader__bd {
  margin-bottom: -4px;
  margin-right: -9px;
  overflow: hidden;
}

.weui-uploader__file {
  float: left;
  margin-right: 9px;
  margin-bottom: 9px;
}

.weui-uploader__img {
  display: block;
  width: 79px;
  height: 79px;
}

.weui-uploader__file_status {
  position: relative;
}

.weui-uploader__file_status:before {
  content: " ";
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
}

.weui-uploader__file-content {
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  color: #fff;
}

.weui-uploader__input-box {
  float: left;
  position: relative;
  margin-right: 9px;
  margin-bottom: 9px;
  width: 77px;
  height: 77px;
  border: 1px solid #d9d9d9;
}

.weui-uploader__input-box:after, .weui-uploader__input-box:before {
  content: " ";
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
  background-color: #d9d9d9;
}

.weui-uploader__input-box:before {
  width: 2px;
  height: 39.5px;
}

.weui-uploader__input-box:after {
  width: 39.5px;
  height: 2px;
}

.weui-uploader__input-box:active {
  border-color: #999;
}

.weui-uploader__input-box:active:after, .weui-uploader__input-box:active:before {
  background-color: #999;
}

.weui-uploader__input {
  position: absolute;
  z-index: 1;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
}

.form-box {
  width: 100%;
  height: auto;
  overflow: hidden;
  padding: 0 40rpx;
  margin-top: 96rpx;

}

.form-item {
  position: relative;

  height: 96rpx;
  border-bottom: 2px solid #d9d9d9;
}

.form-item .nickname, .form-item .password, .form-item .code {
  position: absolute;
  top: 26rpx;
  left: 0;
  display: block;
  width: 100%;
  height: 44rpx;

  color: #333;
  font-size: 30rpx;
}

.form-item .mobile{
  position: absolute;
  top: 32rpx;
  left: 0;
  display: block;
  width: 100%;
  height: 52rpx;

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
  color: #fff;
  background: green;
}

.form-item .clear {
  position: absolute;
  top: 26rpx;
  right: 18rpx;
  z-index: 2;
  display: block;

  height: 44rpx;
  width: 44rpx;
}


.Edit-btn{background-color:#388ceb;font-size:32rpx;color:#fff;width:690rpx;height:90rpx;border-radius:50rpx;text-align:center;line-height:90rpx;margin:76rpx auto 0 auto;}
</style>