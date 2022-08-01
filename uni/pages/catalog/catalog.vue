<template>
<view class="container">
  <view class="search">
    <navigator url="/pages/search/search" class="input jnbook_bg1">
      <image class="icon"></image>
      <text class="txt jnbook_f1">搜索, 共{{booksCount}}款图书</text>
    </navigator>
  </view>
  <view class="catalog">
    <scroll-view class="nav" scroll-y="true">
      <view v-for="(item, index) in categoryList" :key="index" :class="'item ' + ( currentCategory.id == item.id ? 'active' : '')" :data-id="item.id" :data-index="index" @tap="switchCate">{{item.name}}</view>
    </scroll-view>
    <scroll-view class="cate" scroll-y="true">
      <navigator url="url" class="banner">
        <image class="image" :src="currentCategory.picUrl"></image>
        <view class="txt">{{currentCategory.frontName}}</view>
      </navigator>
      <view class="hd">
        <text class="line"></text>
        <text class="txt">{{currentCategory.name}}分类</text>
        <text class="line"></text>
      </view>
      <view class="bd">
        <navigator v-for="(item, index) in currentSubCategoryList" :key="index" :url="'/pages/category/category?id=' + item.id" :class="'item ' + ((index+1) % 3 == 0 ? 'last' : '')">
          <image class="icon" :src="item.picUrl"></image>
          <text class="txt jnbook_f2">{{item.name}}</text>
        </navigator>
      </view>
    </scroll-view>
  </view>
</view>
</template>

<script>
var util = require("../../utils/util.js");
var api = require("../../config/api.js");

export default {
  data() {
    return {
      categoryList: [],
      currentCategory: {},
      currentSubCategoryList: {},
      scrollLeft: 0,
      scrollTop: 0,
      booksCount: 0,
      scrollHeight: 0
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    this.getCatalog();
  },

  onPullDownRefresh() {
    uni.showNavigationBarLoading(); //在标题栏中显示加载

    this.getCatalog();
    uni.hideNavigationBarLoading(); //完成停止加载

    uni.stopPullDownRefresh(); //停止下拉刷新
  },

  onReady: function () {// 页面渲染完成
  },
  onShow: function () {// 页面显示
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    getCatalog: function () {
      //CatalogList
      let that = this;
      uni.showLoading({
        title: '加载中...'
      });
      util.request(api.CatalogList).then(function (res) {
        that.setData({
          categoryList: res.data.categoryList,
          currentCategory: res.data.currentCategory,
          currentSubCategoryList: res.data.currentSubCategory
        });
        uni.hideLoading();
      });
      util.request(api.BooksCount).then(function (res) {
        that.setData({
          booksCount: res.data.booksCount
        });
      });
    },
    getCurrentCategory: function (id) {
      let that = this;
      util.request(api.CatalogCurrent, {
        id: id
      }).then(function (res) {
        that.setData({
          currentCategory: res.data.currentCategory,
          currentSubCategoryList: res.data.currentSubCategory
        });
      });
    },
    switchCate: function (event) {
      var that = this;
      var currentTarget = event.currentTarget;

      if (this.currentCategory.id == event.currentTarget.dataset.id) {
        return false;
      }

      this.getCurrentCategory(event.currentTarget.dataset.id);
    }
  }
};
</script>
<style>
page {
  height: 100%;
}

.container {
  background: #f9f9f9;
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
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
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAA8UExURUdwTGZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZmZjQWelcAAAATdFJOUwDUDlMW8DUFcdHpnc3cSLkosSd78uTdAAAAvUlEQVQoz4WS6QLDEBCE131L0nn/d23J0VCV+bP4LLsM0S6Tg1NQLmRDnYz1OOVti18OcFZLI7Utw/XGtEIS10wkKH3NVoWF3/byBerMNQ5be4vZ4I4Vi8S7+niC3bd55L54yvA1VSDSryJqheE4oZVFKMFBD6AGK0FBDiCHKgEwA/hZfsxkszun1U77nL7Q9G3nvzL6z1sH2ndOANi3eRmrhzjx6qGo2Z327pMNJSMO34paSkc7PdJI/2lc3naRCwMB9sl5AAAAAElFTkSuQmCC') center no-repeat;
  background-size: 100%;
  width: 34rpx;
  height: 34rpx;
}

.search .txt {
  height: 42rpx;
  line-height: 42rpx;
  padding-left: 10rpx;
}

.catalog {
  flex: 1;
  width: 100%;
  background: #fff;
  display: flex;
  border-top: 1px solid #fafafa;
}

.catalog .nav {
  width: 162rpx;
  height: 100%;
}

.catalog .nav .item {
  text-align: center;
  line-height: 90rpx;
  width: 162rpx;
  height: 90rpx;
  color: #333;
  font-size: 28rpx;
  border-left: 6rpx solid #fff;
}

.catalog .nav .item.active {
  color: #388ceb;
  font-size: 29rpx;
  border-left: 6rpx solid #388ceb;
}

.catalog .cate {
  border-left: 1px solid #fafafa;
  flex: 1;
  height: 100%;
  padding: 0 30rpx 0 30rpx;
}

.banner {
  display: block;
  height: 222rpx;
  width: 100%;
  position: relative;
}

.banner .image {
  position: absolute;
  top: 30rpx;
  left: 0;
  border-radius: 4rpx;
  height: 192rpx;
  width: 100%;
}

.banner .txt {
  position: absolute;
  top: 30rpx;
  text-align: center;
  color: #fff;
  font-size: 28rpx;
  left: 0;
  height: 192rpx;
  line-height: 192rpx;
  width: 100%;
}

.catalog .hd {
  height: 108rpx;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.catalog .hd .txt {
  font-size: 24rpx;
  text-align: center;
  color: #333;
  padding: 0 10rpx;
  width: auto;
}

.catalog .hd .line {
  width: 100rpx;
  height: 1px;
  background: #d9d9d9;
}

.catalog .bd {
  height: auto;
  width: 100%;
  overflow: hidden;
}

.catalog .bd .item {
  display: block;
  float: left;
  height: 200rpx;
  width: 152rpx;
  margin-right: 28rpx;
}

.catalog .bd .item.last {
  margin-right: 0;
}

.catalog .bd .item .icon {
  height: 152rpx;
  width: 152rpx;
}

.catalog .bd .item .txt {
  display: block;
  text-align: center;
  font-size: 24rpx;
  color: #333;
  height: 62rpx;
  width: 152rpx;
}

</style>
