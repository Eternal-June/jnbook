<template>
<view class="container">
  <view class="no-login" v-if="!hasLogin">
    <view class="c">
      <image src="/static/static/images/noCart.png"></image>
      <button style="background-color:#A9A9A9" @tap="goLogin">去登录</button>
    </view>
  </view>
  <view class="login" v-else>
    <view class="service-policy">
      <view class="item">让书籍焕发新的生机</view>
      <view class="item">快速退换货</view>
      <view class="item">同校区免邮费</view>
    </view>
    <view class="no-cart" v-if="cartBooks.length <= 0">
      <view class="c">
        <image src="/static/static/images/noCart.png"></image>
      </view>
    </view>
    <view class="cart-view" v-else>
      <view class="list" v-if="isMultiOrderModel==1">
        <view class="group-item">
          <view v-for="(bitem, index) in compusCartbooks" :key="index" class="books">
            <view v-for="(item, index2) in bitem.cartList" :key="index2" :class="'item ' + (isEditCart ? 'edit' : '')">
              <view :class="'checkbox ' + (item.checked ? 'checked' : '')" @tap="checkedItem" :data-productid="item.productId"></view>
              <view class="cart-books">
                <image class="img" :src="item.picUrl" @tap="toBooksDetail" :data-booksid="item.booksId"></image>
                <view class="info">
                  <view class="t">
                    <text class="name">{{item.booksName}}</text>
                    <text class="compusname">{{item.compusname}}</text>
                  </view>
                  <view class="b">
                    <text class="price">￥{{item.price}}</text>
                    <text class="num"> x{{item.number}}</text>
                    <view class="selnum">
                      <view class="cut" @tap="cutNumber" :data-productid="item.productId">-</view>
                      <input :value="item.number" class="number" disabled="true" type="number"></input>
                      <view class="add" @tap="addNumber" :data-productid="item.productId">+</view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="list" v-else>
        <view class="group-item">
          <view class="books">
            <view v-for="(item, index) in cartBooks" :key="index" :class="'item ' + (isEditCart ? 'edit' : '')">
              <view :class="'checkbox ' + (item.checked ? 'checked' : '')" @tap="checkedItem" :data-productId="item.productId"></view>
              <view class="cart-books">
                <image class="img" :src="item.picUrl" @tap="toBooksDetail" :data-booksId="item.booksId"></image>
                <view class="info">
                  <view class="t">
                    <text class="name">{{item.booksName}} </text>
                    <text class="compusname">{{item.compusname}}</text>
                    <text class="num">x{{item.number}}</text>
                  </view>
                  <view class="attr">{{ isEditCart ? '已选择:' : ''}}{{item.booksSpecificationValues||''}}</view>
                  <view class="b">
                    <text class="price">￥{{item.price}}</text>
                    <view class="selnum">
                      <view class="cut" @tap="cutNumber" :data-productid="item.productId">-</view>
                      <input :value="item.number" class="number" disabled="true" type="number"></input>
                      <view class="add" @tap="addNumber" :data-productid="item.productId">+</view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view class="cart-bottom">
        <view :class="'checkbox ' + (checkedAllStatus ? 'checked' : '')" @tap="checkedAll">全选({{cartTotal.checkedBooksCount}})</view>
        <view class="total">{{!isEditCart ? '￥'+cartTotal.checkedBooksAmount : ''}}</view>
        <view class="action_btn_area">
          <view :class="!isEditCart ? 'edit' : 'sure'" @tap="editCart">{{!isEditCart ? '编辑' : '完成'}}</view>
          <view class="delete" @tap="deleteCart" v-if="isEditCart">删除({{cartTotal.checkedBooksCount}})</view>
          <view class="checkout" @tap="checkoutOrder" v-if="!isEditCart">下单</view>
          <!-- </view>  -->
        </view>
      </view>
    </view>

  </view>
</view>
</template>


<script>
var util = require("../../utils/util.js");
var api = require("../../config/api.js");
var user = require("../../utils/user.js");
var app = getApp();

export default {
  data() {
    return {
      isMultiOrderModel: 0,
      cartBooks: [],
      compusCartbooks: [],
      cartTotal: {
        "booksCount": 0,
        "booksAmount": 0.00,
        "checkedBooksCount": 0,
        "checkedBooksAmount": 0.00
      },
      isEditCart: false,
      checkedAllStatus: true,
      editCartList: [],
      hasLogin: false
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {// 页面初始化 options为页面跳转所带来的参数
  },
  onReady: function () {// 页面渲染完成
  },

  onPullDownRefresh() {
    uni.showNavigationBarLoading(); //在标题栏中显示加载

    this.getCartList();
    uni.hideNavigationBarLoading(); //完成停止加载

    uni.stopPullDownRefresh(); //停止下拉刷新
  },

  onShow: function () {
    // 页面显示
    if (app.globalData.hasLogin) {
      this.getCartList();
    }

    this.setData({
      hasLogin: app.globalData.hasLogin
    });
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    goLogin() {
      uni.navigateTo({
        url: "/pages/auth/login/login"
      });
    },

    getCartList: function () {
      let that = this;
      util.request(api.CartList).then(function (res) {
        if (res.errno === 0) {
          if (res.data.isMultiOrderModel === 1) {
            that.setData({
              isMultiOrderModel: res.data.isMultiOrderModel,
              compusCartbooks: res.data.compusCartbooks,
              cartTotal: res.data.cartTotal
            });
          } else {
            that.setData({
              isMultiOrderModel: res.data.isMultiOrderModel,
              cartBooks: res.data.cartList,
              cartTotal: res.data.cartTotal
            });
          }

          that.setData({
            checkedAllStatus: that.isCheckedAll()
          });
        }
      });
    },
    isChildCheckedAll: function (cartList) {
      return cartList.every(function (element, index, array) {
        if (element.checked == true) {
          return true;
        } else {
          return false;
        }
      });
    },
    isCheckedAll: function () {
      let that = this;

      if (that.isMultiOrderModel === 1) {
        //多店铺模式的书籍全选判断
        return that.compusCartbooks.every(function (element, index, array) {
          if (that.isChildCheckedAll(element.cartList)) {
            return true;
          } else {
            return false;
          }
        });
      } else {
        //判断购物车书籍已全选
        return that.cartBooks.every(function (element, index, array) {
          if (element.checked == true) {
            return true;
          } else {
            return false;
          }
        });
      }
    },
    doCheckedAll: function () {
      let checkedAll = this.isCheckedAll();
      this.setData({
        checkedAllStatus: this.isCheckedAll()
      });
    },
    getProductChecked: function (productId) {
      let that = this;
      let isChecked = null;

      if (that.isMultiOrderModel === 1) {
        that.compusCartbooks.forEach(function (v) {
          let cartList = v.cartList;
          cartList.forEach(function (o) {
            if (o.productId == productId) {
              console.log(1);
              isChecked = o.checked ? 0 : 1;
            }
          });
        });
      } else {
        that.cartBooks.forEach(function (o) {
          console.log(productId);
          console.log(o.productId);
          if (o.productId == productId) {
            console.log(0);
            isChecked = o.checked ? 0 : 1;
          }
        });
      }
      console.log(isChecked);

      return isChecked;
    },
    toBooksDetail: function (e){

      let booksId = e.currentTarget.dataset.booksid;
      console.log(booksId);
      uni.navigateTo({
        url: "../books/books?id=" + booksId,
      });
    },
    checkedItem: function (event) {
      //let itemIndex = event.target.dataset.itemIndex;
      let productId = event.currentTarget.dataset.productid;
      let that = this;
      let productIds = [];
      productIds.push(productId);
      let isChecked = that.getProductChecked(productId);

      if (!this.isEditCart) {
        util.request(api.CartChecked, {
          productIds: productIds,
          isChecked: isChecked
        }, 'POST').then(function (res) {
          if (res.errno === 0) {
            if (res.data.isMultiOrderModel === 1) {
              that.setData({
                isMultiOrderModel: res.data.isMultiOrderModel,
                compusCartbooks: res.data.compusCartbooks,
                cartTotal: res.data.cartTotal
              });
            } else {
              that.setData({
                isMultiOrderModel: res.data.isMultiOrderModel,
                cartBooks: res.data.cartList,
                cartTotal: res.data.cartTotal
              });
            }
          }

          that.setData({
            checkedAllStatus: that.isCheckedAll()
          });
        });
      } else {
        //编辑状态
        if (that.isMultiOrderModel === 1) {
          let tmpcompusCartData = this.compusCartbooks.map(function (element, index, array) {
            let tmpcompusBooks = element.cartList.map(function (childEle, childIndex, childArr) {
              if (childEle.productId === productId) {
                childEle.checked = !childEle.checked;
              }

              return childEle;
            });
            element.cartList = tmpcompusBooks;
            return element;
          });
          that.setData({
            compusCartbooks: tmpcompusCartData,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        } else {
          let tmpCartData = this.cartBooks.map(function (element, index, array) {
            if (element.productId === productId) {
              element.checked = !element.checked;
            }

            return element;
          });
          that.setData({
            cartBooks: tmpCartData,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        }
      }
    },
    getCheckedBooksCount: function () {
      let that = this;
      let checkedBooksCount = 0;

      if (that.isMultiOrderModel === 1) {
        that.compusCartbooks.forEach(function (v) {
          v.cartList.forEach(function (o) {
            if (o.checked === true) {
              checkedBooksCount += o.number;
            }
          });
        });
      } else {
        that.cartBooks.forEach(function (v) {
          if (v.checked === true) {
            checkedBooksCount += v.number;
          }
        });
      }

      console.log(checkedBooksCount);
      return checkedBooksCount;
    },
    checkedAll: function () {
      let that = this;

      if (!this.isEditCart) {
        let productIds = [];

        if (that.isMultiOrderModel === 1) {
          that.compusCartbooks.forEach(function (v) {
            v.cartList.forEach(function (o) {
              productIds.push(o.productId);
            });
          });
        } else {
          productIds = that.cartBooks.map(function (v) {
            return v.productId;
          });
        }

        util.request(api.CartChecked, {
          productIds: productIds,
          isChecked: that.isCheckedAll() ? 0 : 1
        }, 'POST').then(function (res) {
          if (res.errno === 0) {
            console.log(res.data);

            if (res.data.isMultiOrderModel === 1) {
              that.setData({
                isMultiOrderModel: res.data.isMultiOrderModel,
                compusCartbooks: res.data.compusCartbooks,
                cartTotal: res.data.cartTotal
              });
            } else {
              that.setData({
                isMultiOrderModel: res.data.isMultiOrderModel,
                cartBooks: res.data.cartList,
                cartTotal: res.data.cartTotal
              });
            }
          }

          that.setData({
            checkedAllStatus: that.isCheckedAll()
          });
        });
      } else {
        //编辑状态,将所有
        let checkedAllStatus = that.isCheckedAll();

        if (that.isMultiOrderModel === 1) {
          let tmpcompusCartData = this.compusCartbooks.map(function (element, index, array) {
            let tmpcompusBooks = element.cartList.map(function (childEle, childIndex, childArr) {
              childEle.checked = !checkedAllStatus;
              return childEle;
            });
            element.cartList = tmpcompusBooks;
            return element;
          });
          that.setData({
            compusCartbooks: tmpcompusCartData,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        } else {
          let tmpCartData = this.cartBooks.map(function (element, index, array) {
            element.checked = !checkedAllStatus;
            return element;
          });
          that.setData({
            cartBooks: tmpCartData,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        }
      }
    },
    editCart: function () {
      var that = this;

      if (this.isEditCart) {
        this.getCartList();
        this.setData({
          isEditCart: !this.isEditCart
        });
      } else {
        //编辑状态
        if (that.isMultiOrderModel === 1) {
          let tmpcompusCartData = that.compusCartbooks.map(function (element, index, array) {
            let tmpcompusBooks = element.cartList.map(function (childEle, childIndex, childArr) {
              childEle.checked = false;
              return childEle;
            });
            element.cartList = tmpcompusBooks;
            return element;
          });
          that.setData({
            compusCartbooks: tmpcompusCartData,
            isEditCart: !that.isEditCart,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        } else {
          let tmpCartData = that.cartBooks.map(function (element, index, array) {
            element.checked = false;
            return element;
          });
          that.setData({
            // editCartList: this.data.cartBooks,
            cartBooks: tmpCartData,
            isEditCart: !that.isEditCart,
            checkedAllStatus: that.isCheckedAll(),
            'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
          });
        }
      }
    },
    updateCart: function (productId, booksId, number, id) {
      let that = this;
      util.request(api.CartUpdate, {
        productId: productId,
        booksId: booksId,
        number: number,
        id: id
      }, 'POST').then(function (res) {
        that.setData({
          checkedAllStatus: that.isCheckedAll()
        });
      });
    },
    getProductItem: function (productId) {
      let that = this;
      let productItem = null;

      if (that.isMultiOrderModel === 1) {
        that.compusCartbooks.forEach(function (v) {
          let cartList = v.cartList;
          cartList.forEach(function (o) {
            if (o.productId === productId) {
              productItem = o;
            }
          });
        });
      } else {
        that.cartBooks.forEach(function (o) {
          if (o.productId === productId) {
            productItem = o;
          }
        });
      }

      return productItem;
    },
    setProductItem: function (cartItem, productId) {
      let that = this;

      if (that.isMultiOrderModel === 1) {
        let tmpcompusCartData = this.compusCartbooks.map(function (element, index, array) {
          let tmpcompusBooks = element.cartList.map(function (childEle, childIndex, childArr) {
            if (childEle.productId === productId) {
              return cartItem;
            } else {
              return childEle;
            }
          });
          element.cartList = tmpcompusBooks;
          return element;
        });
        that.setData({
          compusCartbooks: tmpcompusCartData,
          checkedAllStatus: that.isCheckedAll(),
          'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
        });
      } else {
        let tmpCartData = this.cartBooks.map(function (element, index, array) {
          if (element.productId === productId) {
            return cartItem;
          } else {
            return element;
          }
        });
        that.setData({
          cartBooks: tmpCartData,
          checkedAllStatus: that.isCheckedAll(),
          'cartTotal.checkedBooksCount': that.getCheckedBooksCount()
        });
      }
    },
    cutNumber: function (event) {
      //let itemIndex = event.target.dataset.itemIndex;
      let productId = event.currentTarget.dataset.productid;
      let cartItem = this.getProductItem(productId);
      let number = cartItem.number - 1 > 1 ? cartItem.number - 1 : 1;
      cartItem.number = number;
      this.setProductItem(cartItem, productId);
      this.updateCart(cartItem.productId, cartItem.booksId, number, cartItem.id);
    },
    addNumber: function (event) {
      // let itemIndex = event.target.dataset.itemIndex;
      let productId = event.currentTarget.dataset.productid;
      let cartItem = this.getProductItem(productId);
      let number = cartItem.number + 1;
      cartItem.number = number;
      this.setProductItem(cartItem, productId);
      this.updateCart(cartItem.productId, cartItem.booksId, number, cartItem.id);
    },
    checkoutOrder: function () {
      //获取已选择的书籍
      let that = this;
      /*var checkedBooks = this.data.cartBooks.filter(function(element, index, array) {
        if (element.checked == true) {
          return true;
        } else {
          return false;
        }
      });
      if (checkedBooks.length <= 0) {
        return false;
      }*/

      if (that.getCheckedBooksCount() <= 0) {
        uni.showModal({
          title: '错误信息',
          content: '请勾选需要下单的书籍！',
          showCancel: false
        });
        return false;
      } // storage中设置了cartId，则是购物车购买


      try {
        uni.setStorageSync('cartId', 0);
        uni.navigateTo({
          url: '/pages/checkout/checkout'
        });
      } catch (e) {}
    },
    deleteCart: function () {
      //获取已选择的书籍
      let that = this;
      /*let productIds = this.data.cartBooks.filter(function(element, index, array) {
        if (element.checked == true) {
          return true;
        } else {
          return false;
        }
      });
       if (productIds.length <= 0) {
        return false;
      }*/

      if (that.getCheckedBooksCount() <= 0) {
        uni.showModal({
          title: '错误信息',
          content: '请勾选需要删除的书籍！',
          showCancel: false
        });
        return false;
      }

      let productIds = [];

      if (that.isMultiOrderModel === 1) {
        that.compusCartbooks.forEach(function (v) {
          v.cartList.forEach(function (o) {
            if (o.checked == true) {
              productIds.push(o.productId);
            }
          });
        });
      } else {
        productIds = that.cartBooks.map(function (v) {
          if (v.checked == true) {
            return v.productId;
          }
        });
      }

      util.request(api.CartDelete, {
        productIds: productIds
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          if (res.data.isMultiOrderModel === 1) {
            that.setData({
              isMultiOrderModel: res.data.isMultiOrderModel,
              compusCartbooks: res.data.compusCartbooks,
              cartTotal: res.data.cartTotal
            });
          } else {
            that.setData({
              isMultiOrderModel: res.data.isMultiOrderModel,
              cartBooks: res.data.cartList,
              cartTotal: res.data.cartTotal
            });
          }

          that.setData({
            checkedAllStatus: that.isCheckedAll()
          });
        }
      });
    }
  }
};
</script>

<style>
.page {
  height: 100%;
  min-height: 100%;
  background: #f4f4f4;
}

.container {
  background: #f4f4f4;
  width: 100%;
  height: auto;
  min-height: 100%;
  overflow: hidden;
}

.service-policy {
  width: 750rpx;
  height: 73rpx;
  background: #f4f4f4;
  padding: 0 31.25rpx;
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  justify-content: space-between;
}

.service-policy .item {
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAMAAAC67D+PAAAAM1BMVEWrKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKytnkgnjAAAAEHRSTlMAERJMTU5ub3Z3hL7y8/T5Yye+AQAAAEVJREFUCB0FwQkCgCAMwLAyJyrj6P9fawJkrVUXwFDVAZenR/RjUj4An8U0AMLJNADCRfkAvBbp6a31Y8JQ1QFw196V8AOmRAMI0B3h8wAAAABJRU5ErkJggg==') 0 center no-repeat;
  background-size: 10rpx;
  padding-left: 15rpx;
  display: flex;
  align-items: center;
  font-size: 25rpx;
  color: #666;
}

.no-login {
  width: 100%;
  height: auto;
  margin: 0 auto;
}

.no-login .c {
  width: 100%;
  height: auto;
  margin-top: 200rpx;
}

.no-login .c image {
  margin: 0 auto;
  display: block;
  text-align: center;
  width: 414rpx;
  height: 336rpx;
  margin-bottom: 100rpx;
}

.no-login .c text {
  margin: 0 auto;
  display: block;
  width: 258rpx;
  height: 59rpx;
  line-height: 29rpx;
  text-align: center;
  font-size: 35rpx;
  color: #999;
}

.no-login button {
  width: 90%;
  margin: 40 auto;
  color: #fff;
  font-size: 30rpx;
  height: 96rpx;
  line-height: 96rpx;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: flex;
  bottom: 0;
  left: 0;
  border-radius: 0;
  padding: 0;
  margin-left: 5%;
  text-align: center;
  /* padding-left: -5rpx; */
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #9a9ba1 0%, #9a9ba1 100%);
}

.no-cart {
  width: 100%;
  height: auto;
  margin: 0 auto;
}

.no-cart .c {
  width: 100%;
  height: auto;
  margin-top: 200rpx;
}

.no-cart .c image {
  margin: 0 auto;
  display: block;
  text-align: center;
  width: 414rpx;
  height: 336rpx;
}

.no-cart .c text {
  margin: 0 auto;
  display: block;
  width: 258rpx;
  height: 29rpx;
  line-height: 29rpx;
  text-align: center;
  font-size: 29rpx;
  color: #999;
}

.cart-view {
  width: 100%;
  height: auto;
  overflow: hidden;
}

.cart-view .list {
  height: auto;
  width: 100%;
  overflow: hidden;
  background: #f4f4f4;
  margin-bottom: 120rpx;
}

.cart-view .group-item {
  height: auto;
  width: 100%;
  margin-top:20rpx;
  background: #f4f4f4;
  margin-bottom: 18rpx;
}

.books {
  margin-top: 20rpx;
  background: #fff;
}

.books .h {
  height: 83.3rpx;
  line-height: 83.3rpx;
  margin-left: 31.25rpx;
  padding-right: 31.25rpx;
  border-bottom: 1px solid #f4f4f4;
  font-size: 30rpx;
  color: #333;
}

.books .h .l {
  float: left;
  font-weight:bold;
}

.cart-view .item {
  height: 202rpx;
  width: 100%;
  overflow: hidden;
}

.cart-view .item .checkbox {
  float: left;
  height: 34rpx;
  width: 34rpx;
  margin: 65rpx 18rpx 65rpx 26rpx;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACYAAAAmCAMAAACf4xmcAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABCUExURUdwTMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzAV+Z0EAAAAVdFJOUwAJ+KUEFTPay2bzRXdZ7RkhmJ6qJOWhY+QAAAEDSURBVDjLnZTplsIgDIUNWwK2tdt9/1cdxHGmVcAc+dH25Hw0+71cvjhztDIZM4mNc4txo+BwZKxSVwbSFoMn8iFuCeDrG0RLNkc6GGK+ttCZ8gIzuJcgBgPxJ4rB4T2OkM0HjgRyq8V7Y8i/3/V06YVb/nKECa0qBYPffB1jaFd8AD8+RrBrY8R41FkQew2MkPtrR6IeRglzoW1/HrbizfZ9Pv8jCH0slOAm+D7mMeUn4PoYwegxpVNlCsqCKMurbJay9R8GyT0HSTmWeciTYsh7K+MPK1MW0H9eQOU652sqcch+15rUrFQXLpuFy7ksXLYuXDUZbBZ9v4sqiqju34jyD97JD4dkfgo1AAAAAElFTkSuQmCC') no-repeat;
  background-size: 34rpx;
}

.cart-view .item .checkbox.checked {
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACYAAAAmCAMAAACf4xmcAAAAQlBMVEUAAACrKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyvw19exOzv////z4uK1Q0Pt0dGxOjp+CNkCAAAADnRSTlMARVn7B9cVoc/jIWtnJIM++AMAAADUSURBVDjLndRLEoMgEEVRPyCg+FAh+99qYqmAabFL7/hMaKCrN/VWyRZopbJ9ETUaWbq5RLXBX6YmSChcpMRZdRKX6e6kDAqZzAmNYlpEpnCTimfEbfWmhLlnZp8qmLY5a47pVY0oNIWArfV+h5Jy88FsNg2q3JTNRLIK8sd4hTZnwfmzSuVsmRdPFGV+d1S18QjJUQUZB5IcVVBxvMlRBRsvKzmq0JOr9y58yNU/eEj8s3zyyPkvcyQk9wH57/xwOfCrhl9cNMGswdQ4HEt1GKsXfQHGSThPkNi75AAAAABJRU5ErkJggg==') no-repeat;
  background-size: 34rpx;
}

.cart-view .item .cart-books {
  float: left;
  height: 204rpx;
  width: 672rpx;
  border-bottom: 1px solid #f4f4f4;
}

.cart-view .item .img {
  float: left;
  height: 145.83rpx;
  width: 145.83rpx;
  background: #f4f4f4;
  margin: 12.5rpx 18rpx 12.5rpx 0;
}

.cart-view .item .info {
  flex: 1;
  float: left;
  height: 145.83rpx;
  width: 453rpx;
  margin: 15.5rpx 26rpx 15.5rpx 0;
}

.cart-view .item .t {
  margin: 8rpx 0;
  height: 80rpx;
  font-size: 25rpx;
  color: #333;
}

.cart-view .item .name {
  /*height: 28rpx;
  max-width: 310rpx;
  line-height: 28rpx;
  font-size: 25rpx;
  color: #333;
  overflow: hidden;*/
  display: block;
  float: left;
  height: 33rpx;
  line-height: 33rpx;
  color: #333;
  font-size: 30rpx;
}

.cart-view .item .compusname {
  float: right;
  margin-left: 100rpx;
  height: 33rpx;
  line-height: 33rpx;
  color: 	#DC143C;
  font-size: 30rpx;
}

.cart-view .item .num {
  height: 28rpx;
  line-height: 28rpx;
  float: right;
}

.cart-view .item .attr {
  margin-bottom: 17rpx;
  height: 24rpx;
  line-height: 24rpx;
  font-size: 22rpx;
  color: #666;
  float: right;
}

.cart-view .item .b {
  height: 28rpx;
  line-height: 28rpx;
  font-size: 25rpx;
  color: #333;
  overflow: hidden;
}

.cart-view .item .price {
  float: left;
}

.cart-view .item .open {
  height: 28rpx;
  width: 150rpx;
  display: block;
  float: right;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAUBAMAAABL3sEiAAAAGFBMVEVmZmZ6enp7e3uDg4OEhITHx8fu7u7///8ummCmAAAARUlEQVQI12MoBwOGUjcwVcyUDqLKBNRAVHkSiMtQXqagBqLAXCAF4gIpEBdElSmYI1EQQagSiAaodohhYA7cIqi1EEcAAHB/LX9QfndHAAAAAElFTkSuQmCC') right center no-repeat;
  background-size: 25rpx;
  font-size: 25rpx;
  color: #333;
}

.cart-view .item.edit .num{
  display: none;
}

.cart-view .item.edit .attr {
  text-align: right;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAUBAMAAABL3sEiAAAAGFBMVEVmZmZ6enp7e3uDg4OEhITHx8fu7u7///8ummCmAAAARUlEQVQI12MoBwOGUjcwVcyUDqLKBNRAVHkSiMtQXqagBqLAXCAF4gIpEBdElSmYI1EQQagSiAaodohhYA7cIqi1EEcAAHB/LX9QfndHAAAAAElFTkSuQmCC') right center no-repeat;
  padding-right: 25rpx;
  background-size: 12rpx 20rpx;
  margin-bottom: 24rpx;
  height: 39rpx;
  line-height: 39rpx;
  font-size: 24rpx;
  color: #999;
  overflow: hidden;
}

.cart-view .item.edit .b {
  display: flex;
  height: 52rpx;
  overflow: hidden;
}

.cart-view .item.edit .price {
  line-height: 52rpx;
  height: 52rpx;
  flex: 1;
}

.cart-view .item .selnum {
  display: none;
}

.cart-view .item.edit .selnum {
  width: 235rpx;
  height: 52rpx;
  border: 1rpx solid #ccc;
  display: flex;
}

.selnum .cut {
  width: 70rpx;
  height: 100%;
  text-align: center;
  line-height: 50rpx;
}

.selnum .number {
  flex: 1;
  height: 100%;
  text-align: center;
  line-height: 68.75rpx;
  border-left: 1px solid #ccc;
  border-right: 1px solid #ccc;
  float: left;
}

.selnum .add {
  width: 80rpx;
  height: 100%;
  text-align: center;
  line-height: 50rpx;
}

.cart-view .group-item .header {
  width: 100%;
  height: 94rpx;
  line-height: 94rpx;
  padding: 0 26rpx;
  border-bottom: 1px solid #f4f4f4;
}

.cart-view .promotion .icon {
  display: inline-block;
  height: 24rpx;
  width: 15rpx;
}

.cart-view .promotion {
  margin-top: 25.5rpx;
  float: left;
  height: 43rpx;
  width: 480rpx;
  /*margin-right: 84rpx;*/
  line-height: 43rpx;
  font-size: 0;
}

.cart-view .promotion .tag {
  border: 1px solid #f48f18;
  height: 37rpx;
  line-height: 31rpx;
  padding: 0 9rpx;
  margin-right: 10rpx;
  color: #f48f18;
  font-size: 24.5rpx;
}

.cart-view .promotion .txt {
  height: 43rpx;
  line-height: 43rpx;
  padding-right: 10rpx;
  color: #333;
  font-size: 29rpx;
  overflow: hidden;
}

.cart-view .get {
  margin-top: 18rpx;
  float: right;
  height: 58rpx;
  padding-left: 14rpx;
  border-left: 1px solid #d9d9d9;
  line-height: 58rpx;
  font-size: 29rpx;
  color: #333;
}

.cart-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  height: 100rpx;
  width: 100%;
  background: #fff;
  display: flex;
}

.cart-bottom .checkbox {
  height: 34rpx;
  padding-left: 60rpx;
  line-height: 34rpx;
  margin: 33rpx 18rpx 33rpx 26rpx;
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACYAAAAmCAMAAACf4xmcAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABCUExURUdwTMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzAV+Z0EAAAAVdFJOUwAJ+KUEFTPay2bzRXdZ7RkhmJ6qJOWhY+QAAAEDSURBVDjLnZTplsIgDIUNWwK2tdt9/1cdxHGmVcAc+dH25Hw0+71cvjhztDIZM4mNc4txo+BwZKxSVwbSFoMn8iFuCeDrG0RLNkc6GGK+ttCZ8gIzuJcgBgPxJ4rB4T2OkM0HjgRyq8V7Y8i/3/V06YVb/nKECa0qBYPffB1jaFd8AD8+RrBrY8R41FkQew2MkPtrR6IeRglzoW1/HrbizfZ9Pv8jCH0slOAm+D7mMeUn4PoYwegxpVNlCsqCKMurbJay9R8GyT0HSTmWeciTYsh7K+MPK1MW0H9eQOU652sqcch+15rUrFQXLpuFy7ksXLYuXDUZbBZ9v4sqiqju34jyD97JD4dkfgo1AAAAAElFTkSuQmCC') no-repeat;
  background-size: 34rpx;
  font-size: 29rpx;
}

.cart-bottom .checkbox.checked {
  background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACYAAAAmCAMAAACf4xmcAAAAQlBMVEUAAACrKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyurKyvw19exOzv////z4uK1Q0Pt0dGxOjp+CNkCAAAADnRSTlMARVn7B9cVoc/jIWtnJIM++AMAAADUSURBVDjLndRLEoMgEEVRPyCg+FAh+99qYqmAabFL7/hMaKCrN/VWyRZopbJ9ETUaWbq5RLXBX6YmSChcpMRZdRKX6e6kDAqZzAmNYlpEpnCTimfEbfWmhLlnZp8qmLY5a47pVY0oNIWArfV+h5Jy88FsNg2q3JTNRLIK8sd4hTZnwfmzSuVsmRdPFGV+d1S18QjJUQUZB5IcVVBxvMlRBRsvKzmq0JOr9y58yNU/eEj8s3zyyPkvcyQk9wH57/xwOfCrhl9cNMGswdQ4HEt1GKsXfQHGSThPkNi75AAAAABJRU5ErkJggg==') no-repeat;
  background-size: 34rpx;
}

.cart-bottom .total {
  height: 34rpx;
  flex: 1;
  margin: 33rpx 10rpx;
  font-size: 29rpx;
}

.cart-bottom .delete {
  text-align: center;
  width: 180rpx;
  height: 60rpx;
  line-height: 82rpx;
  padding: 0;
  margin: 0;
  margin-left: -5rpx;
  padding-right: 25rpx;
  font-size: 25rpx;
  color: #f4f4f4;
  /* text-align: center; */
  border-top-left-radius: 0rpx;
  border-bottom-left-radius: 0rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #9a9ba1 0%, #ae8b9c 100%);
}

.cart-bottom .checkout {
  height: 100rpx;
  width: 210rpx;
  text-align: center;
  line-height: 100rpx;
  font-size: 29rpx;
  background: #b4282d;
  color: #fff;
}

.action_btn_area {
  /* border: 1px solid #333; */
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  right: 0;
  top: 0;
  width: 380rpx;
  height: 100rpx;
}

.action_btn_area .edit {
  width: 140rpx;
  /* border: 1px solid #000; */
  height: 70rpx;
  line-height: 70rpx;
  padding: 0;
  margin: 0;
  margin-right: 5rpx;
  text-align: center;
  /* padding-left: 25rpx; */
  font-size: 25rpx;
  color: #f4f4f4;
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  /* background-image: linear-gradient(to right, #ff7701 100%); */
  background-image: linear-gradient(to right, #f8b230 0%, #ee9e09 100%);
}

.action_btn_area .checkout {
  width: 140rpx;
  height: 70rpx;
  line-height: 70rpx;
  padding: 0;
  margin: 0;
  margin-left: 5rpx;
  /* padding-right: 25rpx; */
  font-size: 25rpx;
  color: #f4f4f4;
  text-align: center;
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #ce373c 0%, #b4282d 100%);
}

.action_btn_area .delete {
  width: 140rpx;
  /* border: 1px solid #000; */
  height: 70rpx;
  line-height: 70rpx;
  padding: 0;
  margin: 0;
  margin-right: 5rpx;
  text-align: center;
  padding-left: -5rpx;
  font-size: 25rpx;
  color: #f4f4f4;
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #ff7701 0%, #fe4800 100%);
}

.action_btn_area .sure {
  text-align: center;
  width: 140rpx;
  height: 70rpx;
  line-height: 70rpx;
  padding: 0;
  margin: 0;
  margin-right: 10rpx;
  padding-left: -5rpx;
  font-size: 25rpx;
  color: #f4f4f4;
  /* text-align: center; */
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #f8b230 0%, #ee9e09 100%);
  /* background-image: linear-gradient(to right, #ff7701 0%, #fe4800 100%); */
}

.auth_btn {
  position: fixed;
  top: 55vh;
  left: 10vw;
  width: 80vw;
  height: 96rpx;
  line-height: 96rpx;
  font-size: 25rpx;
  color: #f4f4f4;
  /* text-align: center; */
  border-top-left-radius: 50rpx;
  border-bottom-left-radius: 50rpx;
  border-top-right-radius: 50rpx;
  border-bottom-right-radius: 50rpx;
  letter-spacing: 3rpx;
  background-image: linear-gradient(to right, #f8b230 0%, #ee9e09 100%);
}

</style>
