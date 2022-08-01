<template>
<view class="container">
  <view class="cate-nav">
    <scroll-view scroll-x="true" class="cate-nav-body" style="width: 750rpx;" :scroll-left="scrollLeft">
      <view v-for="(item, index) in navList" :key="index" :class="'item ' + ( id == item.id ? 'active' : '')" :data-id="item.id" :data-index="index" @tap="switchCate">
        <view class="name jnbook_f7">{{item.name}}</view>
      </view>
    </scroll-view>
  </view>
  <scroll-view scroll-y="true" :scroll-top="scrollTop" :style="'height:' + scrollHeight + ';'">

    <view class="cate-item">
      <view class="b">
        <navigator v-for="(iitem, iindex) in booksList" :key="iindex" :class="'item ' + ((iindex + 1) % 2 == 0 ? 'item-b' : '')" :url="'/pages/books/books?id=' + iitem.id">
          <image class="img" :src="iitem.picUrl" background-size="cover"></image>
          <text class="name jnbook_f2">{{iitem.name}}</text>
          <text class="price jnbook_f8">￥{{iitem.retailPrice}}</text>
        </navigator>
      </view>
    </view>
  </scroll-view>
</view>
</template>

<script>
var util = require("../../utils/util.js");
var api = require("../../config/api.js");

export default {
  data() {
    return {
      navList: [],
      booksList: [],
      id: 0,
      currentCategory: {},
      scrollLeft: 0,
      scrollTop: 0,
      scrollHeight: 0,
      page: 1,
      size: 10,
      totalPages: 1
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    var that = this;

    if (options.id) {
      that.setData({
        id: parseInt(options.id)
      });
    }

    uni.getSystemInfo({
      success: function (res) {
        that.setData({
          scrollHeight: res.windowHeight
        });
      }
    });
    this.getCategoryInfo();
  },
  onReady: function () {// 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    console.log(1);
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  onReachBottom: function () {
    if (this.totalPages > this.page) {
      this.setData({
        page: this.page + 1
      });
    } else {
      uni.showToast({
        title: '已经到底了!',
        icon: 'none',
        duration: 2000
      });
      return false;
    }

    this.getBooksList();
  },
  methods: {
    getCategoryInfo: function () {
      let that = this;
      util.request(api.BooksCategory, {
        id: this.id
      }).then(function (res) {
        if (res.errno == 0) {
          that.setData({
            navList: res.data.brotherCategory,
            currentCategory: res.data.currentCategory
          });
          uni.setNavigationBarTitle({
            title: res.data.parentCategory.name
          });

          if (res.data.parentCategory.id == that.id) {
            that.setData({
              id: res.data.currentCategory.id
            });
          } //nav位置


          let currentIndex = 0;
          let navListCount = that.navList.length;

          for (let i = 0; i < navListCount; i++) {
            currentIndex += 1;

            if (that.navList[i].id == that.id) {
              break;
            }
          }

          if (currentIndex > navListCount / 2 && navListCount > 5) {
            that.setData({
              scrollLeft: currentIndex * 60
            });
          }

          that.getBooksList();
        } else {//显示错误信息
        }
      });
    },
    getBooksList: function () {
      var that = this;
      util.request(api.BooksList, {
        categoryId: that.id,
        page: that.page,
        size: that.size
      }).then(function (res) {
        that.setData({
          booksList: that.booksList.concat(res.data.booksList),
          totalPages: res.data.totalPages
        });
      });
    },
    switchCate: function (event) {
      if (this.id == event.currentTarget.dataset.id) {
        return false;
      }

      var that = this;
      var clientX = event.detail.x;
      var currentTarget = event.currentTarget;

      if (clientX < 60) {
        that.setData({
          scrollLeft: currentTarget.offsetLeft - 60
        });
      } else if (clientX > 330) {
        that.setData({
          scrollLeft: currentTarget.offsetLeft
        });
      }

      this.setData({
        id: event.currentTarget.dataset.id,
        booksList: [],
        page: 1,
        totalPages: 1
      });
      this.getCategoryInfo();
    }
  }
};
</script>
<style>
.container {
  background: #f9f9f9;
}

.cate-nav {
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
}

.cate-nav-body {
  height: 84rpx;
  white-space: nowrap;
  background: #fff;
  border-top: 1px solid rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.cate-nav .item {
  display: inline-block;
  height: 84rpx;
  min-width: 130rpx;
  padding: 0 15rpx;
}

.cate-nav .item .name {
  display: block;
  height: 84rpx;
  padding: 0 20rpx;
  line-height: 84rpx;
  width: auto;
}

.cate-nav .item.active .name {
  color: #388ceb;
  border-bottom: 2px solid #388ceb;
}

.cate-item {
  margin-top: 94rpx;
  height: auto;
  overflow: hidden;
}

.cate-item .h {
  height: 145rpx;
  width: 750rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.cate-item .h .name {
  display: block;
  height: 35rpx;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  color: #333;
}

.cate-item .h .desc {
  display: block;
  height: 24rpx;
  font-size: 24rpx;
  color: #999;
}

.cate-item .b {
  width: 750rpx;
  padding: 0 6.25rpx;
  height: auto;
  overflow: hidden;
}

.cate-item .b .item {
  float: left;
  background: #fff;
  width: 365rpx;
  margin-bottom: 6.25rpx;
  padding-bottom: 33.333rpx;
  height: auto;
  overflow: hidden;
  text-align: center;
}

.cate-item .b .item-b {
  margin-left: 6.25rpx;
}

.cate-item .item .img {
  width: 302rpx;
  height: 302rpx;
}

.cate-item .item .name {
  display: block;
  width: 365.625rpx;
  height: 35rpx;
  margin: 11.5rpx 0 22rpx 0;
  text-align: center;
  overflow: hidden;
  padding: 0 20rpx;
  font-size: 30rpx;
  color: #333;
}

.cate-item .item .price {
  display: block;
  width: 365.625rpx;
  height: 30rpx;
  text-align: center;
}

</style>
