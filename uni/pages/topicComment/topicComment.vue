<template>
  <view class="comments">
    <view class="b">
      <view v-for="(item, index) in comments" :key="index" class="item">
        <view class="info">
          <view class="user">
            <image :src="item.userInfo.avatarUrl"></image>
            <text>{{ item.userInfo.nickName }}</text>
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


          <view class="time">{{ item.addTime }}</view>
        </view>
        <view class="comment">{{ item.content }}</view>
        <view class="imgs" v-if="item.picList.length > 0">
          <image
            v-for="(pitem, index2) in item.picList"
            :key="index2"
            class="img"
            :src="pitem.picUrl"
          ></image>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
var app = getApp();
var util = require("../../utils/util.js");
var api = require("../../config/api.js");

export default {
  data() {
    return {
      comments: [],
      allCommentList: [],
      picCommentList: [],
      stars: [1, 2, 3, 4, 5],
      star: 5,
      type: 0,
      valueId: 0,
      showType: 0,
      allCount: 0,
      hasPicCount: 0,
      allPage: 1,
      picPage: 1,
      size: 20,
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      type: options.type,
      valueId: options.valueId,
    });
    this.getCommentCount();
    this.getCommentList();
  },
  onReady: function () {
    // 页面渲染完成
  },
  onShow: function () {
    // 页面显示
  },
  onHide: function () {
    // 页面隐藏
  },
  onUnload: function () {
    // 页面关闭
  },
  onReachBottom: function () {
    console.log("onPullDownRefresh");

    if (this.showType == 0) {
      if (this.allCount / this.size < this.allPage) {
        return false;
      }

      this.setData({
        allPage: this.allPage + 1,
      });
    } else {
      if (this.hasPicCount / this.size < this.picPage) {
        return false;
      }

      this.setData({
        picPage: this.picPage + 1,
      });
    }

    this.getCommentList();
  },
  methods: {
    getCommentCount: function () {
      let that = this;
      util
        .request(api.CommentCount, {
          valueId: that.valueId,
          type: that.type,
        })
        .then(function (res) {
          if (res.errno === 0) {
            that.setData({
              allCount: res.data.allCount,
              hasPicCount: res.data.hasPicCount,
            });
          }
        });
    },
    getCommentList: function () {
      let that = this;
      util
        .request(api.CommentList, {
          valueId: that.valueId,
          type: that.type,
          size: that.size,
          page: that.showType == 0 ? that.allPage : that.picPage,
          showType: that.showType,
        })
        .then(function (res) {
          if (res.errno === 0) {
            if (that.showType == 0) {
              that.setData({
                allCommentList: that.allCommentList.concat(res.data.data),
                allPage: res.data.currentPage,
                comments: that.allCommentList.concat(res.data.data),
              });
              console.log(that.comments);
            } else {
              that.setData({
                picCommentList: that.picCommentList.concat(res.data.data),
                picPage: res.data.currentPage,
                comments: that.picCommentList.concat(res.data.data),
              });
            }
          }
        });
    },
    switchTab: function () {
      this.setData({
        showType: this.showType == 1 ? 0 : 1,
      });
      this.getCommentList();
    },
  },
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

.comments {
  width: 100%;
  height: auto;
  padding-left: 30rpx;
  background: #fff;
  margin: 20rpx 0;
}

.comments .b {
  height: auto;
  width: 720rpx;
}

.comments .b.no-h {
  margin-top: 0;
}

.comments .item {
  height: auto;
  width: 720rpx;
  overflow: hidden;
  border-bottom: 1px solid #d9d9d9;
  padding-bottom: 25rpx;
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

.comments .user image {
  float: left;
  width: 67rpx;
  height: 67rpx;
  margin-right: 17rpx;
  border-radius: 50%;
}

.comments .user text {
  display: inline-block;
  width: auto;
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
  font-size: 29rpx;
  margin-bottom: 16rpx;
}

.comments .imgs {
  width: 720rpx;
  height: 150rpx;
  margin-bottom: 25rpx;
}

.comments .imgs .img {
  height: 150rpx;
  width: 150rpx;
  margin-right: 28rpx;
}

.comments .customer-service {
  width: 690rpx;
  height: auto;
  overflow: hidden;
  margin-top: 23rpx;
  background: rgba(0, 0, 0, 0.03);
  padding: 21rpx;
}

.comments .customer-service .u {
  font-size: 24rpx;
  color: #333;
  line-height: 37.5rpx;
}

.comments .customer-service .c {
  font-size: 24rpx;
  color: #999;
  line-height: 37.5rpx;
}
</style>
