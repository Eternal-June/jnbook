<template>
  <view>
    <view class="user">
      <view class="header bg-color acea-row row-between-wrapper">
        <view class="picTxt acea-row row-between-wrapper" @tap="goLogin">
          <view class="pictrue"><image :src="userInfo.avatarUrl"></image></view>
          <view class="text">
            <view class="acea-row row-middle">
              <view class="name line1">{{ userInfo.nickName || "请授权" }}</view>
            </view>
            <view class="id">用户编号：1000{{ userInfo.userId || 0 }}</view>
          </view>
        </view>
        <text
          class="iconfont icon-shezhi"
          @tap="goPages"
          data-url="/pages/ucenter/user/user"
        ></text>
      </view>
      <view class="wrapper">
        <view class="nav acea-row row-middle">
          <view
            class="item"
            hover-class="none"
            v-if="userInfo.userLevel == 2"
          >
            <view class="title">当前余额</view>
            <view class="text"
              ><text
                class="iconfont icon-jinbi1"
                style="color: #ff654b; margin-right: 8rpx"
              ></text>
              {{ remainAmount || 0.0 }}</view
            >
          </view>
          <view class="item" hover-class="none" v-else>
            <view class="title">用户类型</view>
            <view class="text"
              ><text
                class="iconfont icon-jingyanzhi"
                style="color: #ff654b; margin-right: 8rpx"
              ></text>
              {{ userInfo.userLevelDesc || "普通用户" }}</view
            >
          </view>
          <view
            class="item"
            hover-class="none"
            v-if="userInfo.userLevel == 2"
          >
            <view class="title">卖书总收益</view>
            <view class="text"
              ><text
                class="iconfont icon-qiandai"
                style="color: #ff654b; margin-right: 8rpx"
              ></text>
              {{ totalAmount || 0 }}</view
            >
          </view>
          <view class="item" hover-class="none" v-else>
            <view class="title">注册时间</view>
            <view class="text"
              ><text
                class="iconfont icon-shenhezhong"
                style="color: #ff654b; margin-right: 8rpx"
              ></text
              >{{ userInfo.registerDate || "2021-10-01" }}</view
            >
          </view>
        </view>
        <view class="myOrder">
          <view class="title acea-row row-between-wrapper">
            <view class="jnbook_f4">我的订单</view>
            <view class="allOrder jnbook_f6" hover-class="none" @tap="goOrder"
              >全部订单<text class="iconfont icon-gengduo"></text
            ></view>
          </view>
          <view class="orderState acea-row row-middle">
            <view
              class="item"
              hover-class="none"
              @tap="goOrderIndex"
              data-index="1"
              data-route="/pages/ucenter/order/order"
            >
              <view class="pictrue">
                <text class="item_badge" v-if="order.unpaid != 0">{{
                  order.unpaid
                }}</text>
                <image src="/static/static/images/money.png"></image>
              </view>
              <view class="jnbook_f2">待付款</view>
            </view>
            <view
              class="item"
              hover-class="none"
              @tap="goOrderIndex"
              data-index="2"
              data-route="/pages/ucenter/order/order"
            >
              <view class="pictrue">
                <text class="item_badge" v-if="order.unship != 0">{{
                  order.unship
                }}</text>
                <image src="/static/static/images/wuliu.png"></image>
              </view>
              <view class="jnbook_f2">待发货</view>
            </view>
            <view
              class="item"
              hover-class="none"
              @tap="goOrderIndex"
              data-index="3"
              data-route="/pages/ucenter/order/order"
            >
              <view class="pictrue">
                <text class="item_badge" v-if="order.unrecv != 0">{{
                  order.unrecv
                }}</text>
                <image src="/static/static/images/recieve.png"></image>
              </view>
              <view class="jnbook_f2">待收货</view>
            </view>
            <view
              class="item"
              hover-class="none"
              @tap="goOrderIndex"
              data-index="4"
              data-route="/pages/ucenter/order/order"
            >
              <view class="pictrue">
                <text class="item_badge" v-if="order.uncomment != 0">{{
                  order.uncomment
                }}</text>
                <image src="/static/static/images/disqus.png"></image>
              </view>
              <view class="jnbook_f2">待评价</view>
            </view>
          </view>
        </view>
        <view class="myService">
          <view class="title acea-row row-middle jnbook_f4">我的服务</view>
          <view class="serviceList acea-row row-middle">
            <view
              v-for="(item, index) in MyMenus"
              :key="index"
              class="item"
              @tap="goPages"
              :data-url="item.url"
              v-if="item.url != '#'"
            >
              <view class="pictrue"
                ><image :src="'/static/static/images/' + item.pic"></image
              ></view>
              <view class="jnbook_f2">{{ item.name }}</view>
            </view>
            <button class="item" open-type="contact" hover-class="none">
              <view class="pictrue"
                ><image src="/static/static/images/customer.png"></image
              ></view>
              <view class="jnbook_f2">联系客服</view>
            </button>
          </view>
        </view>

        <view class="myService">
          <view class="title acea-row row-middle jnbook_f4">我卖的书 </view>
        </view>

        <view class="serviceList acea-row row-middle">
          <view class="a-section a-new" v-if="mybooks.length > 0">
            <view class="b">
              <view v-for="(item, index) in mybooks" :key="index" class="item">
                <navigator :url="'/pages/books/books?id=' + item.id">
                  <image class="img" :src="item.picUrl" background-size="cover"></image>
                  <text class="name jnbook_f7">{{ item.name }}</text>
                </navigator>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
    <authorize @onLoadFun="onLoadFun"></authorize>
  </view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var user = require("../../../utils/user.js");
const app = getApp();

export default {
  data() {
    return {
      mybooks: [],
      userInfo: {
        nickName: "点击登录",
        avatarUrl: "/static/static/images/avatar.png",
      },
      order: {
        unpaid: 0,
        unship: 0,
        unrecv: 0,
        uncomment: 0,
      },
      MyMenus: [
        {
          url: "/pages/sell/sell",
          pic: "sell_book.png",
          name: "我要卖书",
        },
        {
          url: "/pages/ucenter/collect/collect",
          pic: "icon_collect.png",
          name: "书籍收藏",
        },
        {
          url: "/pages/ucenter/address/address",
          pic: "address.png",
          name: "地址管理",
        },
        {
          url: "/pages/ucenter/footprint/footprint",
          pic: "footprint.png",
          name: "浏览足迹",
        },
        {
          url: "/pages/ucenter/feedback/feedback",
          pic: "feedback.png",
          name: "意见反馈",
        },
        {
          url: "/pages/help/help",
          pic: "help.png",
          name: "使用帮助",
        },
        {
          url: "/pages/about/about",
          pic: "about_us.png",
          name: "关于我们",
        },
      ],
      hasLogin: false,
      totalAmount: 0.0,
      remainAmount: "",
    };
  },

  components: {},
  props: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {},

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {},

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {},
  onShow: function () {
    //获取用户的登录信息
    if (app.globalData.hasLogin) {
      let userInfo = uni.getStorageSync("userInfo");
      this.setData({
        userInfo: userInfo,
        hasLogin: true,
      });
      let that = this;
      util.request(api.UserIndex).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            order: res.data.order,
            totalAmount: res.data.totalAmount,
            remainAmount: res.data.remainAmount,
          });
        }
      });

      util.request(api.BooksSelled).then(function (res) {
        console.log(res.data);
        if (res.errno === 0) {
          that.setData({
            mybooks: res.data.myBooksList,
          });
        }
      });
    }
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {},
  methods: {
    /**
     * 页面跳转
     */
    goPages: function (e) {
      console.log();

      if (this.hasLogin) {
        uni.navigateTo({
          url: e.currentTarget.dataset.url,
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login",
        });
      }
    },

    goLogin() {
      if (!this.hasLogin) {
        uni.navigateTo({
          url: "/pages/auth/login/login",
        });
      }
    },

    goOrder() {
      if (this.hasLogin) {
        try {
          uni.setStorageSync("tab", "0");
        } catch (e) {}

        uni.navigateTo({
          url: "/pages/ucenter/order/order",
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login",
        });
      }
    },

    goOrderIndex(e) {
      if (this.hasLogin) {
        let tab = e.currentTarget.dataset.index;
        let route = e.currentTarget.dataset.route;

        try {
          uni.setStorageSync("tab", tab);
        } catch (e) {}

        uni.navigateTo({
          url: route,
          success: function (res) {},
          fail: function (res) {},
          complete: function (res) {},
        });
      } else {
        uni.navigateTo({
          url: "/pages/auth/login/login",
        });
      }
    },

    goAfterSale: function () {
      uni.showToast({
        title: "目前不支持",
        icon: "none",
        duration: 2000,
      });
    },
  },
};
</script>
<style>
a-new .b {
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
.user .header {
  padding: 0 30rpx;
  height: 190rpx;
  position: relative;
}
.user .header:after {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -98rpx;
  z-index: -1;
  content: "";
  height: 100rpx;
  width: 100%;
  border-radius: 0 0 50% 50%;
  background-color: #388ceb;
}
.user .header .picTxt .pictrue {
  width: 120rpx;
  height: 120rpx;
}
.user .header .picTxt .pictrue image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #f5f5f5;
}
.user .header .picTxt .text {
  width: 434rpx;
  color: rgba(255, 255, 255, 1);
  margin-left: 35rpx;
}
.user .header .picTxt .text .name {
  color: #fff;
  font-size: 36rpx;
  max-width: 470rpx;
}
.user .header .picTxt .text .member {
  padding: 0 10rpx;
  height: 36rpx;
  background-color: rgba(0, 0, 0, 0.2);
  font-size: 20rpx;
  border-radius: 30rpx;
  margin-left: 17rpx;
}
.user .header .picTxt .text .member image {
  width: 22rpx;
  height: 22rpx;
  font-size: 20rpx;
  margin-right: 8rpx;
}
.user .header .picTxt .text .id {
  color: rgba(255, 255, 255, 0.6);
  font-size: 26rpx;
  margin-top: 15rpx;
}
.user .header .picTxt .text .id .iconfont {
  font-size: 30rpx;
  margin-left: 12rpx;
}
.user .header .icon-shezhi {
  font-size: 38rpx;
  color: #fff;
  margin-top: 15rpx;
  margin-right: 30rpx;
}
.user .wrapper {
  padding: 0 20rpx;
}
.user .wrapper .nav {
  background-color: #fff;
  border-radius: 20rpx;
  height: 140rpx;
}
.user .wrapper .nav .item {
  flex: 1;
  text-align: center;
}
.user .wrapper .nav .item ~ .item {
  border-left: 1px solid #eee;
}
.user .wrapper .nav .item .title {
  font-size: 28rpx;
  color: #666;
}
.user .wrapper .nav .item .num {
  margin-top: 16rpx;
  font-size: 36rpx;
  color: rgb(223, 8, 8);
}
.user .wrapper .nav .item .text {
  margin-top: 16rpx;
  font-size: 30rpx;
}
.user .wrapper .myOrder {
  background-color: #fff;
  border-radius: 10rpx;
  margin-top: 15rpx;
}
.user .wrapper .myOrder .title,
.user .wrapper .myService .title {
  height: 88rpx;
  padding: 0 30rpx;
  border-bottom: 1px dashed #ddd;
  font-size: 30rpx;
  color: #282828;
}
.user .wrapper .myOrder .title .allOrder {
  font-size: 26rpx;
  color: #666;
}
.user .wrapper .myOrder .title .allOrder .iconfont {
  font-size: 30rpx;
  margin-left: 7rpx;
}
.user .wrapper .myOrder .orderState {
  height: 160rpx;
}
.user .wrapper .myOrder .orderState .item {
  font-size: 26rpx;
  color: #454545;
  flex: 1;
  text-align: center;
}
.user .wrapper .myOrder .orderState .item .pictrue {
  width: 64rpx;
  height: 64rpx;
  margin: 0 auto 18rpx auto;
}
.user .wrapper .myOrder .orderState .item .pictrue image {
  width: 100%;
  height: 100%;
}
.user .wrapper .myService {
  background-color: #fff;
  margin-top: 15rpx;
  border-radius: 10rpx;
}
.user .wrapper .myService .serviceList {
  padding: 8rpx 0 27rpx 0;
}
.user .wrapper .myService .serviceList .item {
  width: 25%;
  text-align: center;
  font-size: 26rpx;
  color: #333;
  margin-top: 30rpx;
}
.user .wrapper .myService .serviceList .item .pictrue {
  width: 64rpx;
  height: 64rpx;
  margin: 0 auto 16rpx auto;
}
.user .wrapper .myService .serviceList .item .pictrue image {
  width: 100%;
  height: 100%;
}
.user .support {
  width: 219rpx;
  height: 74rpx;
  margin: 54rpx auto;
  display: block;
}
.item_badge {
  height: 32rpx;
  width: 32rpx;
  position: absolute;
  background: #b4282d;
  color: #fff;
  font-size: 28rpx;
  border-radius: 50%;
  margin-top: 0rpx;
  margin-left: 40rpx;
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
.container .a-section .title .text .name .new {
  position: absolute;
  top: 6rpx;
  left: 210rpx;
  font-size: 20rpx;
  font-weight: bold;
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
.a-topic .b .desc {
  display: block;
  height: 30rpx;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.container .a-section .title .more .iconfont {
  margin-left: 9rpx;
  font-size: 26rpx;
  vertical-align: 3rpx;
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
</style>
