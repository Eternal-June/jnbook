<template>
<view class="container">

  <picker @change="bindPickerChange" :value="index" :range="array">
    <view class="picker">
      <view class="fb-type">
        <view class="type-label">{{array[index]}}</view>
        <image class="type-icon" src="/static/static/images/down.png"></image>
      </view>
    </view>
  </picker>
  <view class="fb-body">
    <textarea class="content" placeholder="对我们网站、图书、服务，你还有什么建议吗？你还希望在商城上买到什么？请告诉我们..." @input="contentInput" maxlength="500" auto-focus="true" :value="content"></textarea>
    <view class="weui-uploader__files" id="uploaderFiles">
      <block v-for="(item, index) in files" :key="index">
        <view class="weui-uploader__file" @tap="previewImage" :id="item">
          <image class="weui-uploader__img" :src="item" mode="aspectFill"></image>
        </view>
      </block>
      <view class="weui-uploader__input-box" v-if="files.length < 5">
        <view class="weui-uploader__input" @tap="chooseImage"></view>
      </view>
    </view>
    <view class="text-count">{{contentLength}}/500</view>
  </view>
  <view class="fb-mobile">
    <view class="label">手机号码</view>
    <view class="mobile-box">
      <input class="mobile" maxlength="11" type="number" placeholder="方便我们与你联系" @input="mobileInput" :value="mobile"></input>
      <image class="clear-icon" src="/static/static/images/clear_input.png" v-if="mobile.length > 0" @tap.stop="clearMobile"></image>
    </view>
  </view>

  <view class="fb-btn" @tap="submitFeedback">提交</view>
</view>
</template>

<script>
var util = require("../../../utils/util.js");
var check = require("../../../utils/check.js");
var api = require("../../../config/api.js");
var app = getApp();

export default {
  data() {
    return {
      array: ['请选择反馈类型', '图书相关', '功能异常', '优化建议', '其他'],
      index: 0,
      content: '',
      contentLength: 0,
      mobile: '',
      hasPicture: false,
      picUrls: [],
      files: []
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {},
  onReady: function () {},
  onShow: function () {},
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    chooseImage: function (e) {
      if (this.files.length >= 5) {
        util.showErrorToast('只能上传五张图片');
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
    bindPickerChange: function (e) {
      this.setData({
        index: e.detail.value
      });
    },
    mobileInput: function (e) {
      this.setData({
        mobile: e.detail.value
      });
    },
    contentInput: function (e) {
      this.setData({
        contentLength: e.detail.cursor,
        content: e.detail.value
      });
    },
    clearMobile: function (e) {
      this.setData({
        mobile: ''
      });
    },
    submitFeedback: function (e) {
      if (!app.globalData.hasLogin) {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      }

      let that = this;

      if (that.index == 0) {
        util.showErrorToast('请选择反馈类型');
        return false;
      }

      if (that.content == '') {
        util.showErrorToast('请输入反馈内容');
        return false;
      }

      if (that.mobile == '') {
        util.showErrorToast('请输入手机号码');
        return false;
      }

      if (!check.isValidPhone(this.mobile)) {
        this.setData({
          mobile: ''
        });
        util.showErrorToast('请输入手机号码');
        return false;
      }

      uni.showLoading({
        title: '提交中...',
        mask: true,
        success: function () {}
      });
      util.request(api.FeedbackAdd, {
        mobile: that.mobile,
        feedType: that.array[that.index],
        content: that.content,
        hasPicture: that.hasPicture,
        picUrls: that.picUrls
      }, 'POST').then(function (res) {
        uni.hideLoading();

        if (res.errno === 0) {
          uni.showToast({
            title: '感谢您的反馈！',
            icon: 'success',
            duration: 2000,
            complete: function () {
              that.setData({
                index: 0,
                content: '',
                contentLength: 0,
                mobile: '',
                hasPicture: false,
                picUrls: [],
                files: []
              });
            }
          });
        } else {
          util.showErrorToast(res.errmsg);
        }
      });
    }
  }
};
</script>
<style>
page {
  background: #f4f4f4;
  min-height: 100%;
}

.container {
  background: #f4f4f4;
  min-height: 100%;
  padding-top: 30rpx;
}

.fb-type {
  height: 104rpx;
  width: 100%;
  background: #fff;
  margin-bottom: 20rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 30rpx;
  padding-right: 30rpx;
}

.fb-type .type-label {
  height: 36rpx;
  flex: 1;
  color: #333;
  font-size: 28rpx;
}

.fb-type .type-icon {
  height: 36rpx;
  width: 36rpx;
}

.fb-body {
  width: 100%;
  background: #fff;
  height: 600rpx;
  padding: 18rpx 30rpx 64rpx 30rpx;
}

.fb-body .content {
  width: 100%;
  height: 400rpx;
  color: #333;
  line-height: 40rpx;
  font-size: 28rpx;
}

.weui-uploader__files {
  width: 100%;
}

.weui-uploader__file {
  float: left;
  margin-right: 9px;
  margin-bottom: 9px;
}

.weui-uploader__img {
  display: block;
  width: 50px;
  height: 50px;
}

.weui-uploader__input-box {

  float: left;
  position: relative;
  margin-right: 9px;
  margin-bottom: 9px;
  width: 50px;
  height: 50px;
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

.fb-body .text-count {
  line-height: 30rpx;
  float: right;
  color: #666;
  font-size: 24rpx;
}

.fb-mobile {
  height: 162rpx;
  width: 100%;
}

.fb-mobile .label {
  height: 58rpx;
  width: 100%;
  padding-top: 14rpx;
  padding-bottom: 11rpx;
  color: #7f7f7f;
  font-size: 24rpx;
  padding-left: 30rpx;
  padding-right: 30rpx;
  line-height: 33rpx;
}

.fb-mobile .mobile-box {
  height: 104rpx;
  width: 100%;
  color: #333;
  padding-left: 30rpx;
  padding-right: 30rpx;
  font-size: 24rpx;
  background: #fff;
  position: relative;
}

.fb-mobile .mobile {
  position: absolute;
  top: 27rpx;
  left: 30rpx;
  height: 50rpx;
  width: 100%;
  color: #333;
  line-height: 50rpx;
  font-size: 24rpx;
}

.fb-mobile .clear-icon {
  position: absolute;
  top: 27rpx;
  right: 30rpx;
  width: 48rpx;
  height: 48rpx;
  z-index: 2;
}

.fb-btn{background-color:#388ceb;font-size:32rpx;color:#fff;width:690rpx;height:90rpx;border-radius:50rpx;text-align:center;line-height:90rpx;margin:76rpx auto 0 auto;}
</style>
