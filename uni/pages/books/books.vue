<template>
<view>
<view class="container">
  <swiper class="booksimgs" indicator-dots="true" autoplay="true" interval="3000" duration="1000">
    <swiper-item v-for="(item, index) in books.gallery" :key="index">
      <image :src="item" background-size="cover"></image>
    </swiper-item>
  </swiper>
  <!-- 分享 -->
  <view class="books_name">
    <view class="books_name_left jnbook_f10">{{books.name}}</view>
    <view class="books_name_right iconfont icon-fenxiang" @tap="shareFriendOrCircle">联系他/她</view>
  </view>

  <view class="books-info">
    <view class="c">
      <text class="desc jnbook_f11">{{books.brief}}</text>
      <view class="price">
        <view class="retailPrice jnbook_f8">￥{{checkedSpecPrice}}</view>
        <view class="counterPrice jnbook_f9">￥{{books.counterPrice}}</view>
      </view>
    </view>
  </view>

  <view class="section-nav section-attr" @tap="switchAttrPop">
    <view class="t jnbook_f4">{{checkedSpecText}}</view>
    <image class="i jnbook_f6" src="/static/static/images/address_right.png" background-size="cover"></image>
  </view>
  <view class="comments" v-if="comment.count > 0">
    <view class="h">
      <navigator :url="'/pages/comment/comment?valueId=' + books.id + '&type=0'">
        <text class="t jnbook_f4">评价({{comment.count > 999 ? '999+' : comment.count}})</text>
        <text class="i jnbook_f6">查看全部</text>
      </navigator>
    </view>
    <view class="b">
      <view v-for="(item, index) in comment.data" :key="index" class="item">
        <view class="info">
          <view class="user">
            <image :src="item.avatar"></image>
            <text>{{item.nickname}}</text>
          </view>
          <view class="time">{{item.addTime}}</view>
        </view>
        <view class="content">
          {{item.content}}
        </view>
        <view class="imgs" v-if="item.picList.length > 0">
          <image v-for="(iitem, index2) in item.picList" :key="index2" class="img" :src="iitem + ' '"></image>
        </view>
      </view>
    </view>
  </view>
  <view class="books-attr">
    <view class="t jnbook_f4">图书参数</view>
    <view class="l">
      <view v-for="(item, index) in attribute" :key="index" class="item">
        <text class="left">{{item.attribute}}</text>
        <text class="right">{{item.value}}</text>
      </view>
    </view>
  </view>

  <view class="detail">
    <jyf-parser :html="article_booksDetail"></jyf-parser>
  </view>

  <view class="common-problem">
    <view class="h">
      <view class="line"></view>
      <text class="title">常见问题</text>
    </view>
    <view class="b">
      <view v-for="(item, index) in issueList" :key="index" class="item">
        <view class="question-box">
          <text class="spot"></text>
          <text class="question">{{item.question}}</text>
        </view>
        <view class="answer">
          {{item.answer}}
        </view>
      </view>
    </view>
  </view>

  <!-- 大家都在看 -->
  <view class="related-books" v-if="relatedBooks.length > 0">
    <view class="h">
      <view class="line"></view>
      <text class="title">大家都在看</text>
    </view>
    <view class="b">
      <view v-for="(item, index) in relatedBooks" :key="index" class="item">
        <navigator :url="'/pages/books/books?id=' + item.id">
          <image class="img" :src="item.picUrl" background-size="cover"></image>
          <text class="name jnbook_f7">{{item.name}}</text>
          <text class="price jnbook_f8">￥{{item.retailPrice}}</text>
        </navigator>
      </view>
    </view>
  </view>
</view>
<!-- 规格选择界面 -->
<view class="attr-pop-box" :hidden="!openAttr">
  <view class="attr-pop">
    <view class="close" @tap="closeAttr">
      <image class="icon" src="/static/static/images/icon_close.png"></image>
    </view>
    <view class="img-info">
      <image class="img" :src="checkSpecificationUrl || books.picUrl"></image>
      <view class="info">
        <view class="c">
          <view class="p">价格：￥{{checkedSpecPrice}}</view>
          <view class="a">{{tmpSpecText}}</view>
        </view>
      </view>
    </view>

    <!-- 规格列表 -->
    <view class="spec-con">
      <view v-for="(item, index) in specificationList" :key="index" class="spec-item">
        <view class="name">{{item.name}}</view>
        <view class="values">
          <view v-for="(vitem, index2) in item.valueList" :key="index2" :class="'value ' + (vitem.checked ? 'selected' : '')" @tap="clickSkuValue" :data-value-id="vitem.id" :data-name="vitem.specification">{{vitem.value}}</view>
        </view>
      </view>

      <!-- 数量 -->
      <view class="number-item">
        <view class="name">数量</view>
        <view class="selnum">
          <view class="cut" @tap="cutNumber">-</view>
          <input :value="number" class="number" disabled="true" type="number"></input>
          <view class="add" @tap="addNumber">+</view>
        </view>
      </view>


    </view>
  </view>
</view>

<!-- 底部按钮 -->
<view class="bottom-btn">
  <view class="l l-collect" @tap="addCollectOrNot">
    <image class="icon" :src="collectImage"></image>
  </view>
  <view class="r" @tap="addToCart" v-if="!soldout">加入购物车</view>
  <view class="c" @tap="addFast" v-if="!soldout">立即购买</view>
  <view class="n" v-if="soldout">图书已售空</view>
</view>
</view>
</template>

<script>
var app = getApp();

var util = require("../../utils/util.js");
var api = require("../../config/api.js");
var user = require("../../utils/user.js");

export default {
  data() {
    return {
      id: 0,
      books: {},
      attribute: [],
      issueList: [],
      comment: [],
      specificationList: [],
      productList: [],
      relatedBooks: [],
      cartBooksCount: 0,
      userHasCollect: 0,
      number: 1,
      checkedSpecText: '规格数量选择',
      tmpSpecText: '请选择规格数量',
      checkedSpecPrice: 0,
      openAttr: false,
      noCollectImage: "/static/static/images/icon_like.png",
      hasCollectImage: "/static/static/images/icon_like_checked.png",
      collectImage: "/static/static/images/icon_like.png",
      shareImage: '',
      soldout: false,
      canWrite: false //用户是否获取了保存相册的权限
      ,
      checkSpecificationUrl: "",
      article_booksDetail: ""
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    if (options.id) {
      this.setData({
        id: parseInt(options.id)
      });
      this.getBooksInfo();
    }

    let that = this;
    uni.getSetting({
      success: function (res) {
        console.log(res); //不存在相册授权

        if (!res.authSetting['scope.writePhotosAlbum']) {
          uni.authorize({
            scope: 'scope.writePhotosAlbum',
            success: function () {
              that.setData({
                canWrite: true
              });
            },
            fail: function (err) {
              that.setData({
                canWrite: false
              });
            }
          });
        } else {
          that.setData({
            canWrite: true
          });
        }
      }
    });
  },
  onShow: function () {
    // 页面显示
    var that = this;
    util.request(api.CartBooksCount).then(function (res) {
      if (res.errno === 0) {
        that.setData({
          cartBooksCount: res.data
        });
      }
    });
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  onReady: function () {// 页面渲染完成
  },

  // 下拉刷新
  onPullDownRefresh() {
    uni.showNavigationBarLoading(); //在标题栏中显示加载

    this.getBooksInfo();
    uni.hideNavigationBarLoading(); //完成停止加载

    uni.stopPullDownRefresh(); //停止下拉刷新
  },

  methods: {
    shareFriendOrCircle: function () {
      console.log(this.books);
            uni.showModal({
              title: '他/她的邮箱',
              content: this.books.email,
              showCancel: false
            });
    },
    // 获取图书信息
    getBooksInfo: function () {
      let that = this;
      util.request(api.BooksDetail, {
        id: that.id
      }).then(function (res) {
        if (res.errno === 0) {
          let _specificationList = res.data.specificationList; // 如果仅仅存在一种货品，那么图书页面初始化时默认checked

          if (_specificationList.length == 1) {
            if (_specificationList[0].valueList.length == 1) {
              _specificationList[0].valueList[0].checked = true; // 如果仅仅存在一种货品，那么图书价格应该和货品价格一致
              // 这里检测一下

              let _productPrice = res.data.productList[0].price;
              let _booksPrice = res.data.info.retailPrice;

              if (_productPrice != _booksPrice) {
                console.error('图书数量价格和货品不一致');
              }

              that.setData({
                checkedSpecText: _specificationList[0].valueList[0].value,
                tmpSpecText: '已选择：' + _specificationList[0].valueList[0].value
              });
            }
          }

          that.setData({
            books: res.data.info,
            attribute: res.data.attribute,
            issueList: res.data.issue,
            comment: res.data.comment,
            specificationList: res.data.specificationList,
            productList: res.data.productList,
            userHasCollect: res.data.userHasCollect,
            shareImage: res.data.shareImage,
            checkedSpecPrice: res.data.info.retailPrice,
          });

          if (res.data.userHasCollect == 1) {
            that.setData({
              collectImage: that.hasCollectImage
            });
          } else {
            that.setData({
              collectImage: that.noCollectImage
            });
          }

          setTimeout(() => {
            that.article_booksDetail = res.data.info.detail;
          }, 200); //获取推荐图书

          that.getBooksRelated();
        }
      });
    },
    // 获取推荐图书
    getBooksRelated: function () {
      let that = this;
      util.request(api.BooksRelated, {
        id: that.id
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            relatedBooks: res.data.booksList
          });
        }
      });
    },

    // 规格选择
    clickSkuValue: function (event) {
      let that = this;
      let specName = event.currentTarget.dataset.name;
      let specValueId = event.currentTarget.dataset.valueId; //判断是否可以点击
      //TODO 性能优化，可在wx:for中添加index，可以直接获取点击的属性名和属性值，不用循环

      let _specificationList = this.specificationList;

      for (let i = 0; i < _specificationList.length; i++) {
        if (_specificationList[i].name === specName) {
          for (let j = 0; j < _specificationList[i].valueList.length; j++) {
            if (_specificationList[i].valueList[j].id == specValueId) {
              //如果已经选中，则反选
              if (_specificationList[i].valueList[j].checked) {
                _specificationList[i].valueList[j].checked = false;
              } else {
                _specificationList[i].valueList[j].checked = true; //选择了，则判断当前是否有图片，且图片内容不能为空，不能为默认lazy图片，则替换

                that.setSpecificationUrl(_specificationList[i].valueList[j].picUrl);
              }
            } else {
              _specificationList[i].valueList[j].checked = false;
            }
          }
        }
      }

      this.setData({
        specificationList: _specificationList
      }); //重新计算spec改变后的信息

      this.changeSpecInfo(); //重新计算哪些值不可以点击
    },
    setSpecificationUrl: function (picUrl) {
      if (picUrl && picUrl.length > 1 && picUrl.indexOf("lazyload.png") == -1) this.setData({
        checkSpecificationUrl: picUrl
      });
    },
    //获取选中的规格信息
    getCheckedSpecValue: function () {
      let checkedValues = [];
      let _specificationList = this.specificationList;

      for (let i = 0; i < _specificationList.length; i++) {
        let _checkedObj = {
          name: _specificationList[i].name,
          valueId: 0,
          valueText: ''
        };

        for (let j = 0; j < _specificationList[i].valueList.length; j++) {
          if (_specificationList[i].valueList[j].checked) {
            _checkedObj.valueId = _specificationList[i].valueList[j].id;
            _checkedObj.valueText = _specificationList[i].valueList[j].value;
          }
        }

        checkedValues.push(_checkedObj);
      }

      return checkedValues;
    },
    //判断规格是否选择完整
    isCheckedAllSpec: function () {
      return !this.getCheckedSpecValue().some(function (v) {
        if (v.valueId == 0) {
          return true;
        }
      });
    },
    getCheckedSpecKey: function () {
      let checkedValue = this.getCheckedSpecValue().map(function (v) {
        return v.valueText;
      });
      return checkedValue;
    },
    // 规格改变时，重新计算价格及显示信息
    changeSpecInfo: function () {
      let checkedNameValue = this.getCheckedSpecValue(); //设置选择的信息

      let checkedValue = checkedNameValue.filter(function (v) {
        if (v.valueId != 0) {
          return true;
        } else {
          return false;
        }
      }).map(function (v) {
        return v.valueText;
      });

      if (checkedValue.length > 0) {
        this.setData({
          tmpSpecText: checkedValue.join('　')
        });
      } else {
        this.setData({
          tmpSpecText: '请选择规格数量'
        });
      }

      if (this.isCheckedAllSpec()) {
        this.setData({
          checkedSpecText: this.tmpSpecText
        }); // 规格所对应的货品选择以后

        let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());

        if (!checkedProductArray || checkedProductArray.length <= 0) {
          this.setData({
            soldout: true
          });
          console.error('规格所对应货品不存在');
          return;
        }

        let checkedProduct = checkedProductArray[0];

        if (checkedProduct.number > 0) {
          this.setData({
            checkedSpecPrice: checkedProduct.price,
            soldout: false
          });
        } else {
          this.setData({
            checkedSpecPrice: this.books.retailPrice,
            soldout: true
          });
        }
      } else {
        this.setData({
          checkedSpecText: '规格数量选择',
          checkedSpecPrice: this.books.retailPrice,
          soldout: false
        });
      }
    },
    // 获取选中的产品（根据规格）
    getCheckedProductItem: function (key) {
      return this.productList.filter(function (v) {
        console.log(key.toString() + "--" + v.specifications.toString());

        if (v.specifications.toString() == key.toString()) {
          return true;
        } else {
          return false;
        }
      });
    },
    //添加或是取消收藏
    addCollectOrNot: function () {
      let that = this;
      util.request(api.CollectAddOrDelete, {
        type: 0,
        valueId: this.id
      }, "POST").then(function (res) {
        let _res = res;

        if (_res.errno == 0) {
          if (_res.data.type == 'add') {
            that.setData({
              collectImage: that.hasCollectImage
            });
          } else {
            that.setData({
              collectImage: that.noCollectImage
            });
          }
        } else {
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: _res.errmsg,
            mask: true
          });
        }
      });
    },
    //立即购买（先自动加入购物车）
    addFast: function () {
      var that = this;

      if (this.openAttr == false) {
        //打开规格选择窗口
        this.setData({
          openAttr: !this.openAttr
        });
      } else {
        //提示选择完整规格
        if (!this.isCheckedAllSpec()) {
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '请选择完整规格'
          });
          return false;
        } //根据选中的规格，判断是否有对应的sku信息


        let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());

        if (!checkedProductArray || checkedProductArray.length <= 0) {
          //找不到对应的product信息，提示没有库存
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '没有库存'
          });
          return false;
        }

        let checkedProduct = checkedProductArray[0]; //验证库存

        if (checkedProduct.number <= 0) {
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '没有库存'
          });
          return false;
        }

        util.request(api.CartFastAdd, {
          booksId: this.books.id,
          number: this.number,
          productId: checkedProduct.id
        }, "POST").then(function (res) {
          if (res.errno == 0) {
            // 如果storage中设置了cartId，则是立即购买，否则是购物车购买
            try {
              uni.setStorageSync('cartId', res.data);
              uni.navigateTo({
                url: '/pages/checkout/checkout'
              });
            } catch (e) {}
          } else {
            uni.showToast({
              image: "/static/static/images/icon_error.png",
              title: res.errmsg,
              mask: true
            });
          }
        });
      }
    },
    //添加到购物车
    addToCart: function () {
      var that = this;

      if (this.openAttr == false) {
        //打开规格选择窗口
        this.setData({
          openAttr: !this.openAttr
        });
      } else {
        //提示选择完整规格
        if (!this.isCheckedAllSpec()) {
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '请选择完整规格'
          });
          return false;
        }


        let checkedProductArray = this.getCheckedProductItem(this.getCheckedSpecKey());

        if (!checkedProductArray || checkedProductArray.length <= 0) {
          //找不到对应的product信息，提示没有库存
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '没有库存'
          });
          return false;
        }

        let checkedProduct = checkedProductArray[0]; //验证库存

        if (checkedProduct.number <= 0) {
          uni.showToast({
            image: "/static/static/images/icon_error.png",
            title: '没有库存'
          });
          return false;
        } //添加到购物车


        util.request(api.CartAdd, {
          booksId: this.books.id,
          number: this.number,
          productId: checkedProduct.id
        }, "POST").then(function (res) {
          let _res = res;

          if (_res.errno == 0) {
            uni.showToast({
              title: '添加成功'
            });
            that.setData({
              openAttr: !that.openAttr,
              cartBooksCount: _res.data
            });

            if (that.userHasCollect == 1) {
              that.setData({
                collectImage: that.hasCollectImage
              });
            } else {
              that.setData({
                collectImage: that.noCollectImage
              });
            }
          } else {
            uni.showToast({
              image: "/static/static/images/icon_error.png",
              title: _res.errmsg,
              mask: true
            });
          }
        });
      }
    },
    cutNumber: function () {
      this.setData({
        number: this.number - 1 > 1 ? this.number - 1 : 1
      });
    },
    addNumber: function () {
      this.setData({
        number: this.number + 1
      });
    },
    switchAttrPop: function () {
      if (this.openAttr == false) {
        this.setData({
          openAttr: !this.openAttr
        });
      }
    },
    closeAttr: function () {
      this.setData({
        openAttr: false
      });
    },
    openCartPage: function () {
      uni.switchTab({
        url: '/pages/cart/cart'
      });
    },
    //根据已选的值，计算其它值的状态
    setSpecValueStatus: function () {}
  }
};
</script>
<style>
.container {
  margin-bottom: 100rpx;
}

.booksimgs {
  width: 750rpx;
  height: 750rpx;
}

.booksimgs image {
  width: 750rpx;
  height: 750rpx;
}

.commodity_screen {
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  background: #000;
  opacity: 0.2;
  overflow: hidden;
  z-index: 1000;
  color: #fff;
}

.commodity_attr_box {
  width: 100%;
  overflow: hidden;
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 2000;
  background: #fff;
  padding-top: 20rpx;
}

.books-info {
  width: 750rpx;
  height: 300rpx;
  overflow: hidden;
  background: #fff;
}

.books-info .c {
  display: block;
  width: 718.75rpx;
  height: 100%;
  margin-left: 31.25rpx;
  padding: 18rpx 31.25rpx 38rpx 0;
  border-bottom: 1px solid #f4f4f4;
}

.books-info .c text {
  display: block;
  width: 687.5rpx;
  text-align: left;
}

.books_name {
  /* border: 1px solid black; */
  height: 90rpx;
  line-height: 86rpx;
  border-bottom: 1px solid #fafafa;
}

.books_name_left {
  /* border: 1px solid #757575; */
  float: left;
  height: 90rpx;
  line-height: 86rpx;
  margin-left: 25rpx;
  width: 70%;
  letter-spacing: 1rpx;
  overflow: hidden;
  text-overflow:ellipsis;
  white-space: nowrap;
}

.books_name_right {
  float: right;
  font-weight: 550;
  margin-top: 28rpx;
  width: 25%;
  height: 90rpx;
  line-height: 85rpx;
  padding: 0;
  margin: 0;
  margin-right: 0rpx;
  text-align: center;
  font-size: 30rpx;
  color: #f4f4f4;
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 0rpx;
  border-bottom-right-radius: 0rpx;
  letter-spacing: 3rpx;
  /* background-image: linear-gradient(to right, #ff7701 100%); */
  background-image: linear-gradient(to right, #f3d10e 0%, #f48f18 100%);
}

.books-info .desc {
  height: 43rpx;
  margin-bottom: 41rpx;
  line-height: 36rpx;
}

.books-info .price {
  height: 70rpx;
  align-content: center;
}

.books-info .counterPrice {
  padding-left: 5%;
  text-decoration: line-through;
}

.books-info .retailPrice {
  float: left;
  padding-left: 0rpx;
}

.section-nav {
  width: 750rpx;
  height: 108rpx;
  background: #fff;
  margin-bottom: 20rpx;
}

.section-nav .t {
  float: left;
  width: 600rpx;
  height: 108rpx;
  line-height: 108rpx;
  margin-left: 31.25rpx;
}

.section-nav .i {
  float: right;
  width: 52rpx;
  height: 52rpx;
  margin-right: 16rpx;
  margin-top: 28rpx;
}

.section-act .t {
  float: left;
  display: flex;
  align-items: center;
  width: 600rpx;
  height: 108rpx;
  overflow: hidden;
  line-height: 108rpx;
  margin-left: 31.25rpx;
}

.section-act .label {
  color: #999;
}

.section-act .tag {
  display: flex;
  align-items: center;
  padding: 0 10rpx;
  border-radius: 3px;
  height: 37rpx;
  width: auto;
  color: #f48f18;
  overflow: hidden;
  border: 1px solid #f48f18;
  font-size: 25rpx;
  margin: 0 10rpx;
}

.section-act .text {
  display: flex;
  align-items: center;
  height: 37rpx;
  width: auto;
  overflow: hidden;
  color: #f48f18;
  font-size: 29rpx;
}

.comments {
  width: 100%;
  height: auto;
  padding-left: 30rpx;
  background: #fff;
  margin: 20rpx 0;
}

.comments .h {
  height: 102.5rpx;
  line-height: 100.5rpx;
  width: 718.75rpx;
  padding-right: 16rpx;
  border-bottom: 1px solid #dfdfdf;
}

.comments .h .t {
  display: block;
  float: left;
  width: 50%;
}

.comments .h .i {
  display: block;
  float: right;
  width: 164rpx;
  height: 100.5rpx;
  line-height: 100.5rpx;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAAAAAA7VNdtAAAA4klEQVRIx+2VPQuCABCG/bVvmgiCfVM0CH2SQdBiEERDNNbk5laTkzREkEMQBRIEhaFprQ0RvNDo7c9w9z53J7zoElIkRT6rVT2xSBfankQCA4pL9hKNIK3I9pM5MhY7MSuDeUIOeS3BjMhcXAVGQEbpadCvZPqnMipnUpirjpxHOhb0oGxILSMTWYc0OZmhQCLPMUoc8hhAdink1oS6o9r3aygeqSEf8qhfqCi3Kto3ShhHxjCktLRFTGJK/iWwoFYsnkK0qUUOh1/M+oncO1C33FFqIH8gT19f99OXlCL/Q97bCIp/pz2QqAAAAABJRU5ErkJggg==') right center no-repeat;
  background-size: 52rpx;
}

.comments .b {
  height: auto;
  width: 720rpx;
}

.comments .item {
  height: auto;
  width: 720rpx;
  overflow: hidden;
}

.comments .info {
  height: 117rpx;
  width: 100%;
  padding: 15rpx 0 15rpx 0;
}

.comments .user {
  float: left;
  width: 40%;
  height: 87rpx;
  line-height: 87rpx;
  font-size: 0;
}

.comments .user image {
  display: block;
  float: left;
  width: 67rpx;
  height: 67rpx;
  margin-right: 27rpx;
  border-radius: 50%;
  overflow: hidden;
}

.comments .user text {
  display: block;
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
  color: #999;
  font-size: 28rpx;
  margin-right: 30rpx;
}

.comments .content {
  width: 720rpx;
  padding-right: 30rpx;
  line-height: 45.8rpx;
  font-size: 28rpx;
  margin-bottom: 15rpx;
}

.comments .imgs {
  width: 720rpx;
  height: auto;
  margin-bottom: 25rpx;
}

.comments .imgs .img {
  height: 150rpx;
  width: 150rpx;
  margin-right: 28rpx;
}

.comments .spec {
  width: 720rpx;
  padding-right: 30rpx;
  line-height: 30rpx;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.books-attr {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 0 31.25rpx 25rpx 31.25rpx;
  background: #fff;
}

.books-attr .t {
  width: 687.5rpx;
  height: 104rpx;
  line-height: 104rpx;
}

.books-attr .item {
  width: 687.5rpx;
  height: 65rpx;
  padding: 10rpx 20rpx;
  margin-bottom: 3rpx;
  background: #f7f7f7;
}

.books-attr .left {
  float: left;
  font-size: 28rpx;
  width: 134rpx;
  height: 45rpx;
  line-height: 45rpx;
  overflow: hidden;
  color: #666;
  font-weight: bold;
}

.books-attr .right {
  float: left;
  font-size: 28rpx;
  margin-left: 35rpx;
  width: 465rpx;
  height: 45rpx;
  line-height: 45rpx;
  overflow: hidden;
  color: #666;
}

.detail {
  width: 750rpx;
  height: auto;
  overflow: hidden;
}

.detail image {
  width: 750rpx;
  display: block;
}

.common-problem {
  width: 750rpx;
  height: auto;
  overflow: hidden;
}

.common-problem .h {
  position: relative;
  height: 145.5rpx;
  width: 750rpx;
  padding: 56.25rpx 0;
  background: #fff;
  text-align: center;
}

.common-problem .h .line {
  display: inline-block;
  position: absolute;
  top: 72rpx;
  left: 0;
  z-index: 2;
  height: 1px;
  margin-left: 225rpx;
  width: 300rpx;
  background: #ccc;
}

.common-problem .h .title {
  display: inline-block;
  position: absolute;
  top: 56.125rpx;
  left: 0;
  z-index: 3;
  height: 33rpx;
  margin-left: 285rpx;
  width: 180rpx;
  background: #fff;
}

.common-problem .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 0rpx 30rpx;
  background: #fff;
}

.common-problem .item {
  height: auto;
  overflow: hidden;
  padding-bottom: 25rpx;
}

.common-problem .question-box .spot {
  float: left;
  display: block;
  height: 8rpx;
  width: 8rpx;
  background: #b4282d;
  border-radius: 50%;
  margin-top: 11rpx;
}

.common-problem .question-box .question {
  float: left;
  line-height: 30rpx;
  padding-left: 8rpx;
  display: block;
  font-size: 26rpx;
  padding-bottom: 15rpx;
  color: #303030;
}

.common-problem .answer {
  line-height: 36rpx;
  padding-left: 16rpx;
  font-size: 26rpx;
  color: #787878;
}

.related-books {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding-bottom: 80rpx;
}

.related-books .h {
  position: relative;
  height: 145.5rpx;
  width: 750rpx;
  padding: 56.25rpx 0;
  background: #fff;
  text-align: center;
  border-bottom: 1px solid #f4f4f4;
}

.related-books .h .line {
  display: inline-block;
  position: absolute;
  top: 72rpx;
  left: 0;
  z-index: 2;
  height: 1px;
  margin-left: 225rpx;
  width: 300rpx;
  background: #ccc;
}

.related-books .h .title {
  display: inline-block;
  position: absolute;
  top: 56.125rpx;
  left: 0;
  z-index: 3;
  height: 33rpx;
  margin-left: 285rpx;
  width: 180rpx;
  background: #fff;
}

.related-books .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
}

.related-books .b .item {
  float: left;
  background: #fff;
  width: 375rpx;
  height: auto;
  overflow: hidden;
  text-align: center;
  padding: 15rpx 31.25rpx;
  border-right: 1px solid #f4f4f4;
  border-bottom: 1px solid #f4f4f4;
}

.related-books .item .img {
  width: 311.45rpx;
  height: 311.45rpx;
}

.related-books .item .name {
  display: block;
  width: 311.45rpx;
  height: 35rpx;
  margin: 11.5rpx 0 15rpx 0;
  text-align: center;
  overflow: hidden;
  font-size: 30rpx;
  color: #333;
}

.related-books .item .price {
  display: block;
  width: 311.45rpx;
  height: 30rpx;
  text-align: center;
  font-size: 30rpx;
  color: #b4282d;
}

.bottom-btn {
  position: fixed;
  left: 0;
  bottom: 0;
  z-index: 10;
  width: 750rpx;
  height: 100rpx;
  display: flex;
  background: #fff;
}

.bottom-btn .l {
  float: left;
  height: 100rpx;
  width: 162rpx;
  border: 1px solid #f4f4f4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bottom-btn .l.l-collect {
  border-right: none;
  border-left: none;
  text-align: center;
  width: 90rpx;
}

.bottom-btn .l.l-kefu {
  position: relative;
  height: 54rpx;
  width: 63rpx;
}

.bottom-btn .l.l-cart .box {
  position: relative;
  height: 60rpx;
  width: 60rpx;
}

.bottom-btn .l.l-cart .cart-count {
  height: 28rpx;
  width: 28rpx;
  z-index: 10;
  position: absolute;
  top: 0;
  right: 0;
  background: #b4282d;
  text-align: center;
  font-size: 18rpx;
  color: #fff;
  line-height: 28rpx;
  border-radius: 50%;
}

.bottom-btn .l.l-cart .icon {
  position: absolute;
  top: 10rpx;
  left: 0;
}

.bottom-btn .l.l-kefu .kefu-count {
  height: 28rpx;
  width: 28rpx;
  z-index: 10;
  position: absolute;
  top: 0;
  right: 0;
  /* background: #b4282d; */
  text-align: center;
  font-size: 18rpx;
  color: #fff;
  line-height: 28rpx;
  border-radius: 50%;
}

.bottom-btn .l.l-kefu .icon {
  position: absolute;
  top: 10rpx;
  left: 0;
}

.bottom-btn .l .icon {
  display: block;
  height: 44rpx;
  width: 44rpx;
}

.bottom-btn .c {
  float: left;
  background: #b4282d;
  height: 100rpx;
  line-height: 96rpx;
  flex: 1;
  text-align: center;
  color: #fff;
  /* border-top: 2px solid #f4f4f4; *//* border-bottom: 2px solid #f4f4f4;  */
}

.bottom-btn .r {
  border: 1px solid #f48f18;
  background: #f48f18;
  float: left;
  height: 100rpx;
  line-height: 96rpx;
  flex: 1;
  text-align: center;
  color: #fff;
}

.bottom-btn .n {
  border: 1px solid #c0b8ae;
  background: #c0b8ae;
  float: left;
  height: 100rpx;
  line-height: 96rpx;
  flex: 1;
  text-align: center;
  color: #fff;
}



.attr-pop-box {
  width: 100%;
  height: 100%;
  position: fixed;
  background: rgba(0, 0, 0, 0.5);
  z-index: 8;
  bottom: 0;
  /* display: none; */
}

.attr-pop {
  width: 100%;
  height: auto;
  max-height: 780rpx;
  padding: 31.25rpx;
  background: #fff;
  position: fixed;
  z-index: 9;
  bottom: 100rpx;
  border-top-left-radius: 20rpx;
  border-bottom-left-radius: 0rpx;
  border-top-right-radius: 20rpx;
  border-bottom-right-radius: 0rpx;
}

.attr-pop .close {
  position: absolute;
  width: 48rpx;
  height: 48rpx;
  right: 31.25rpx;
  overflow: hidden;
  top: 31.25rpx;
}

.attr-pop .close .icon {
  width: 48rpx;
  height: 48rpx;
}

.attr-pop .img-info {
  width: 687.5rpx;
  height: 177rpx;
  overflow: hidden;
  margin-bottom: 41.5rpx;
}

.attr-pop .img {
  float: left;
  height: 177rpx;
  width: 177rpx;
  background: #f4f4f4;
  margin-right: 31.25rpx;
}

.attr-pop .info {
  float: left;
  height: 177rpx;
  display: flex;
  align-items: center;
}

.attr-pop .p {
  font-size: 33rpx;
  color: #333;
  height: 33rpx;
  line-height: 33rpx;
  margin-bottom: 10rpx;
}

.attr-pop .a {
  font-size: 29rpx;
  color: #333;
  height: 40rpx;
  line-height: 40rpx;
}

.spec-con {
  width: 100%;
  height: auto;
  overflow: hidden;
}

.spec-con .name {
  height: 32rpx;
  line-height: 32rpx;
  margin-bottom: 22rpx;
  font-size: 29rpx;
  color: #333;
}

.spec-con .values {
  height: auto;
  margin-bottom: 31.25rpx;
  font-size: 0;
}

.spec-con .value {
  display: inline-block;
  height: 62rpx;
  padding: 0 15rpx;
  line-height: 56rpx;
  text-align: center;
  margin-right: 25rpx;
  margin-bottom: 16.5rpx;
  border: 1px solid #999;
  border-radius:6rpx;
  font-size: 25rpx;
  color: #999;
}

.spec-con .value.disable {
  border: 1px solid #ccc;
  color: #ccc;
}

.spec-con .value.selected {
  border: 1px solid #f0070e;
  color: #fff;
  background: #f0070e;
}

.number-item .selnum {
  width: 322rpx;
  height: 71rpx;
  border: 1px solid #ccc;
  display: flex;
}

.number-item .cut {
  width: 93.75rpx;
  height: 100%;
  text-align: center;
  line-height: 65rpx;
}

.number-item .number {
  flex: 1;
  height: 100%;
  text-align: center;
  line-height: 68.75rpx;
  border-left: 1px solid #ccc;
  border-right: 1px solid #ccc;
  float: left;
}

.number-item .add {
  width: 93.75rpx;
  height: 100%;
  text-align: center;
  line-height: 65rpx;
}

.share-pop-box {
  width: 100%;
  height: 100%;
  position: fixed;
  background: rgba(0, 0, 0, 0.5);
  z-index: 8;
  bottom: 0;
  /* display: none; */
}

.share-pop {
  width: 100%;
  height: auto;
  max-height: 780rpx;
  padding: 31.25rpx;
  background: #fff;
  position: fixed;
  z-index: 9;
  bottom: 100rpx;
}

.share-pop .close {
  position: absolute;
  width: 48rpx;
  height: 48rpx;
  right: 31.25rpx;
  top: 31.25rpx;
}

.share-pop .close .icon {
  width: 48rpx;
  height: 48rpx;
}

.share-pop .share-info {
  width: 100%;
  height: 225rpx;
  overflow: hidden;
  margin-bottom: 41.5rpx;
}

.sharebtn {
  top: 75rpx;
  background: none !important;
  font-size: 32rpx;
  color: #fff !important;
  border-radius: 0%;
  width: 175rpx;
  height: 150rpx;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  float: left;
  background: #fff;
  border-bottom: 0px solid #fafafa;
  margin-left: 15%;
}

.sharebtn::after {
  border: none;
  border-radius: 0%;
}

.sharebtn_image {
  /* border: 1px solid #757575; */
  width: 128rpx;
  height: 128rpx;
  margin-top: 0rpx;
}

.sharebtn_text {
  /* border: 1px solid #757575; */
  width: 150rpx;
  margin-bottom: 2rpx;
  height: 20rpx;
  line-height: 20rpx;
  font-size: 20rpx;
  color: #555;
}

.separate {
  background: #e0e3da;
  width: 100%;
  height: 6rpx;
}

</style>
