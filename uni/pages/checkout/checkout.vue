<template>
<view class="container">
  <view class="address-box">
    <view class="address-item" @tap="selectAddress" v-if="checkedAddress.id > 0">
      <view class="l">
        <text class="name">{{checkedAddress.name}}</text>
        <text class="default" v-if="checkedAddress.isDefault">默认</text>
      </view>
      <view class="m">
        <text class="mobile">{{checkedAddress.mobile}}</text>
        <text class="address">{{checkedAddress.address}}</text>
      </view>
      <view class="r">
        <image src="/static/static/images/address_right.png"></image>
      </view>
    </view>
    <view class="address-item address-empty" @tap="selectAddress" v-else>
      <view class="m">
        还没有收货地址，去添加
      </view>
      <view class="r">
        <image src="/static/static/images/address_right.png"></image>
      </view>
    </view>
  </view>

  <view class="message-box">
    <input class="message-item" @input="bindMessageInput" placeholder="如需要，请输入留言" :value="message" placeholder-style="color: #ccc;"></input>
  </view>

  <view class="books-items" v-if="isMultiOrderModel==1">
    <view v-for="(bitem, index) in brandCartbooks" :key="index" class="group-item">
      <view v-for="(item, index2) in bitem.cartList" :key="index2" class="item">
        <view class="img">
          <image :src="item.picUrl"></image>
        </view>
        <view class="info">
          <view class="t">
            <text class="name">{{item.booksName}}</text>
          </view>
          <view class="m">{{item.specifications}}</view>
          <view class="b">￥{{item.price}}</view>
          <text class="number">x{{item.number}}</text>
        </view>
      </view>
      <view class="brand-order">
        <view class="order-item">
          <view class="l">
            <text class="name">图书合计</text>
          </view>
          <view class="r">
            <text class="txt">￥{{bitem.bandBooksTotalPrice}}元</text>
          </view>
        </view>
        <view class="order-item">
          <view class="l">
            <text class="name">运费</text>
          </view>
          <view class="r">
            <text class="txt">￥{{bitem.bandFreightPrice}}元</text>
          </view>
        </view>
      </view>
    </view>
  </view>

  <view class="books-items" v-else>
    <view v-for="(item, index) in checkedBooksList" :key="index" class="item">
      <view class="img">
        <image :src="item.picUrl"></image>
      </view>
      <view class="info">
        <view class="t">
          <text class="name">{{item.booksName}}</text>
          <text class="number">x{{item.number}}</text>
        </view>
        <view class="m">{{item.specifications}}</view>
        <view class="b">￥{{item.price}}</view>
      </view>
    </view>
  </view>

  <view class="order-box" v-if="isMultiOrderModel==1">
  </view>

  <view class="order-box" v-else>
    <view class="order-item">
      <view class="l">
        <text class="name">图书合计</text>
      </view>
      <view class="r">
        <text class="txt">￥{{booksTotalPrice}}元</text>
      </view>
    </view>
    <view class="order-item">
      <view class="l">
        <text class="name">运费</text>
      </view>
      <view class="r">
        <text class="txt">￥{{freightPrice}}元</text>
      </view>
    </view>
  </view>

  <view class="order-total">
    <view class="l">实付：￥{{actualPrice}}</view>
    <view class="r" @tap="submitOrder">去付款</view>
  </view>
</view>
</template>

<script>
var util = require("../../utils/util.js");
var api = require("../../config/api.js");
var app = getApp();
var lastTime = null;

export default {
  data() {
    return {
      isMultiOrderModel: 0,
      brandCartbooks: [],
      checkedBooksList: [],
      checkedAddress: {},
      booksTotalPrice: 0.00,
      //书籍总价
      freightPrice: 0.00,
      //快递费
      orderTotalPrice: 0.00,
      //订单总价
      actualPrice: 0.00,
      //实际需要支付的总价
      cartId: 0,
      addressId: 0,
      message: '',

    };
  },

  components: {},
  props: {},
  onLoad: function (options) {// 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {// 页面渲染完成
          util.request(api.OrderSubmit, {
        cartId: this.cartId,
        addressId: this.addressId,
        message: this.message,
      }, 'POST');
  },
  onShow: function () {
    // 页面显示
    uni.showLoading({
      title: '加载中...'
    });

    try {
      var cartId = uni.getStorageSync('cartId');

      if (cartId) {
        this.setData({
          'cartId': cartId
        });
      }

      var addressId = uni.getStorageSync('addressId');

      if (addressId) {
        this.setData({
          'addressId': addressId
        });
      }
    } catch (e) {
      // Do something when catch error
      console.log(e);
    }

    this.getCheckoutInfo();
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    //获取checkou信息
    getCheckoutInfo: function () {
      let that = this;
      util.request(api.CartCheckout, {
        cartId: that.cartId,
        addressId: that.addressId,
      }).then(function (res) {
        if (res.errno === 0) {
          let brandCartbooks = [];
          let checkedBooksList = [];

          if (res.data.isMultiOrderModel === 1) {
            brandCartbooks = res.data.brandCartbooks;
          } else {
            checkedBooksList = res.data.checkedBooksList;
          }

          that.setData({
            isMultiOrderModel: res.data.isMultiOrderModel,
            brandCartbooks: brandCartbooks,
            checkedBooksList: checkedBooksList,
            checkedAddress: res.data.checkedAddress,
            actualPrice: res.data.actualPrice,
            freightPrice: res.data.freightPrice,
            booksTotalPrice: res.data.booksTotalPrice,
            orderTotalPrice: res.data.orderTotalPrice,
            addressId: res.data.addressId,
          });
        }

        uni.hideLoading();
      });
    },

    selectAddress() {
      uni.navigateTo({
        url: '/pages/ucenter/address/address'
      });
    },


    bindMessageInput: function (e) {
      this.setData({
        message: e.detail.value
      });
    },
    submitOrder: function () {
      if (this.addressId <= 0) {
        util.showErrorToast('请选择收货地址');
        return false;
      }

      util.jnbookLoadShow("正在下单，请稍后...");
      let nowTime = +new Date();

      if (nowTime - lastTime > 5000 || !lastTime) {
        //5秒内避免重复提交订单
        lastTime = nowTime;
      } else {
        return false;
      }

      util.request(api.OrderSubmit, {
        cartId: this.cartId,
        addressId: this.addressId,
        message: this.message,
      }, 'POST').then(res => {
        util.jnbookLoadHide();
        const orderId = res.data.orderId;
        if (res.errno === 0) {
          console.log(1);
          // util.request(api.OrderPrepay, {
          //   orderId: orderId
          // }, 'POST').then(function (res) {
          //   if (res.errno === 0) {
          //     const payParam = res.data;
          //     console.log("支付过程开始");
          //     uni.requestPayment({
          //       'timeStamp': payParam.timeStamp,
          //       'nonceStr': payParam.nonceStr,
          //       'package': payParam.packageValue,
          //       'signType': payParam.signType,
          //       'paySign': payParam.paySign,
          //       'success': function (res) {
          //         console.log("支付过程成功");
          //         uni.redirectTo({
          //           url: '/pages/payResult/payResult?status=1&orderId=' + orderId
          //         });
          //       },
          //       'fail': function (res) {
          //         console.log("支付过程失败");
          //         uni.redirectTo({
          //           url: '/pages/payResult/payResult?status=0&orderId=' + orderId
          //         });
          //       },
          //       'complete': function (res) {
          //         console.log("支付过程结束");
          //       }
          //     });
          //   } else {
          //     uni.redirectTo({
          //       url: '/pages/payResult/payResult?status=0&orderId=' + orderId
          //     });
          //   }
          // });
          util.request(api.OrderImitatepay, {
            orderId: orderId
          }, 'POST').then(function (res) {
            if (res.errno === 0) {
                  console.log("支付过程成功");
                  uni.redirectTo({
                    url: '/pages/payResult/payResult?status=1&orderId=' + orderId
                  });
            } else {
              uni.redirectTo({
                url: '/pages/payResult/payResult?status=0&orderId=' + orderId
              });
            }
          });
        } else {
          uni.redirectTo({
            url: '/pages/payResult/payResult?status=0&orderId=' + orderId
          });
        }
      });
    }
  }
};
</script>
<style>
page {
  height: auto;
  min-height: 100%;
  background: #f4f4f4;
}

.address-box {
  width: 100%;
  height: 166.55rpx;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAAKCAMAAADfAc3wAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAYUExURc2AgICg1v///+rw+ffq6tKMjI2p2ouo2QT3I5MAAAA9SURBVCjPpcs5EgAgCARB5ND//9jEYwlZJp1q0ZTNAS3L10OgcGlYlYZNuGoRly3guv2YsA8z9mLKHsxZ3e5sBBsNqhCTAAAAAElFTkSuQmCC') 0 0 repeat-x;
  background-size: 62.5rpx 10.5rpx;
  margin-bottom: 20rpx;
  padding-top: 10.5rpx;
}

.address-item {
  display: flex;
  height: 155.55rpx;
  background: #fff;
  padding: 41.6rpx 0 41.6rpx 31.25rpx;
}

.address-item.address-empty {
  line-height: 75rpx;
  text-align: center;
}

.address-box .l {
  width: 125rpx;
  height: 100%;
}

.address-box .l .name {
  margin-left: 6.25rpx;
  margin-top: -7.25rpx;
  display: block;
  width: 125rpx;
  height: 43rpx;
  line-height: 43rpx;
  font-size: 30rpx;
  color: #333;
  margin-bottom: 15rpx;
}

.address-box .l .default {
  margin-left: 6.25rpx;
  display: block;
  width: 62rpx;
  height: 33rpx;
  border-radius: 5rpx;
  border: 1px solid #b4282d;
  font-size: 20.5rpx;
  text-align: center;
  line-height: 29rpx;
  color: #b4282d;
}

.address-box .m {
  flex: 1;
  height: 72.25rpx;
  color: #999;
}

.address-box .mobile {
  display: block;
  height: 29rpx;
  line-height: 29rpx;
  margin-bottom: 16.25rpx;
  font-size: 30rpx;
  color: #333;
}

.address-box .address {
  display: block;
  height: 37.5rpx;
  line-height: 37.5rpx;
  font-size: 25rpx;
  color: #666;
}

.address-box .r {
  width: 77rpx;
  height: 77rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.address-box .r image {
  width: 52.078rpx;
  height: 52.078rpx;
}

.message-box {
  width: 100%;
  height: auto;
  overflow: hidden;
  background: #fff;
  margin-bottom: 16.25rpx;
}

.message-box .message-item {
  height: 72.078rpx;
  overflow: hidden;
  background: #fff;
  display: flex;
  margin-left: 31.25rpx;
  padding-right: 31.25rpx;
  padding-top: 10rpx;
}

.group-item {
 width: 100%;
 background: #fff;
 height: auto;
 padding-right: 11.25rpx;
 margin-bottom: 26.25rpx;
}
.group-item .h {
  height: 83.3rpx;
  line-height: 83.3rpx;
  margin-left: 21.25rpx;
  padding-right: 11.25rpx;
  border-bottom: 1px solid #f4f4f4;
  font-size: 30rpx;
  color: #333;
}

.group-item .h .l {
  float: left;
  font-weight:bold;
}

.brand-order{
  width: 100%;
  height: auto;
  overflow: hidden;
  background: #fff;
}


.brand-order .order-item {
  height: 64.3rpx;
  overflow: hidden;
  background: #fff;
  display: flex;
  margin-left: 31.25rpx;
  padding-right: 31.25rpx;
  padding-top: 16rpx;
  border-bottom: 1px solid #fafafa;
}

.brand-order .order-item .l {
  float: left;
  height: 42rpx;
  width: 50%;
  line-height: 42rpx;
  overflow: hidden;
}

.brand-order .order-item .r {
  float: right;
  text-align: right;
  width: 50%;
  height: 42rpx;
  line-height: 42rpx;
  overflow: hidden;
}

.order-box {
  margin-top: 10rpx;
  width: 100%;
  height: auto;
  overflow: hidden;
  background: #fff;
  margin-bottom: 120rpx;
}

.order-box .order-item {
  height: 64.3rpx;
  overflow: hidden;
  background: #fff;
  display: flex;
  margin-left: 31.25rpx;
  padding-right: 31.25rpx;
  padding-top: 16rpx;
  border-bottom: 1px solid #fafafa;
}

.order-box .order-item .l {
  float: left;
  height: 42rpx;
  width: 50%;
  line-height: 42rpx;
  overflow: hidden;
}

.order-box .order-item .r {
  float: right;
  text-align: right;
  width: 50%;
  height: 42rpx;
  line-height: 42rpx;
  overflow: hidden;
}

.order-box .order-item.no-border {
  border-bottom: none;
}

.books-items {
  margin-top: 20rpx;
  width: 100%;
  height: auto;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.books-items .item {
  height: 162rpx;
  padding-left: 11.25rpx;
  padding-right: 31.25rpx;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #fafafa;
}

.books-items .item.no-border {
  border-bottom: none;
}

.books-items .item:last-child {
  border-bottom: none;
}

.books-items .img {
  height: 145.83rpx;
  width: 145.83rpx;
  background-color: #f4f4f4;
  margin-right: 20rpx;
}

.books-items .img image {
  height: 145.83rpx;
  width: 145.83rpx;
}

.books-items .info {
  flex: 1;
  height: 145.83rpx;
  padding-top: 5rpx;
}

.books-items .t {
  height: 33rpx;
  line-height: 33rpx;
  margin-bottom: 10rpx;
  overflow: hidden;
  font-size: 30rpx;
  color: #333;
}

.books-items .t .name {
  display: block;
  float: left;
}

.books-items .b {
  float: left;
  height: 41rpx;
  overflow: hidden;
  line-height: 41rpx;
  font-size: 30rpx;
  color: #ec4223;
}
.books-items .number {
  display: block;
  float: right;
  text-align: right;
}

.books-items .m {
  height: 29rpx;
  overflow: hidden;
  line-height: 29rpx;
  margin-bottom: 25rpx;
  font-size: 25rpx;
  color: #999;
}


.order-total {
  position: fixed;
  left: 0;
  bottom: 0;
  height: 100rpx;
  width: 100%;
  display: flex;
}

.order-total .l {
  flex: 1;
  height: 100rpx;
  line-height: 100rpx;
  color: #b4282d;
  background: #fff;
  font-size: 33rpx;
  padding-left: 31.25rpx;
  border-top: 1rpx solid rgba(0, 0, 0, 0.2);
  border-bottom: 1rpx solid rgba(0, 0, 0, 0.2);
}

.order-total .r {
  width: 233rpx;
  height: 100rpx;
  background: #b4282d;
  border: 1px solid #fafafa;
  line-height: 100rpx;
  text-align: center;
  color: #fff;
  font-size: 30rpx;
}

</style>
