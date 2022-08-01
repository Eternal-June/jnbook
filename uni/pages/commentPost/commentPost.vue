<template>
  <view class="container">
    <view class="post-comment">
      <view class="books">
        <view class="img">
          <image :src="orderBooks.picUrl"></image>
        </view>
        <view class="info">
          <view class="t">
            <text class="name">{{ orderBooks.booksName }} x{{ orderBooks.number }}</text>
          </view>
          <view class="attr">{{ orderBooks.booksSpecificationValues }}</view>
        </view>
      </view>
      <view class="rater">
        <text class="rater-title">评分</text>
        <block v-for="(item, index) in stars" :key="index">
          <image
            src="/static/static/images/icon_star_checked.png"
            @tap="selectRater"
            :data-star="item"
            v-if="item <= star"
          ></image>
          <image
            src="/static/static/images/icon_star.png"
            @tap="addRater"
            :data-star="item"
            v-else
          ></image>
        </block>
        <text class="rater-desc">{{ starText }}</text>
      </view>
      <view class="input-box">
        <textarea
          class="content"
          focus="true"
          @input="bindInputValue"
          maxlength="140"
          placeholder="留言经过筛选后，对所有人可见"
        ></textarea>
        <text class="count">{{ 140 - content.length }}</text>
      </view>

      <view class="weui-uploader">
        <view class="weui-uploader__hd">
          <view class="weui-uploader__title">图片上传</view>
          <view class="weui-uploader__info">{{ picUrls.length }}/{{ files.length }}</view>
        </view>
        <view class="weui-uploader__bd">
          <view class="weui-uploader__files" id="uploaderFiles">
            <block v-for="(item, index) in files" :key="index">
              <view class="weui-uploader__file" @tap="previewImage" :id="item">
                <image class="weui-uploader__img" :src="item" mode="aspectFill"></image>
              </view>
            </block>
            <view class="weui-uploader__input-box">
              <view class="weui-uploader__input" @tap="chooseImage"></view>
            </view>
          </view>
        </view>
      </view>

      <view class="btns">
        <view class="close" @tap="onClose">取消</view>
        <view class="post" @tap="onPost">发表</view>
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
      orderId: 0,
      type: 0,
      valueId: 0,
      orderBooks: {},
      content: "",
      stars: [1, 2, 3, 4, 5],
      star: 5,
      starText: "十分满意",
      hasPicture: false,
      picUrls: [],
      files: [],
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    var that = this;
    that.setData({
      orderId: options.orderId,
      type: options.type,
      valueId: options.valueId,
    });
    this.getOrderBooks();
  },
  onReady: function () {},
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },
  methods: {
    chooseImage: function (e) {
      if (this.files.length >= 5) {
        util.showErrorToast("只能上传五张图片");
        return false;
      }

      var that = this;
      uni.chooseImage({
        count: 1,
        sizeType: ["original", "compressed"],
        sourceType: ["album", "camera"],
        success: function (res) {
          that.setData({
            files: that.files.concat(res.tempFilePaths),
          });
          that.upload(res);
        },
      });
    },
    upload: function (res) {
      var that = this;
      const uploadTask = uni.uploadFile({
        url: api.StorageUpload,
        filePath: res.tempFilePaths[0],
        name: "file",
        success: function (res) {
          var _res = JSON.parse(res.data);

          if (_res.errno === 0) {
            var url = _res.data.url;
            that.picUrls.push(url);
            that.setData({
              hasPicture: true,
              picUrls: that.picUrls,
            });
          }
        },
        fail: function (e) {
          uni.showModal({
            title: "错误",
            content: "上传失败",
            showCancel: false,
          });
        },
      });
      uploadTask.onProgressUpdate((res) => {
        console.log("上传进度", res.progress);
        console.log("已经上传的数据长度", res.totalBytesSent);
        console.log("预期需要上传的数据总长度", res.totalBytesExpectedToSend);
      });
    },
    previewImage: function (e) {
      uni.previewImage({
        current: e.currentTarget.id,
        urls: this.files,
      });
    },
    selectRater: function (e) {
      var star;
      var starText;
      star = e.currentTarget.dataset.star;
      if (e.currentTarget.dataset.star == 0) {
        star = e.currentTarget.dataset.star;
      }
      if (star == 1) {
        starText = "很差";
      } else if (star == 2) {
        starText = "不太满意";
      } else if (star == 3) {
        starText = "满意";
      } else if (star == 4) {
        starText = "比较满意";
      } else if (star == 5) {
        starText = "十分满意";
      }

      this.setData({
        star: star,
        starText: starText,
      });
    },

    addRater: function (e) {
      var star;
      var starText;
      star = e.currentTarget.dataset.star;
      if (e.currentTarget.dataset.star == 6) {
        star = e.currentTarget.dataset.star - 1;
      }
      if (star == 1) {
        starText = "很差";
      } else if (star == 2) {
        starText = "不太满意";
      } else if (star == 3) {
        starText = "满意";
      } else if (star == 4) {
        starText = "比较满意";
      } else if (star == 5) {
        starText = "十分满意";
      }

      this.setData({
        star: star,
        starText: starText,
      });
    },
    getOrderBooks: function () {
      let that = this;
      util
        .request(api.OrderBooks, {
          orderId: that.orderId,
          booksId: that.valueId,
        })
        .then(function (res) {
          if (res.errno === 0) {
            that.setData({
              orderBooks: res.data,
            });
          }
        });
    },
    onClose: function () {
      uni.navigateBack();
    },
    onPost: function () {
      let that = this;

      if (!this.content) {
        util.showErrorToast("请填写评论");
        return false;
      }

      util
        .request(
          api.OrderComment,
          {
            orderBooksId: that.orderBooks.id,
            content: that.content,
            star: that.star,
            hasPicture: that.hasPicture,
            picUrls: that.picUrls,
          },
          "POST"
        )
        .then(function (res) {
          if (res.errno === 0) {
            uni.showToast({
              title: "评论成功",
              complete: function () {
                uni.switchTab({
                  url: "/pages/ucenter/index/index",
                });
              },
            });
          }
        });
    },

    bindInputValue(event) {
      let value = event.detail.value; //判断是否超过140个字符

      if (value && value.length > 140) {
        return false;
      }

      this.setData({
        content: event.detail.value,
      });
    },
  },
};
</script>
<style>
page,
.container {
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

.weui-uploader__input-box:after,
.weui-uploader__input-box:before {
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

.weui-uploader__input-box:active:after,
.weui-uploader__input-box:active:before {
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
</style>
