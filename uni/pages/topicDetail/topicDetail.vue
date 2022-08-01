<template>
<view>
<scroll-view class="container">
  <scroll-view class="content">
    <!--<import src="../../lib/wxParse/wxParse.wxml"></import>-->
    <jyf-parser :html="article_topicDetail"></jyf-parser>
  </scroll-view>
  <scroll-view class="sv-books" v-if="topicBooks.length > 0">
    <view class="topic-books">
      <view class="b">
        <view v-for="(item, index) in topicBooks" :key="index" class="item">
          <navigator :url="'/pages/books/books?id=' + item.id">
            <image class="img" :src="item.picUrl" background-size="cover"></image>
            <view class="right">
              <view class="text">
                <text class="name jnbook_f7">{{item.name}}</text>
                <text class="desc jnbook_f5">{{item.brief}}</text>
                <view class="price">
                  <text class="retailPrice jnbook_f8">￥{{item.retailPrice}}</text>
                  <text class="counterPrice jnbook_f9">￥{{item.counterPrice}}</text>
                </view>
                <!-- <text class="price jnbook_f8">￥{{item.retailPrice}}</text> -->
              </view>
            </view>
          </navigator>
        </view>
      </view>
    </view>
  </scroll-view>
  <scroll-view class="comments">
    <view class="h">
      <text class="t">精选留言</text>
      <image @tap="postComment" class="i" src="http://nos.netease.com/mailpub/hxm/yanxuan-wap/p/20150730/style/img/icon-normal/comment-add-2aca147c3f.png"></image>
    </view>
    <view class="has-comments" v-if="commentList.length > 0">
      <view class="b">
        <view v-for="(item, index) in commentList" :key="index" class="item">
          <view class="info">
            <view class="user">
              <image class="avatar" :src="item.userInfo.avatarUrl"></image>
              <text class="nickname">{{item.userInfo.nickName}}</text>
            </view>
            <view class="rater">
            <block v-for="(item1, index) in stars" :key="index">
              <image
                src="/static/static/images/icon_star_checked.png"
                v-if="item1 <= item.star"
              ></image>
              <image src="/static/static/images/icon_star.png" v-else></image>
            </block>
          </view>
            <view class="time">{{item.addTime}}</view>
          </view>
          <view class="comment">
            {{item.content}}
          </view>
        </view>
      </view>
      <view class="load" v-if="commentCount > 5">
        <navigator :url="'/pages/topicComment/topicComment?valueId=' + topic.id + '&type=1'">查看更多</navigator>
      </view>
    </view>
    <view class="no-comments" v-if="commentList.length <= 0">
      <view class="b">
        <image class="icon" src="http://yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/icon-normal/no-comment-560f87660a.png"></image>
        <text class="txt">等你来留言</text>
      </view>
    </view>
  </scroll-view>
  <scroll-view class="rec-box">
    <view class="h">
      <text class="txt">活动推荐</text>
    </view>
    <view class="b">
      <navigator v-for="(item, index) in topicList" :key="index" class="item" :url="'../topicDetail/topicDetail?id=' + item.id">
        <image class="img" :src="item.picUrl"></image>
        <text class="title">{{item.title}}</text>
      </navigator>
    </view>
  </scroll-view>
</scroll-view>
</view>
</template>

<script>
var app = getApp();

var util = require("../../utils/util.js");
var api = require("../../config/api.js");

export default {
  data() {
    return {
      id: 0,
      topic: {},
      topicList: [],
      commentCount: 0,
      stars: [1, 2, 3, 4, 5],
      star: 5,
      commentList: [],
      topicBooks: [],
      article_topicDetail: ""
    };
  },

  components: {},
  props: {},
  onShareAppMessage: function () {
    let that = this;
    let userInfo = uni.getStorageSync('userInfo');
    let shareUserId = 1;

    if (userInfo) {
      shareUserId = userInfo.userId;
    }

    console.log('/pages/index/index?scene=topic,' + that.topic.id + '&shareUserId=' + shareUserId);
    return {
      title: that.topic.subtitle,
      path: '/pages/index/index?scene=topic,' + that.topic.id + '&shareUserId=' + shareUserId
    };
  },
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    var that = this;
    that.setData({
      id: options.id
    });
    util.request(api.TopicDetail, {
      id: that.id
    }).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          topic: res.data.topic,
          topicBooks: res.data.books
        });
        //WxParse.wxParse('topicDetail', 'html', res.data.topic.content, that)
        setTimeout(() => {
          that.article_topicDetail = res.data.topic.content;
        }, 200);
      }
    });
    util.request(api.TopicRelated, {
      id: that.id
    }).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          topicList: res.data
        });
      }
    });
  },
  onReady: function () {},
  onShow: function () {
    // 页面显示
    this.getCommentList();
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    getCommentList() {
      let that = this;
      util.request(api.CommentList, {
        valueId: that.id,
        type: 1,
        showType: 0,
        page: 1,
        size: 5
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            commentList: res.data.data,
            commentCount: res.data.count
          });
        }
      });
    },

    postComment() {
      if (!app.globalData.hasLogin) {
        uni.navigateTo({
          url: "/pages/auth/login/login"
        });
      } else {
        uni.navigateTo({
          url: '/pages/topicCommentPost/topicCommentPost?valueId=' + this.id + '&type=1'
        });
      }
    }

  }
};
</script>
<style>

.info .rater {
  display: flex;
  flex-direction: row;
  height: 55rpx;
}

.info .rater .rater-title {
  font-size: 29rpx;
  padding-right: 10rpx;
}

.info .rater image {
  padding-left: 5rpx;
  height: 30rpx;
  width: 30rpx;
}

.info .rater .rater-desc {
  font-size: 29rpx;
  padding-left: 10rpx;
}

.content {
  width: 100%;
  height: auto;
  font-size: 0;
}

.content image {
  display: inline-block;
  width: 100%;
}

.comments {
  width: 100%;
  height: auto;
  padding-left: 30rpx;
  background: #fff;
  margin-top: 20rpx;
}

.comments .h {
  height: 93rpx;
  line-height: 93rpx;
  width: 720rpx;
  padding-right: 30rpx;
  border-bottom: 1px solid #d9d9d9;
}

.comments .h .t {
  display: block;
  float: left;
  width: 50%;
  font-size: 29rpx;
  color: #333;
}

.comments .h .i {
  display: block;
  float: right;
  margin-top: 30rpx;
  width: 33rpx;
  height: 33rpx;
}

.comments .b {
  height: auto;
  width: 720rpx;
}

.comments .item {
  height: auto;
  width: 720rpx;
  overflow: hidden;
  border-bottom: 1px solid #d9d9d9;
}

.comments .info {
  height: 127rpx;
  width: 100%;
  padding: 33rpx 0 27rpx 0;
}

.comments .user {
  float: left;
  width: auto;
  height: 67rpx;
  line-height: 67rpx;
  font-size: 0;
}

.comments .user .avatar {
  display: block;
  float: left;
  width: 67rpx;
  height: 67rpx;
  margin-right: 17rpx;
  border-radius: 50%;
}

.comments .user .nickname {
  display: block;
  width: auto;
  float: left;
  height: 66rpx;
  overflow: hidden;
  font-size: 29rpx;
  line-height: 66rpx;
}

.comments .time {
  display: block;
  float: right;
  width: auto;
  height: 67rpx;
  line-height: 67rpx;
  color: #7f7f7f;
  font-size: 25rpx;
  margin-right: 30rpx;
}

.comments .comment {
  width: 720rpx;
  padding-right: 30rpx;
  line-height: 45.8rpx;
  margin-bottom: 30rpx;
  font-size: 29rpx;
  color: #333;
}

.comments .load {
  width: 720rpx;
  height: 108rpx;
  line-height: 108rpx;
  text-align: center;
  font-size: 38.5rpx;
}

.no-comments {
  height: 297rpx;
}

.no-comments .txt {
  height: 43rpx;
  line-height: 43rpx;
  display: block;
  width: 100%;
  text-align: center;
  font-size: 29rpx;
  color: #7f7f7f;
}

.no-comments .icon {
  margin: 48rpx auto 18rpx auto;
  height: 130rpx;
  display: block;
  width: 115rpx;
}

.sv-books {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  background: #fff
}

.sv-books .b .item {
  border-bottom: 1px solid #f4f4f4;
  margin: 0 10rpx;
  margin-bottom: -2rpx;
  height: 270rpx;
  width: 690rpx;
}

.sv-books .b .img {
  margin-top: 10rpx;
  margin-right: 15rpx;
  float: left;
  width: 230rpx;
  height: 230rpx;
}

.sv-books .b .right {
  float: left;
  height: 264rpx;
  width: 436rpx;
  display: flex;
  flex-flow: row nowrap;
}

.sv-books .b .text {
  display: flex;
  flex-wrap: nowrap;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
  height: 264rpx;
  width: 436rpx;
}

.sv-books .b .name {
  width: 436rpx;
  display: block;
  line-height: 40rpx;
  display: -webkit-box;
 -webkit-box-orient: vertical;
 -webkit-line-clamp: 2;
  overflow: hidden;
}

.sv-books .b .desc {
  margin-top: 18rpx;
  width: 436rpx;
  display: block;
  line-height: 38rpx;
  display: -webkit-box;
 -webkit-box-orient: vertical;
 -webkit-line-clamp: 2;
 overflow: hidden;
}

.sv-books .b .price {
  margin-top: 18rpx;
  width: 436rpx;
  display: block;
  line-height: 30rpx;
}

.sv-books .text .price {
  height: 70rpx;
  align-content: center;
}

.sv-books .text .counterPrice {
  padding-left: 5%;
  text-decoration: line-through;
}

.sv-books .text  .retailPrice {
  float: left;
  padding-left: 0rpx;
}

.rec-box {
  width: 690rpx;
  height: auto;
  margin: 0 30rpx;
}

.rec-box .h {
  position: relative;
  width: 690rpx;
  height: 96rpx;
  /*border-bottom: 1px solid #d0d0d0;*/
  margin-bottom: 32rpx;
}

.rec-box .h .txt {
  display: inline-block;
  position: absolute;
  background: #f4f4f4;
  top: 59rpx;
  left: 200rpx;
  width: 290rpx;
  height: 45rpx;
  line-height: 45rpx;
  font-size: 30rpx;
  color: #999;
  text-align: center;
}

.rec-box .b .item {
  width: 690rpx;
  height: 397rpx;
  padding: 24rpx 24rpx 30rpx 24rpx;
  background: #fff;
  margin-bottom: 30rpx;
}

.rec-box .b .item .img {
  height: 278rpx;
  width: 642rpx;
}

.rec-box .b .item .title {
  display: block;
  margin-top: 30rpx;
  height: 30rpx;
  width: 642rpx;
  font-size: 28rpx;
}


</style>
