<template>
<view class="container">
  <view class="form-box">

    <button type="primary" class="Edit-btn" @click="tapdetail">请前往后台管理系统发布书籍</button>

  </view>
</view>
</template>

<script>
export default {
  data() {
    return {
      url: 'http://hazesky.fun/'
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {

  },
  onReady: function () {},
  onShow: function () {},
  onHide: function () {
  },
  onUnload: function () {
  },
  methods: {
    // #ifdef H5
    tapdetail: function() {
      window.location.href = `http://hazesky.fun/`;
    },
    // #endif

    // #ifdef APP-PLUS
    tapdetail: function() {
      plus.runtime.openURL(`http://hazesky.fun/`);
    },
    // #endif

    // #ifdef MP-WEIXIN
    tapdetail: function() {
      uni.navigateTo({
			  url: '/pages/sell/to?url='+ this.url
			})
    },
    // #endif

  },
};
</script>
<style>

.page, .container {
  height: 100%;
  background: #f4f4f4;
}

.Edit-btn{background-color:#388ceb;font-size:32rpx;color:#fff;width:690rpx;height:90rpx;border-radius:50rpx;text-align:center;line-height:90rpx;margin:76rpx auto 0 auto;}
</style>