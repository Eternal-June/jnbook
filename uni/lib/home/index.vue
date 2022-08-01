<template>
<view class="home" :style="' top:' + (top*2) + 'rpx;'" catchtouchmove="setTouchMove">
   
        <view :class="'homeCon bg-color acea-row row-column-between ' + (homeActive==true?'on':'')">
          <navigator hover-class="none" url="/pages/index/index" open-type="switchTab" class="iconfont icon-shouye-xianxing"></navigator>
          <navigator hover-class="none" url="/pages/cart/cart" open-type="switchTab" class="iconfont icon-caigou-xianxing"></navigator>
          <navigator hover-class="none" url="/pages/ucenter/index/index" open-type="switchTab" class="iconfont icon-yonghu1"></navigator>
      </view>
   <view class="pictrue" @tap="open"><image :src="homeActive==false?'/static/images/close.gif':'/static/images/open.gif'"></image></view>
</view>
</template>

<script>

export default {
  data() {
    return {
      homeActive: false,
      top: ""
    };
  },

  components: {},
  props: {},

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {},

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {},

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {},

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {},

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {},

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {},

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {},

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {},
  methods: {
    setTouchMove: function (e) {
      var that = this;

      if (e.touches[0].clientY < 400 && e.touches[0].clientY > 66) {
        that.setData({
          top: e.touches[0].clientY
        });
      }
    },
    open: function () {
      this.setData({
        homeActive: !this.homeActive
      });
    }
  }
};
</script>
<style>

.home{position: fixed ;top:580rpx;color: white;text-align: center;z-index:99;right:10rpx;}
.home .homeCon{width:86rpx;border-radius:50rpx;padding:34rpx 0;box-sizing:border-box;transition:all 0.3s ease-in-out 0s;opacity:0;transform: scale(0);height:0rpx;color:#e93323;}
.home .homeCon.on{opacity:1;transform: scale(1);height:300rpx;color:#fff;}
.home .homeCon .iconfont{font-size:48rpx;}
.home .pictrue{width:86rpx;height:86rpx;border-radius:50%;margin-top:20rpx;}
.home .pictrue image{width:100%;height:100%;border-radius:50%;}

</style>