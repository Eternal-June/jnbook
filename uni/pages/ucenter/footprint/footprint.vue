<template>
<view class="container">
  <view class="no-footprint" v-if="footprintList.length <= 0">
    <view class="c">
      <image src="/static/static/images/noCart_old.png"></image>
      <text>没有浏览足迹</text>
    </view>
  </view>
  <view class="footprint" v-if="footprintList.length > 0">
    <view v-for="(item, index) in footprintList" :key="index" class="day-item">
      <view class="day-hd" v-if="item.length > 0">{{item[0].addTime}}</view>
      <view class="day-list" v-if="item.length > 0">
        <view v-for="(iitem, iindex) in item" :key="iindex" class="item" :data-index="index" :data-iindex="iindex" @touchstart="touchStartFun" @touchend="touchEndFun" @tap="deleteItem">
          <image class="img" :src="iitem.picUrl"></image>
          <view class="info">
            <view class="name jnbook_f7">{{iitem.name}}</view>
            <view class="subtitle jnbook_f5">{{iitem.brief}}</view>
            <view class="price jnbook_f8">￥{{iitem.retailPrice}}</view>
          </view>
        </view>
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
      footprintList: [],
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
    this.getFootprintList();
  },

  onReachBottom() {
    if (this.totalPages > this.page) {
      this.setData({
        page: this.page + 1
      });
      this.getFootprintList();
    } else {
      uni.showToast({
        title: '没有更多用户足迹了',
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
    getFootprintList() {
      uni.showLoading({
        title: '加载中...'
      });
      let that = this;
      util.request(api.FootprintList, {
        page: that.page,
        size: that.size
      }).then(function (res) {
        if (res.errno === 0) {
          let f1 = that.footprintList;
          let f2 = res.data.footprintList;

          for (let i = 0; i < f2.length; i++) {
            let last = f1.length - 1;

            if (last >= 0 && f1[last][0].addTime === f2[i].addTime) {
              f1[last].push(f2[i]);
            } else {
              let tmp = [];
              tmp.push(f2[i]);
              f1.push(tmp);
            }
          }

          that.setData({
            footprintList: f1,
            totalPages: res.data.totalPages
          });
        }

        uni.hideLoading();
      });
    },

    deleteItem(event) {
      let that = this;
      let index = event.currentTarget.dataset.index;
      let iindex = event.currentTarget.dataset.iindex;
      let footprintId = this.footprintList[index][iindex].id;
      let booksId = this.footprintList[index][iindex].booksId;
      var touchTime = that.touchEnd - that.touchStart;
      console.log(touchTime); //如果按下时间大于350为长按

      if (touchTime > 350) {
        uni.showModal({
          title: '',
          content: '要删除所选足迹？',
          success: function (res) {
            if (res.confirm) {
              util.request(api.FootprintDelete, {
                id: footprintId
              }, 'POST').then(function (res) {
                if (res.errno === 0) {
                  uni.showToast({
                    title: '删除成功',
                    icon: 'success',
                    duration: 2000
                  });
                  that.footprintList[index].splice(iindex, 1);

                  if (that.footprintList[index].length == 0) {
                    that.footprintList.splice(index, 1);
                  }

                  that.setData({
                    footprintList: that.footprintList
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
      console.log(e.timeStamp + '- touchStart');
    },
    //按下事件结束
    touchEndFun: function (e) {
      let that = this;
      that.setData({
        touchEnd: e.timeStamp
      });
      console.log(e.timeStamp + '- touchEnd');
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

.no-footprint {
  width: 100%;
  height: auto;
  margin: 0 auto;
}

.no-footprint .c {
  width: 100%;
  height: auto;
  margin-top: 200rpx;
}

.no-footprint .c image {
  margin: 0 auto;
  display: block;
  text-align: center;
  width: 258rpx;
  height: 258rpx;
}

.no-footprint .c text {
  margin: 0 auto;
  display: block;
  width: 258rpx;
  height: 29rpx;
  line-height: 29rpx;
  text-align: center;
  font-size: 29rpx;
  color: #999;
}

.footprint {
  height: auto;
  overflow: hidden;
  width: 100%;
  border-top: 1px solid #e1e1e1;
}

.day-item {
  height: auto;
  overflow: hidden;
  width: 100%;
  margin-bottom: 20rpx;
}

.day-hd {
  height: 94rpx;
  width: 100%;
  line-height: 94rpx;
  background: #fff;
  padding-left: 30rpx;
  color: #333;
  font-size: 28rpx;
}

.day-list {
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
  line-height: 40rpx;
}

</style>
