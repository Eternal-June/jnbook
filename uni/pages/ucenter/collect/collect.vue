<template>
<view class="container">
  <view class="no-collect" v-if="collectList.length <= 0">
    <view class="c">
      <image src="/static/static/images/noCart_old.png"></image>
      <text>还没有收藏</text>
    </view>
  </view>
  <view class="collect-list" v-else>
    <view v-for="(item, index) in collectList" :key="index" class="item" @tap="openBooks" @touchstart="touchStartFun" @touchend="touchEndFun" :data-index="index">
      <image class="img" :src="item.picUrl"></image>
      <view class="info">
        <view class="name jnbook_f7">{{item.name}}</view>
        <view class="subtitle jnbook_f5">{{item.brief}}</view>
        <view class="price jnbook_f8">￥{{item.retailPrice}}</view>
      </view>
    </view>
  </view>

</view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var app = getApp();

export default {
  data() {
    return {
      type: 0,
      collectList: [],
      page: 1,
      size: 10,
      totalPages: 1,
      touchStart: "",
      touchEnd: ""
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    this.getCollectList();
  },

  onReachBottom() {
    if (this.totalPages > this.page) {
      this.setData({
        page: this.page + 1
      });
      this.getCollectList();
    } else {
      uni.showToast({
        title: '没有更多用户收藏了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },

  onReady: function () {},
  onShow: function () {},
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    getCollectList() {
      uni.showLoading({
        title: '加载中...'
      });
      let that = this;
      util.request(api.CollectList, {
        type: that.type,
        page: that.page,
        size: that.size
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            collectList: that.collectList.concat(res.data.collectList),
            totalPages: res.data.totalPages
          });
        }

        uni.hideLoading();
      });
    },

    openBooks(event) {
      let that = this;
      let index = event.currentTarget.dataset.index;
      let booksId = this.collectList[index].valueId; //触摸时间距离页面打开的毫秒数

      var touchTime = that.touchEnd - that.touchStart;
      console.log(touchTime); //如果按下时间大于350为长按

      if (touchTime > 350) {
        uni.showModal({
          title: '',
          content: '确定删除吗？',
          success: function (res) {
            if (res.confirm) {
              util.request(api.CollectAddOrDelete, {
                type: that.type,
                valueId: booksId
              }, 'POST').then(function (res) {
                if (res.errno === 0) {
                  console.log(res.data);
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success',
                    duration: 2000
                  });
                  that.collectList.splice(index, 1);
                  that.setData({
                    collectList: that.collectList
                  });
                }
              });
            }
          }
        });
      } else {
        uni.navigateTo({
          url: '/pages/books/books?id=' + booksId
        });
      }
    },

    //按下事件开始
    touchStartFun: function (e) {
      let that = this;
      that.setData({
        touchStart: e.timeStamp
      });
    },
    //按下事件结束
    touchEndFun: function (e) {
      let that = this;
      that.setData({
        touchEnd: e.timeStamp
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
  width: 100%;
  height: auto;
  overflow: hidden;
}

.no-collect {
  width: 100%;
  height: auto;
  margin: 0 auto;
}

.no-collect .c {
  width: 100%;
  height: auto;
  margin-top: 200rpx;
}

.no-collect .c image {
  margin: 0 auto;
  display: block;
  text-align: center;
  width: 258rpx;
  height: 258rpx;
}

.no-collect .c text {
  margin: 0 auto;
  display: block;
  width: 258rpx;
  height: 29rpx;
  line-height: 29rpx;
  text-align: center;
  font-size: 29rpx;
  color: #999;
}

.collect-list {
  width: 100%;
  height: auto;
  overflow: hidden;
  background: #fff;
  padding-left: 30rpx;
  border-top: 1px solid #e1e1e1;
}

.item {
  height: 242rpx;
  width: 720rpx;
  background: #fff;
  padding: 30rpx 30rpx 30rpx 0;
  border-bottom: 1px solid #e1e1e1;
}

.item:last-child {
  border-bottom: 1px solid #fff;
}

.item .img {
  float: left;
  width: 170rpx;
  height: 170rpx;
}

.item .info {
  float: right;
  width: 520rpx;
  height: 200rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 20rpx;
}

.item .info .name {
  line-height: 50rpx;
  display: -webkit-box;
 -webkit-box-orient: vertical;
 -webkit-line-clamp: 1;
  overflow: hidden;
}

.item .info .subtitle {
  margin-top: 8rpx;
  line-height: 50rpx;
  display: -webkit-box;
 -webkit-box-orient: vertical;
 -webkit-line-clamp: 2;
 overflow: hidden;
}

.item .info .price {
  margin-top: 8rpx;
  line-height: 50rpx;
}

</style>
