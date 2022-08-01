<template>
  <view>
    <!--index.wxml-->
    <view class="container">
      <view class="search">
        <navigator url="/pages/schoolSearch/schoolSearch" class="input jnbook_bg1">
          <image class="icon"></image>
          <text class="txt jnbook_f1">共{{ booksCount }}本书籍供您选择...</text>
        </navigator>
      </view>

      <view class="a-section a-popular" v-if="hotbooks.length > 0">
        <view class="hot-bg">
          <view class="title acea-row row-between-wrapper">
            <view class="text">
              <text class="label">同校推荐</text>好书近在眼前
            </view>
            <navigator
              class="more jnbook_f6"
              hover-class="none"
              url="../schoolBooks/schoolBooks"
              >更多<text class="iconfont icon-gengduo"></text
            ></navigator>
          </view>
        </view>

        <view class="b">
          <view v-for="(item, index) in hotbooks" :key="index" class="item">
            <navigator :url="'/pages/books/books?id=' + item.id">
              <image class="img" :src="item.picUrl" background-size="cover"></image>
              <view class="right">
                <view class="text">
                  <text class="name jnbook_f7">{{ item.name }}</text>
                  <text class="desc jnbook_f5">{{ item.brief }}</text>
                  <text class="price jnbook_f8">￥{{ item.retailPrice }}</text>
                </view>
              </view>
            </navigator>
          </view>
        </view>
      </view>

    </view>
  </view>
</template>

<script>
const util = require("../../utils/util.js");
const api = require("../../config/api.js");
const user = require("../../utils/user.js"); //获取应用实例
//获取应用实例
const app = getApp();
import zanCapsule from "../../lib/zanui-weapp/capsule/index";
import home from "../../lib/home/index";

export default {
  data() {
    return {
      hotbooks: [],
      booksCount: 0,
      indicatorDots: false,
      window: false,
    };
  },

  beforeCreate() {
    util.request(api.SchoolBooksCount).then(function (res) {
      that.setData({
        booksCount: res.data.schoolBooksCount,
      });
    });
    this.getIndexData();
  },

  components: {
    zanCapsule,
    home,
  },
  props: {},

  onShareAppMessage: function () {
    let userInfo = uni.getStorageSync("userInfo");
    let shareUserId = 1;

    if (userInfo) {
      shareUserId = userInfo.userId;
    }

    console.log("/pages/index/index?shareUserId=" + shareUserId);
    return {
      title: "江南E书",
      desc: "江南E书科技与您共约",
      path: "/pages/index/index?shareUserId=" + shareUserId,
    };
  },

  onPullDownRefresh() {
    uni.showNavigationBarLoading(); //在标题栏中显示加载

    this.getIndexData();
    uni.hideNavigationBarLoading(); //完成停止加载
    this.getIndexData();
    uni.stopPullDownRefresh(); //停止下拉刷新
    this.getIndexData();
  },

  onLoad: function (options) {

    if (options.scene) {
      var scene = decodeURIComponent(options.scene);
      console.log("scene:" + scene);
      let info_arr = [];
      info_arr = scene.split(",");
      let _type = info_arr[0];
      let id = info_arr[1];

      if (_type == "books") {
        uni.navigateTo({
          url: "../books/books?id=" + id,
        });
      } else if (_type == "topic") {
        uni.navigateTo({
          url: "../topicDetail/topicDetail?id=" + id,
        });
      } else {
        if (id != null) {
          uni.setStorageSync("shareUserId", id);
        }

        uni.navigateTo({
          url: "../index/index",
        });
      }
    } // 页面初始化 options为页面跳转所带来的参数

    if (options.bookId) {
      //这个bookId的值存在则证明首页的开启来源于分享,同时可以通过获取到的bookId的值跳转导航到对应的详情页
      uni.navigateTo({
        url: "../books/books?id=" + options.bookId,
      });
    } // 页面初始化 options为页面跳转所带来的参数

    if (options.orderId) {
      //这个orderId的值存在则证明首页的开启来源于订单模版通知,同时可以通过获取到的pageId的值跳转导航到对应的详情页
      uni.navigateTo({
        url: "../ucenter/orderDetail/orderDetail?id=" + options.orderId,
      });
    }

    this.getIndexData();
  },
  onReady: function () {
    // 页面渲染完成
    let that = this;
    let userInfo = uni.getStorageSync("userInfo");
    this.getIndexData();

    if (userInfo) {
      that.setData({
        window: true,
      });
    }
  },
  onShow: function () {
    // 每次页面显示，需获取是否用户登录，如果用户登录，则查询用户是否有优惠券，有则弹出优惠券领取窗口
    let that = this;
    let userInfo = uni.getStorageSync("userInfo");
    this.getIndexData();
  },
  onHide: function () {
      this.getIndexData();
    // 页面隐藏
  },
  onUnload: function () {
      this.getIndexData();
    // 页面关闭
  },
  methods: {
    getIndexData: function () {
      let that = this;
      util.request(api.SchoolUrl).then(function (res) {
        console.log(res.data);
        if (res.errno === 0) {
          that.setData({
            hotbooks: res.data.hotbooksList,
          });
        }
      });
      util.request(api.SchoolBooksCount).then(function (res) {
        that.setData({
          booksCount: res.data.schoolBooksCount,
        });
      });
    },
    onColse: function () {
      this.setData({
        window: false,
      });
    },
  },
};
</script>
<style>
.banner {
  width: 750rpx;
  height: 312rpx;
}

.banner image {
  width: 100%;
  height: 312rpx;
}

.m-menu {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding-bottom: 0rpx;
  padding-top: 30rpx;
}

.m-menu .item {
  width: 150rpx;
  height: 136rpx;
}

.m-menu image {
  display: block;
  width: 64rpx;
  height: 64rpx;
  margin: 0 auto;
  margin-bottom: 16rpx;
}

.m-menu text {
  display: block;
  text-align: center;
  margin: 0 auto;
  line-height: 1;
}

.a-section {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  background: #fff;
  margin-top: 20rpx;
}

.a-section .h {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  justify-content: center;
  height: 130rpx;
}

.a-section .h .txt {
  padding-right: 30rpx;
  background-size: 16.656rpx 27rpx;
  display: inline-block;
  height: 36rpx;
  line-height: 36rpx;
}

.a-new .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 0 31rpx 45rpx 31rpx;
}

.a-new .b .item {
  float: left;
  width: 324rpx;
  background: #f9f9f9;
  margin: 11rpx;
}

.a-new .b .item-b {
  margin-left: 42rpx;
}

.a-new .b .img {
  width: 324rpx;
  height: 324rpx;
}

.a-new .b .name {
  text-align: center;
  display: block;
  width: 324rpx;
  height: 42rpx;
  padding: 10rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.a-new .b .price {
  display: block;
  text-align: center;
  line-height: 30rpx;
  padding-bottom: 25rpx;
}

.a-popular {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  background: #f4f4f4;
}

.a-popular .b .item {
  border-bottom: 1px solid #f4f4f4;
  margin: 0 10rpx;
  margin-bottom: -2rpx;
  height: 280rpx;
  width: 690rpx;
}

.a-popular .b .img {
  margin-top: 10rpx;
  margin-right: 15rpx;
  float: left;
  width: 230rpx;
  height: 230rpx;
}

.a-popular .b .right {
  float: left;
  height: 264rpx;
  width: 436rpx;
  display: flex;
  flex-flow: row nowrap;
}

.a-popular .b .text {
  display: flex;
  flex-wrap: nowrap;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
  height: 264rpx;
  width: 436rpx;
}

.a-popular .b .name {
  width: 436rpx;
  display: block;
  line-height: 40rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.a-popular .b .desc {
  margin-top: 18rpx;
  width: 436rpx;
  display: block;
  line-height: 38rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.a-popular .b .price {
  margin-top: 18rpx;
  width: 436rpx;
  display: block;
  line-height: 30rpx;
}

.a-topic .b {
  height: 533rpx;
  width: 750rpx;
  padding: 0 0 48rpx 0;
}

.a-topic .b .list {
  height: 533rpx;
  width: 750rpx;
  white-space: nowrap;
}

.a-topic .b .item {
  display: inline-block;
  height: 533rpx;
  width: 680.5rpx;
  margin-left: 30rpx;
  overflow: hidden;
}

.a-topic .b .item:last-child {
  margin-right: 30rpx;
}

.a-topic .b .img {
  height: 387.5rpx;
  width: 680.5rpx;
  margin-bottom: 30rpx;
}

.a-topic .b .np {
  height: 55rpx;
  text-align: center;
}

.a-topic .b .np .price {
  padding-left: 40rpx;
}

.a-topic .b .desc {
  display: block;
  height: 30rpx;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-grid {
  width: 750rpx;
  height: auto;
  overflow: hidden;
}

.book-grid .h {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  justify-content: center;
  height: 130rpx;
}

.book-grid .b {
  width: 750rpx;
  padding: 0 6.25rpx;
  height: auto;
  overflow: hidden;
}

.book-grid .b .item {
  float: left;
  background: #fff;
  width: 365rpx;
  margin-bottom: 6.25rpx;
  height: 452rpx;
  overflow: hidden;
  text-align: center;
}

.book-grid .b .item .a {
  height: 452rpx;
  width: 100%;
}

.book-grid .b .item-b {
  margin-left: 6.25rpx;
}

.book-grid .item .img {
  margin-top: 20rpx;
  width: 302rpx;
  height: 302rpx;
}

.book-grid .item .name {
  display: block;
  width: 365.625rpx;
  padding: 0 20rpx;
  overflow: hidden;
  height: 35rpx;
  margin: 11.5rpx 0 22rpx 0;
  text-align: center;
}

.book-grid .item .price {
  display: block;
  width: 365.625rpx;
  height: 30rpx;
  text-align: center;
}

.book-grid .t {
  height: 100rpx;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.search {
  height: 100rpx;
  width: 100%;
  padding: 0 30rpx;
  background: #fff;
  display: flex;
  align-items: center;
}

.search .input {
  width: 690rpx;
  height: 70rpx;
  border-radius: 20rpx;
  display: flex;
  padding-top: 18rpx;
  padding-left: 20rpx;
  align-items: left;
  vertical-align: middle;
  justify-content: left;
}

.search .icon {
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAA8UExURUdwTGZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZjQWelcAAAATdFJOUwDUDlMW8DUFcdHpnc3cSLkosSd78uTdAAAAvUlEQVQoz4WS6QLDEBCE131L0nn/d23J0VCV+bP4LLsM0S6Tg1NQLmRDnYz1OOVti18OcFZLI7Utw/XGtEIS10wkKH3NVoWF3/byBerMNQ5be4vZ4I4Vi8S7+niC3bd55L54yvA1VSDSryJqheE4oZVFKMFBD6AGK0FBDiCHKgEwA/hZfsxkszun1U77nL7Q9G3nvzL6z1sH2ndOANi3eRmrhzjx6qGo2Z327pMNJSMO34paSkc7PdJI/2lc3naRCwMB9sl5AAAAAElFTkSuQmCC")
    center no-repeat;
  background-size: 100%;
  width: 34rpx;
  height: 34rpx;
}

.search .txt {
  height: 42rpx;
  line-height: 42rpx;
  padding-left: 10rpx;
}

.container .news {
  height: 77rpx;
  border-bottom: 1rpx solid #eee;
  padding: 0 30rpx;
  box-shadow: 0 10rpx 30rpx #f5f5f5;
  background: #fff;
}
.container .news .ariticle {
  color: red;
  width: 150rpx;
  height: 35rpx;
  border-right: 6rpx solid #ddd;
  padding-right: 23rpx;
  box-sizing: content-box;
}
.container .news .ariticle image {
  width: 100%;
  height: 100%;
}
.container .news .swiperTxt {
  width: 510rpx;
  height: 100%;
  line-height: 77rpx;
  overflow: hidden;
}
.container .news .swiperTxt .text {
  width: 480rpx;
}
.container .news .swiperTxt .text .label {
  font-size: 20rpx;
  color: #ff4c48;
  width: 64rpx;
  height: 35rpx;
  border-radius: 40rpx;
  text-align: center;
  line-height: 35rpx;
  border: 2rpx solid #ff4947;
}
.container .news .swiperTxt .text .newsTitle {
  width: 397rpx;
}
.container .news .swiperTxt .iconfont {
  font-size: 30rpx;
  color: #888;
}
.container .news .swiperTxt swiper {
  height: 100%;
}

.container .a-section .title {
  height: 120rpx;
  border-top: 0rpx solid #eee;
  padding-top: 14rpx;
  margin: 0 30rpx;
}
.container .a-section .title .text {
  width: 530rpx;
}
.container .a-section .title .text .name {
  margin-bottom: 5rpx;
  position: relative;
}
.container .a-section .title .text .name .new {
  position: absolute;
  top: 6rpx;
  left: 210rpx;
  font-size: 20rpx;
  font-weight: bold;
}
.container .a-section .title .more .iconfont {
  margin-left: 9rpx;
  font-size: 26rpx;
  vertical-align: 3rpx;
}

.container .a-section .hot-bg {
  width: 100%;
  height: 215rpx;
  background-image: url("https://jnbook.oss-cn-shanghai.aliyuncs.com/new.jpg");
  background-repeat: no-repeat;
  background-size: 100% 100%;
  box-sizing: border-box;
  padding: 14rpx;
  margin-bottom: 0;
}
.container .a-section .hot-bg .title {
  height: 87rpx;
}
.container .a-section .hot-bg .title .text {
  width: 575rpx;
  font-size: 24rpx;
  color: #fff;
}
.container .a-section .hot-bg .title .text .label {
  font-size: 30rpx;
  font-weight: bold;
  margin-right: 20rpx;
  color: #fff;
}
.container .a-section .hot-bg .title .more {
  font-size: 26rpx;
  color: #fff;
}
.container .a-section .hot-bg .title .more .iconfont {
  font-size: 25rpx;
  vertical-align: 2rpx;
  margin-left: 10rpx;
  color: #fff;
}
.container .a-popular .b {
  width: 96%;
  height: auto;
  border-radius: 20rpx;
  background-color: #fff;
  margin: -100rpx auto 0 auto;
  box-sizing: border-box;
  box-shadow: 0 0 30rpx -10rpx #aaa;
}
</style>
