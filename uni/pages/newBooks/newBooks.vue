<template>
<view class="container">
  <view class="brand-info">
    <view class="name">
      <image class="img" :src="bannerInfo.imgUrl" background-size="cover"></image>
      <view class="info-box">
        <view class="info">
          <text class="txt">{{bannerInfo.name}}</text>
          <text class="line"></text>
        </view>
      </view>
    </view>
  </view>
  <view class="sort">
    <view class="sort-box">
      <view :class="'item ' + (currentSortType == 'default' ? 'active' : '')" @tap="openSortFilter" id="defaultSort">
        <text class="txt">综合</text>
      </view>
      <view :class="'item by-price ' + (currentSortType == 'price' ? 'active' : '') + ' ' + (currentSortOrder == 'asc'  ? 'asc' : 'desc')" @tap="openSortFilter" id="priceSort">
        <text class="txt">价格</text>
      </view>
       <view :class="'item by-price ' + (currentSortType == 'browse' ? 'active' : '') + ' ' + (currentSortOrder == 'asc'  ? 'asc' : 'desc')" @tap="openSortFilter" id="browseSort">
        <text class="txt">浏览量</text>
      </view>
    </view>
    <view class="sort-box-category" v-if="categoryFilter">
      <view v-for="(item, index) in filterCategory" :key="index" :class="'item ' + (item.checked ? 'active' : '')" :data-category-index="index" @tap="selectCategory">{{item.name}}</view>
    </view>
  </view>
  <view class="cate-item">
    <view class="b">
      <block v-for="(iitem, iindex) in booksList" :key="iindex">
        <navigator :class="'item ' + (iindex % 2 == 0 ? 'item-b' : '' )" :url="'../books/books?id=' + iitem.id">
          <image class="img" :src="iitem.picUrl" background-size="cover"></image>
          <text class="name">{{iitem.name}}</text>
          <text class="price">￥{{iitem.retailPrice}}</text>
        </navigator>
      </block>
    </view>
  </view>
</view>
</template>

<script>
var util = require("../../utils/util.js");
var api = require("../../config/api.js");
var app = getApp();

export default {
  data() {
    return {
      bannerInfo: {
        'imgUrl': 'https://jnbook.oss-cn-shanghai.aliyuncs.com/hj7banyc1k3be759m47i.jpg',
        'name': '大家都在买的'
      },
      categoryFilter: false,
      filterCategory: [],
      booksList: [],
      categoryId: 0,
      currentSortType: 'default',
      currentSort: 'add_time',
      currentSortOrder: 'desc',
      page: 1,
      size: 10,
      totalPages: 1
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    this.getBooksList();
  },
  onReady: function () {// 页面渲染完成
  },
  onShow: function () {// 页面显示
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
    getBooksList: function () {
      this.booksList=[];
      var that = this;
      util.request(api.BooksList, {
        isNew: true,
        page: that.page,
        size: that.size,
        order: that.currentSortOrder,
        sort: that.currentSort,
        categoryId: that.categoryId
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            booksList: that.booksList.concat(res.data.booksList),
            filterCategory: res.data.filterCategoryList,
            totalPages: res.data.totalPages
          });
        }
      });
    },
    openSortFilter: function (event) {
      let currentId = event.currentTarget.id;
      let tmpSortOrder = 'asc';

      switch (currentId) {
        case 'categoryFilter':
          this.setData({
            categoryFilter: !this.categoryFilter,
            currentSortType: 'category',
            currentSort: 'add_time',
            currentSortOrder: 'desc'
          });
          break;

        case 'priceSort':
          if (this.currentSortOrder == 'asc') {
            tmpSortOrder = 'desc';
          }

          this.setData({
            currentSortType: 'price',
            currentSort: 'retail_price',
            currentSortOrder: tmpSortOrder,
            categoryFilter: false
          });
          this.getBooksList();
          break;

        case 'browseSort':
          if (this.currentSortOrder == 'asc') {
            tmpSortOrder = 'desc';
          }

          this.setData({
            currentSortType: 'browse',
            currentSort: 'browse',
            currentSortOrder: tmpSortOrder,
            categoryFilter: false
          });
          this.getBooksList();
          break;

        default:
          //综合排序
          this.setData({
            currentSortType: 'default',
            currentSort: 'add_time',
            currentSortOrder: 'desc',
            categoryFilter: false,
            categoryId: 0
          });
          this.getBooksList();
      }
    },
    selectCategory: function (event) {
      let currentIndex = event.target.dataset.categoryIndex;
      this.setData({
        'categoryFilter': false,
        'categoryId': this.filterCategory[currentIndex].id
      });
      this.getBooksList();
    }
  }
};
</script>
<style>

.brand-info .name {
  width: 100%;
  height: 278rpx;
  position: relative;
}

.brand-info .img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 278rpx;
}

.brand-info .info-box {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 278rpx;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.brand-info .info {
  display: block;
}

.brand-info .txt {
  display: block;
  height: 40rpx;
  font-size: 37.5rpx;
  color: #fff;
}

.brand-info .line {
  margin: 0 auto;
  margin-top: 16rpx;
  display: block;
  height: 2rpx;
  width: 145rpx;
  background: #fff;
}

.page {
  background: #f4f4f4;
}

.sort {
  position: relative;
  background: #fff;
  width: 100%;
  height: 78rpx;
}

.sort-box {
  background: #fff;
  width: 100%;
  height: 78rpx;
  overflow: hidden;
  padding: 0 30rpx;
  display: flex;
  border-bottom: 1px solid #d9d9d9;
}

.sort-box .item {
  height: 78rpx;
  line-height: 78rpx;
  text-align: center;
  flex: 1;
  color: #333;
  font-size: 30rpx;
}

.sort-box .item .txt {
  display: block;
  width: 100%;
  height: 100%;
  color: #333;
}

.sort-box .item.active .txt {
  color: #ec4223;
}

.sort-box .item.by-price {
  background: url("//yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/icon-normal/no-3127092a69.png") 155rpx center no-repeat;
  background-size: 15rpx 21rpx;
}

.sort-box .item.by-price.active.asc {
  background: url("http://yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/icon-normal/up-636b92c0a5.png") 155rpx center no-repeat;
  background-size: 15rpx 21rpx;
}

.sort-box .item.by-price.active.desc {
  background: url("http://yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/icon-normal/down-95e035f3e5.png") 155rpx center no-repeat;
  background-size: 15rpx 21rpx;
}

.sort-box-category {
  background: #fff;
  width: 100%;
  height: auto;
  overflow: hidden;
  padding: 40rpx 40rpx 0 0;
  border-bottom: 1px solid #d9d9d9;
}

.sort-box-category .item {
  height: 54rpx;
  line-height: 54rpx;
  text-align: center;
  float: left;
  padding: 0 16rpx;
  margin: 0 0 40rpx 40rpx;
  border: 1px solid #666;
  color: #333;
  font-size: 24rpx;
}

.sort-box-category .item.active {
  color: #ec4223;
  border: 1px solid #ec4223;
}

.cate-item .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  border-top: 1rpx solid #f4f4f4;
  margin-top: 20rpx;
}

.cate-item .b .item {
  float: left;
  background: #fff;
  width: 375rpx;
  padding-bottom: 33.333rpx;
  border-bottom: 1rpx solid #f4f4f4;
  height: auto;
  overflow: hidden;
  text-align: center;
}

.cate-item .b .item-b {
  border-right: 1rpx solid #f4f4f4;
}

.cate-item .item .img {
  margin-top: 10rpx;
  width: 302rpx;
  height: 302rpx;
}

.cate-item .item .name {
  display: block;
  width: 365.625rpx;
  height: 35rpx;
  padding: 0 20rpx;
  overflow: hidden;
  margin: 11.5rpx 0 22rpx 0;
  text-align: center;
  font-size: 30rpx;
  color: #333;
}

.cate-item .item .price {
  display: block;
  width: 365.625rpx;
  height: 30rpx;
  text-align: center;
  font-size: 30rpx;
  color: #ec4223;
}

</style>
