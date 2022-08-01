<template>
  <view class="container">
    <view class="address-list" v-if="addressList.length > 0">
      <view
        v-for="(item, index) in addressList"
        :key="index"
        class="item"
        @tap="addressAddOrUpdate"
        :data-address-id="item.id"
      >
        <view class="l">
          <view class="name">{{ item.name }}</view>
          <view class="default" v-if="item.isDefault">默认</view>
        </view>
        <view class="c">
          <view class="mobile">{{ item.mobile }}</view>
          <view class="address">{{ item.detailedAddress }}</view>
        </view>
        <view class="r">
          <image
            @tap.stop="deleteAddress"
            :data-address-id="item.id"
            class="del"
            src="/static/static/images/del-address.png"
          ></image>
        </view>
      </view>
    </view>
    <view class="empty-view" v-if="addressList.length <= 0">
      <image class="icon" src="/static/static/images/noAddress.png"></image>
      <text class="text">收货地址在哪里</text>
    </view>
    <view class="add-address" @tap="addressAddOrUpdate" data-address-id="0">新建</view>
  </view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var app = getApp();

export default {
  data() {
    return {
      addressList: [],
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
    this.getAddressList();
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },
  methods: {
    getAddressList() {
      let that = this;
      util.request(api.AddressList).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            addressList: res.data,
          });
        }
      });
    },

    addressAddOrUpdate(event) {
      console.log(event); //返回之前，先取出上一页对象，并设置addressId

      var pages = getCurrentPages();
      var prevPage = pages[pages.length - 2];

      if (prevPage.route == "pages/checkout/checkout") {
        try {
          uni.setStorageSync("addressId", event.currentTarget.dataset.addressId);
        } catch (e) {}

        let addressId = event.currentTarget.dataset.addressId;

        if (addressId && addressId != 0) {
          uni.navigateBack();
        } else {
          uni.navigateTo({
            url: "/pages/ucenter/addressAdd/addressAdd?id=" + addressId,
          });
        }
      } else {
        uni.navigateTo({
          url:
            "/pages/ucenter/addressAdd/addressAdd?id=" +
            event.currentTarget.dataset.addressId,
        });
      }
    },

    deleteAddress(event) {
      console.log(event.target);
      let that = this;
      uni.showModal({
        title: "",
        content: "确定要删除地址？",
        success: function (res) {
          if (res.confirm) {
            let addressId = event.target.dataset.addressId;
            util
              .request(
                api.AddressDelete,
                {
                  id: addressId,
                },
                "POST"
              )
              .then(function (res) {
                if (res.errno === 0) {
                  that.getAddressList();
                  uni.removeStorage({
                    key: "addressId",
                    success: function (res) {},
                  });
                }
              });
            console.log("用户点击确定");
          }
        },
      });
      return false;
    },
  },
};
</script>
<style>
page {
  height: 100%;
  width: 100%;
  background: #f4f4f4;
}

.container {
  height: 100%;
  width: 100%;
}


.address-list {
  padding-left: 31.25rpx;
  background: #fff
    url("data:image/png;base64,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")
    0 0 repeat-x;
  background-size: auto 10.5rpx;
  margin-bottom: 10rpx;
}

.address-list .item {
  height: 156.55rpx;
  align-items: center;
  display: flex;
  border-bottom: 1rpx solid #dcd9d9;
}

.address-list .l {
  width: 125rpx;
  height: 80rpx;
  overflow: hidden;
}

.address-list .name {
  width: 125rpx;
  height: 43rpx;
  font-size: 29rpx;
  color: #333;
  margin-bottom: 5.2rpx;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.address-list .default {
  width: 62.5rpx;
  height: 33rpx;
  line-height: 28rpx;
  text-align: center;
  font-size: 20rpx;
  color: #b4282d;
  border: 1rpx solid #b4282d;
  visibility: visible;
}

.address-list .c {
  flex: 1;
  height: auto;
  overflow: hidden;
}

.address-list .mobile {
  height: 29rpx;
  font-size: 29rpx;
  line-height: 29rpx;
  overflow: hidden;
  color: #333;
  margin-bottom: 6.25rpx;
}

.address-list .address {
  height: 37rpx;
  font-size: 25rpx;
  line-height: 37rpx;
  overflow: hidden;
  color: #666;
}

.address-list .r {
  width: 52rpx;
  height: auto;
  overflow: hidden;
  margin-right: 16.5rpx;
}

.address-list .del {
  display: block;
  width: 52rpx;
  height: 52rpx;
}

.add-address {
  background-color: #388ceb;
  font-size: 32rpx;
  color: #fff;
  width: 690rpx;
  height: 90rpx;
  border-radius: 50rpx;
  text-align: center;
  line-height: 90rpx;
  margin: 76rpx auto 0 auto;
}

.empty-view {
  height: 60%;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-view .icon {
  height: 248rpx;
  width: 258rpx;
  margin-bottom: 10rpx;
}

.empty-view .text {
  width: auto;
  font-size: 28rpx;
  line-height: 35rpx;
  color: #999;
}
</style>
