<template>
<view class="container">
  <view class="order-info">
      <view class="t">
        <text class="lable">订单编号：</text>
        <text class="value">{{orderInfo.orderSn}}</text>
      </view>
      <view class="t">
        <text class="lable">下单时间：</text>
        <text class="value">{{orderInfo.addTime}}</text>
      </view>
      <view class="t">
        <text class="lable">收货人：</text>
        <text class="value">{{orderInfo.consignee}} {{orderInfo.mobile}}</text>
      </view>
      <view class="b">{{orderInfo.address}}</view>
  </view>


  <view class="order-books">
    <view class="h">
      <view class="label">图书信息</view>
      <view class="status">{{orderInfo.orderStatusText}}</view>
    </view>
    <view class="books">
      <view v-for="(item, index) in orderBooks" :key="index" class="item">
        <view class="img">
          <image :src="item.picUrl"></image>
        </view>
        <view class="info">
          <view class="t">
            <text class="name">{{item.booksName}}</text>
          </view>
          <view class="t">
            <text class="attr">{{item.specifications}}</text>
            <text class="number">x{{item.number}}</text>
          </view>
          <view class="price">￥{{item.price}}</view>
          <view class="btn active" v-if="handleOption.comment && (item.comment == 0)">
            <navigator :url="'../../commentPost/commentPost?orderId=' + item.orderId + '&&valueId=' + item.booksId + '&type=0'">去评价</navigator>
          </view>
          <view class="btn active" v-if="handleOption.rebuy">
            <navigator :url="'../../books/books?id=' + item.booksId">再次购买</navigator>
          </view>
        </view>
      </view>
    </view>

    <view class="order-bottom">
      <view class="total">
        <view class="t">
          <text class="label">图书合计：</text>
          <text class="txt">￥{{orderInfo.booksPrice}}</text>
        </view>
        <view class="t">
          <text class="label">运费：</text>
          <text class="txt">￥{{orderInfo.freightPrice}}</text>
        </view>
      </view>
      <view class="pay-fee">
        <text class="label">实付：</text>
        <text class="txt">￥{{orderInfo.actualPrice}}</text>
      </view>

    </view>

  </view>
</view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");

export default {
  data() {
    return {
      orderId: 0,
      orderInfo: {},
      orderBooks: [],
      flag: false,
      handleOption: {}
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      orderId: options.id
    });
    this.getOrderDetail();
  },

  onPullDownRefresh() {
    uni.showNavigationBarLoading(); //在标题栏中显示加载

    this.getOrderDetail();
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
    expandDetail: function () {
      let that = this;
      this.setData({
        flag: !that.flag
      });
    },
    getOrderDetail: function () {
      uni.showLoading({
        title: '加载中'
      });
      setTimeout(function () {
        uni.hideLoading();
      }, 2000);
      let that = this;
      util.request(api.OrderDetail, {
        orderId: that.orderId
      }).then(function (res) {
        if (res.errno === 0) {
          console.log(res.data);
          that.setData({
            orderInfo: res.data.orderInfo,
            orderBooks: res.data.orderBooks,
            handleOption: res.data.orderInfo.handleOption
          });
        }

        uni.hideLoading();
      });
    },
    // “去付款”按钮点击效果
    payOrder: function () {
      let that = this;
      util.request(api.OrderPrepay, {
        orderId: that.orderId
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          const payParam = res.data;
          console.log("支付过程开始");
          uni.requestPayment({
            'timeStamp': payParam.timeStamp,
            'nonceStr': payParam.nonceStr,
            'package': payParam.packageValue,
            'signType': payParam.signType,
            'paySign': payParam.paySign,
            'success': function (res) {
              console.log("支付过程成功");
              util.redirect('/pages/ucenter/order/order');
            },
            'fail': function (res) {
              console.log("支付过程失败");
              util.showErrorToast('支付失败');
            },
            'complete': function (res) {
              console.log("支付过程结束");
            }
          });
        }
      });
    },
    // “取消订单”点击效果
    cancelOrder: function () {
      let that = this;
      let orderInfo = that.orderInfo;
      uni.showModal({
        title: '',
        content: '确定要取消此订单？',
        success: function (res) {
          if (res.confirm) {
            util.request(api.OrderCancel, {
              orderId: orderInfo.id
            }, 'POST').then(function (res) {
              if (res.errno === 0) {
                uni.showToast({
                  title: '取消订单成功'
                });
                util.redirect('/pages/ucenter/order/order');
              } else {
                util.showErrorToast(res.errmsg);
              }
            });
          }
        }
      });
    },
    // “取消订单并退款”点击效果
    refundOrder: function () {
      let that = this;
      let orderInfo = that.orderInfo;
      uni.showModal({
        title: '',
        content: '确定要取消此订单？',
        success: function (res) {
          if (res.confirm) {
            util.request(api.OrderRefund, {
              orderId: orderInfo.id
            }, 'POST').then(function (res) {
              if (res.errno === 0) {
                uni.showToast({
                  title: '取消订单成功'
                });
                util.redirect('/pages/ucenter/order/order');
              } else {
                util.showErrorToast(res.errmsg);
              }
            });
          }
        }
      });
    },
    // “删除”点击效果
    deleteOrder: function () {
      let that = this;
      let orderInfo = that.orderInfo;
      uni.showModal({
        title: '',
        content: '确定要删除此订单？',
        success: function (res) {
          if (res.confirm) {
            util.request(api.OrderDelete, {
              orderId: orderInfo.id
            }, 'POST').then(function (res) {
              if (res.errno === 0) {
                uni.showToast({
                  title: '删除订单成功'
                });
                util.redirect('/pages/ucenter/order/order');
              } else {
                util.showErrorToast(res.errmsg);
              }
            });
          }
        }
      });
    },
    // “确认收货”点击效果
    confirmOrder: function () {
      let that = this;
      let orderInfo = that.orderInfo;
      uni.showModal({
        title: '',
        content: '确认收货？',
        success: function (res) {
          if (res.confirm) {
            util.request(api.OrderConfirm, {
              orderId: orderInfo.id
            }, 'POST').then(function (res) {
              if (res.errno === 0) {
                uni.showToast({
                  title: '确认收货成功！'
                });
                util.redirect('/pages/ucenter/order/order');
              } else {
                util.showErrorToast(res.errmsg);
              }
            });
          }
        }
      });
    }
  }
};
</script>
<style>
page {
  height: 100%;
  width: 100%;
  background: #f4f4f4;
}

.order-info {
  padding-top: 25rpx;
  background: #fff;
  height: auto;
  padding-left: 31.25rpx;
  overflow: hidden;
}

.item-a {
  padding-left: 31.25rpx;
  height: 42.5rpx;
  padding-bottom: 12.5rpx;
  line-height: 30rpx;
  font-size: 30rpx;
  color: #666;
}

.item-b {
  padding-left: 31.25rpx;
  height: 29rpx;
  line-height: 29rpx;
  margin-top: 12.5rpx;
  margin-bottom: 41.5rpx;
  font-size: 30rpx;
  color: #666;
}

.item-c {
  margin-left: 31.25rpx;
  border-top: 1px solid #f4f4f4;
  height: 103rpx;
  line-height: 103rpx;
}

.item-c .l {
  float: left;
}

.item-c .r {
  height: 103rpx;
  float: right;
  display: flex;
  align-items: center;
  padding-right: 16rpx;
}

.item-c .r .btn {
  float: right;
}

.item-c .cost {
  color: #b4282d;
}

.item-c .btn {
  line-height: 66rpx;
  border-radius: 5rpx;
  text-align: center;
  margin: 0 15rpx;
  padding: 0 20rpx;
  height: 66rpx;
}

.item-c .btn.active {
  background: #b4282d;
  color: #fff;
}

.order-books {
  margin-top: 20rpx;
  background: #fff;
}

.order-books .h {
  height: 93.75rpx;
  line-height: 93.75rpx;
  margin-left: 31.25rpx;
  border-bottom: 1px solid #f4f4f4;
  padding-right: 31.25rpx;
}

.order-books .h .label {
  float: left;
  font-size: 30rpx;
  color: #333;
}

.order-books .h .status {
  float: right;
  font-size: 30rpx;
  color: #b4282d;
}

.order-books .item {
  display: flex;
  align-items: top;
  height: 202rpx;
  margin-left: 31.25rpx;
  margin-bottom: 15.25rpx;
  padding-right: 31.25rpx;
  border-bottom: 1px solid #f4f4f4;
}

.order-books .item:last-child {
  border-bottom: none;
}

.order-books .item .img {
  height: 145.83rpx;
  margin-top: 15.5rpx;
  width: 145.83rpx;
  background: #f4f4f4;
}

.order-books .item .img image {
  height: 145.83rpx;
  width: 145.83rpx;
}

.order-books .item .info {
  flex: 1;
  height: 145.83rpx;
  margin-left: 20rpx;
}

.order-books .item .t {
  margin-top: 14rpx;
  height: 45rpx;
  line-height: 45rpx;
  margin-bottom: 8.5rpx;
}

.order-books .item .t .name {
  display: block;
  float: left;
  height: 33rpx;
  line-height: 33rpx;
  color: #333;
  font-size: 30rpx;
}

.order-books .item .t .number {
  display: block;
  float: right;
  height: 29rpx;
  text-align: right;
  line-height: 29rpx;
  color: #333;
  font-size: 30rpx;
}

.order-books .item .attr {
  height: 29rpx;
  line-height: 29rpx;
  color: #666;
  margin-bottom: 25rpx;
  font-size: 25rpx;
}

.order-books .item .price {
  display: block;
  float: left;
  height: 30rpx;
  line-height: 30rpx;
  color: #333;
  font-size: 30rpx;
}

.order-books .item .btn {
  height: 50rpx;
  line-height: 50rpx;
  border-radius: 40rpx;
  text-align: center;
  display: block;
  float: right;
  margin: 0 15rpx;
  padding: 0 20rpx;
}

.order-books .item .btn.active {
  background: #ee9e09;
  color: #fff;
  border-radius: 40rpx;
}

.order-bottom {
  margin-top: 20rpx;
  padding-left: 31.25rpx;
  height: auto;
  overflow: hidden;
  background: #fff;
}

.order-info .address {
  height: 128rpx;
  padding-top: 25rpx;
}

.order-info .t {
  height: 60rpx;
  line-height: 55rpx;
  margin-bottom: 7.5rpx;
}

.order-info .lable {
  display: inline-block;
  height: 35rpx;
  width: 220rpx;
  line-height: 35rpx;
  font-size: 30rpx;
}

.order-info .value {
  display: inline-block;
  height: 35rpx;
  line-height: 35rpx;
  font-size: 30rpx;
  color: #999;
}

.order-info .b {
  height: 50rpx;
  line-height: 40rpx;
  font-size: 30rpx;
  color: #999;
  padding-right: 10rpx;
  margin-bottom: 40rpx;
}

.order-bottom .total {
  height: 136rpx;
  padding-top: 20rpx;
  border-bottom: 1px solid #f4f4f4;
}

.order-bottom .total .t {
  height: 30rpx;
  line-height: 30rpx;
  margin-bottom: 7.5rpx;
}

.order-bottom .total .label {
  width: 250rpx;
  height: 35rpx;
  color: #999;
  line-height: 35rpx;
  font-size: 30rpx;
  float: left;
}

.order-bottom .total .txt {
  height: 35rpx;
  float: right;
  padding-right: 31.25rpx;
  line-height: 35rpx;
  font-size: 30rpx;
}

.order-bottom .pay-fee {
  height: 81rpx;
  line-height: 81rpx;
}

.order-bottom .pay-fee .label {
  width: 250rpx;
  color: #333;
  font-weight: bold;
  float: left;
}

.order-bottom .pay-fee .txt {
  float: right;
  padding-right: 31.25rpx;
  color: #b4282d;
}

.order-express {
  margin-top: 20rpx;
  width: 100%;
  height: 100rpx;
  background: #fff;
}

.order-express .title {
  float: left;
  margin-bottom: 20rpx;
  padding: 20rpx;
}

.order-express .ti {
  float: right;
  width: 52rpx;
  height: 52rpx;
  margin-right: 16rpx;
  margin-top: 28rpx;
}

.order-express .t {
  font-size: 29rpx;
  margin-left: 10.25rpx;
  color: #a78845;
}

.order-express .b {
  font-size: 29rpx;
  margin-left: 10.25rpx;
  color: #a78845;
}

.order-express .traces {
  padding: 17.5rpx;
  background: #fff;
  border-bottom: 1rpx solid #f1e6cdcc;
}

.order-express .trace {
  padding-bottom: 17.5rpx;
  padding-top: 17.5rpx;
  background: #fff;
}

.order-express .acceptTime {
  margin-top: 20rpx;
  margin-right: 40rpx;
  text-align: right;
  font-size: 26rpx;
}

.order-express .acceptStation {
  font-size: 26rpx;
}

</style>
