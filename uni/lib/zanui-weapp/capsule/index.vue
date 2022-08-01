<template>
<view :class="'custom-class zan-capsule zan-capsule--' + type">
 <block v-if="color">
  <view class="zan-capsule__left" :style="'background: ' + color + '; border-color: ' + color">{{ leftText }}</view>
  <view class="zan-capsule__right" :style="'color: ' + color + '; border-color: ' + color">{{ rightText }}</view>
 </block>
 <block v-else>
  <view class="zan-capsule__left">{{ leftText }}</view>
  <view class="zan-capsule__right">{{ rightText }}</view>
 </block>
</view>
</template>

<script>
'use strict';

export default {
  data() {
    return {};
  },

  components: {},
  props: {
    // 颜色状态
    type: {
      type: String,
      default: ''
    },
    // 自定义颜色
    color: {
      type: String,
      default: ''
    },
    // 左侧内容
    leftText: {
      type: String,
      default: ''
    },
    // 右侧内容
    rightText: {
      type: String,
      default: ''
    }
  },
  externalClasses: ['custom-class'],
  methods: {}
};
</script>
<style>
.zan-capsule {
 display: inline-block;
 font-size: 12px;
 vertical-align: middle;
 line-height: 19px;
 -webkit-transform: scale(0.83);
 transform: scale(0.83);
}

.zan-capsule__left, .zan-capsule__right {
 display: inline-block;
 line-height: 17px;
 height: 19px;
 vertical-align: middle;
 box-sizing: border-box;
}

.zan-capsule__left {
 padding: 0 2px;
 color: #fff;
 background: #999;
 border-radius: 2px 0 0 2px;
 border: 1rpx solid #999;
}

.zan-capsule__right {
 padding: 0 5px;
 color: #999;
 border-radius: 0 2px 2px 0;
 border: 1rpx solid #999;
}

.zan-capsule--danger .zan-capsule__left {
 color: #fff;
 background: #f24544;
 border-color: #f24544;
}

.zan-capsule--danger .zan-capsule__right {
 color: #f24544;
 border-color: #f24544;
}

</style>