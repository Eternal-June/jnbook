<template>
  <view>
    <view class="newsDetail">
      <view class="title">{{ title }}</view>
      <view class="list acea-row row-middle">
        <view v-if="type == 1" class="label cart-color">公告</view>
        <view v-else class="label cart-color">新闻</view>
        <view class="item"
          ><text class="iconfont icon-shenhezhong"></text>{{ addTime }}</view
        >
        <view class="item"><text class="iconfont icon-liulan"></text>10000+</view>
      </view>
      <view class="conter">
        <jyf-parser :html="article_content"></jyf-parser>
      </view>
    </view>
    <home></home>
  </view>
</template>

<script>
var app = getApp();
var util = require("../../utils/util.js");
var api = require("../../config/api.js");

import home from "../../lib/home/index";

export default {
  data() {
    return {
      title: "公告详情",
      content: "",
      id: 0,
      addTime: "2021-01-01",
      type: 1,
      article_content: "",
    };
  },

  components: {
    home,
  },
  props: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (options.hasOwnProperty("id")) {
      this.setData({
        id: options.id,
      });
    } else {
      uni.navigateBack({
        delta: 1,
      });
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {},

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.getArticleOne();
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
  methods: {
    getArticleOne: function () {
      var that = this;
      util
        .request(api.ArticleDetail, {
          id: that.id,
        })
        .then(function (res) {
          if (res.errno === 0) {
            that.setData({
              title: res.data.title,
              content: res.data.content,
              addTime: res.data.addTime,
              type: res.data.type,
            }); //html转wxml
            setTimeout(() => {
              that.article_content = res.data.content;
            }, 200);
          }
        });
    },
  },
};
</script>
<style>
page {
  background-color: #fff !important;
}
.newsDetail .title {
  padding: 0 30rpx;
  font-size: 34rpx;
  color: #282828;
  font-weight: bold;
  margin: 45rpx 0 23rpx 0;
  line-height: 1.5;
}
.newsDetail .list {
  margin: 0 30rpx;
  border-bottom: 1rpx solid #eee;
  padding-bottom: 25rpx;
}
.newsDetail .list .label {
  font-size: 24rpx;
  height: 38rpx;
  border-radius: 3rpx;
  text-align: center;
  line-height: 38rpx;
  padding: 0 10rpx;
}
.newsDetail .list .item {
  margin-left: 27rpx;
  font-size: 24rpx;
  color: #999;
}
.newsDetail .list .item .iconfont {
  font-size: 28rpx;
  margin-right: 10rpx;
}
.newsDetail .list .item .iconfont.icon-shenhezhong {
  font-size: 26rpx;
}
.newsDetail .conter {
  padding: 0 15rpx;
  font-size: 30rpx;
  color: #333;
  line-height: 1.7;
  padding-top: 35rpx;
}
</style>

