<template>
<view>
<view class="add-address">
  <view class="add-form">
    <view class="form-item">
      <input class="input" @input="bindinputName" placeholder="姓名" :value="address.name" auto-focus></input>
    </view>
    <view class="form-item">
      <input class="input" @input="bindinputMobile" :value="address.mobile" placeholder="手机号码"></input>
    </view>
    <view class="form-item">
      <input class="input" :value="address.provinceName +  address.cityName + address.areaName" disabled="true" @tap="chooseRegion" placeholder="省份、城市、区县"></input>
    </view>
    <view class="form-item">
      <input class="input" @input="bindinputAddress" :value="address.address" placeholder="详细地址, 如街道、楼盘号等"></input>
    </view>
    <view class="form-default">
      <text @tap="bindIsDefault" :class="'default-input ' + (address.isDefault == 1 ? 'selected' : '')">设为默认地址</text>
    </view>
  </view>

  <view class="btns">
    <button class="cannel" @tap="cancelAddress">取消</button>
    <button class="save" @tap="saveAddress">保存</button>
  </view>

  <view class="region-select" v-if="openSelectRegion">
    <view class="hd">
      <view class="region-selected">
        <view v-for="(item, index) in selectRegionList" :key="index" :class="'item ' + (item.id == 0 ? 'disabled' : '') + ' ' + ((regionType -1) === index ? 'selected' : '')" @tap="selectRegionType" :data-region-type-index="index">{{item.name}}</view>
      </view>
      <view :class="'done ' + (selectRegionDone ? '' : 'disabled')" @tap="doneSelectRegion">确定</view>
    </view>
    <view class="bd">
      <scroll-view scroll-y class="region-list">
        <view v-for="(item, index) in regionList" :key="index" :class="'item ' + (item.selected ? 'selected' : '')" @tap="selectRegion" :data-region-index="index">{{item.name}}</view>
      </scroll-view>
    </view>
  </view>
</view>
<view class="bg-mask" @tap="cancelSelectRegion" v-if="openSelectRegion"></view>
</view>
</template>

<script>
var util = require("../../../utils/util.js");
var api = require("../../../config/api.js");
var check = require("../../../utils/check.js");
var app = getApp();

export default {
  data() {
    return {
      address: {
        id: 0,
        provinceId: 0,
        cityId: 0,
        areaId: 0,
        address: '',
        name: '',
        mobile: '',
        isDefault: 0,
        provinceName: '',
        cityName: '',
        areaName: ''
      },
      addressId: 0,
      openSelectRegion: false,
      selectRegionList: [{
        id: 0,
        name: '省份',
        pid: 1,
        type: 1
      }, {
        id: 0,
        name: '城市',
        pid: 1,
        type: 2
      }, {
        id: 0,
        name: '区县',
        pid: 1,
        type: 3
      }],
      regionType: 1,
      regionList: [],
      selectRegionDone: false
    };
  },

  components: {},
  props: {},
  onLoad: function (options) {
    // 页面初始化 options为页面跳转所带来的参数
    console.log(options);

    if (options.id && options.id != 0) {
      this.setData({
        addressId: options.id
      });
      this.getAddressDetail();
    }
  },
  onReady: function () {},
  onShow: function () {// 页面显示
  },
  onHide: function () {// 页面隐藏
  },
  onUnload: function () {// 页面关闭
  },
  methods: {
    bindinputMobile(event) {
      let address = this.address;
      address.mobile = event.detail.value;
      this.setData({
        address: address
      });
    },

    bindinputName(event) {
      let address = this.address;
      address.name = event.detail.value;
      this.setData({
        address: address
      });
    },

    bindinputAddress(event) {
      let address = this.address;
      address.address = event.detail.value;
      this.setData({
        address: address
      });
    },

    bindIsDefault() {
      let address = this.address;
      address.isDefault = !address.isDefault;
      this.setData({
        address: address
      });
    },

    getAddressDetail() {
      let that = this;
      util.request(api.AddressDetail, {
        id: that.addressId
      }).then(function (res) {
        if (res.errno === 0) {
          if (res.data) {
            that.setData({
              address: res.data
            });
          }
        }
      });
    },

    setRegionDoneStatus() {
      let that = this;
      let doneStatus = that.selectRegionList.every(item => {
        return item.id != 0;
      });
      that.setData({
        selectRegionDone: doneStatus
      });
    },

    chooseRegion() {
      let that = this;
      this.setData({
        openSelectRegion: !this.openSelectRegion
      }); //设置区域选择数据

      let address = this.address;

      if (address.provinceId > 0 && address.cityId > 0 && address.areaId > 0) {
        let selectRegionList = this.selectRegionList;
        selectRegionList[0].id = address.provinceId;
        selectRegionList[0].name = address.provinceName;
        selectRegionList[0].pid = 0;
        selectRegionList[1].id = address.cityId;
        selectRegionList[1].name = address.cityName;
        selectRegionList[1].pid = address.provinceId;
        selectRegionList[2].id = address.areaId;
        selectRegionList[2].name = address.areaName;
        selectRegionList[2].pid = address.cityId;
        this.setData({
          selectRegionList: selectRegionList,
          regionType: 3
        });
        this.getRegionList(address.cityId);
      } else {
        this.setData({
          selectRegionList: [{
            id: 0,
            name: '省份',
            pid: 0,
            type: 1
          }, {
            id: 0,
            name: '城市',
            pid: 0,
            type: 2
          }, {
            id: 0,
            name: '区县',
            pid: 0,
            type: 3
          }],
          regionType: 1
        });
        this.getRegionList(0);
      }

      this.setRegionDoneStatus();
    },

    selectRegionType(event) {
      let that = this;
      let regionTypeIndex = event.target.dataset.regionTypeIndex;
      let selectRegionList = that.selectRegionList; //判断是否可点击

      if (regionTypeIndex + 1 == this.regionType || regionTypeIndex - 1 >= 0 && selectRegionList[regionTypeIndex - 1].id <= 0) {
        return false;
      }

      this.setData({
        regionType: regionTypeIndex + 1
      });
      let selectRegionItem = selectRegionList[regionTypeIndex];
      this.getRegionList(selectRegionItem.pid);
      this.setRegionDoneStatus();
    },

    selectRegion(event) {
      let that = this;
      let regionIndex = event.target.dataset.regionIndex;
      let regionItem = this.regionList[regionIndex];
      let regionType = regionItem.type;
      let selectRegionList = this.selectRegionList;
      selectRegionList[regionType - 1] = regionItem;

      if (regionType != 3) {
        this.setData({
          selectRegionList: selectRegionList,
          regionType: regionType + 1
        });
        this.getRegionList(regionItem.id);
      } else {
        this.setData({
          selectRegionList: selectRegionList
        });
      } //重置下级区域为空


      selectRegionList.map((item, index) => {
        if (index > regionType - 1) {
          item.id = 0;
          item.name = index == 1 ? '城市' : '区县';
          item.pid = 0;
        }

        return item;
      });
      this.setData({
        selectRegionList: selectRegionList
      });
      that.setData({
        regionList: that.regionList.map(item => {
          //标记已选择的
          if (that.regionType == item.type && that.selectRegionList[that.regionType - 1].id == item.id) {
            item.selected = true;
          } else {
            item.selected = false;
          }

          return item;
        })
      });
      this.setRegionDoneStatus();
    },

    doneSelectRegion() {
      if (this.selectRegionDone === false) {
        return false;
      }

      let address = this.address;
      let selectRegionList = this.selectRegionList;
      address.provinceId = selectRegionList[0].id;
      address.cityId = selectRegionList[1].id;
      address.areaId = selectRegionList[2].id;
      address.provinceName = selectRegionList[0].name;
      address.cityName = selectRegionList[1].name;
      address.areaName = selectRegionList[2].name;
      this.setData({
        address: address,
        openSelectRegion: false
      });
    },

    cancelSelectRegion() {
      this.setData({
        openSelectRegion: false,
        regionType: this.regionDoneStatus ? 3 : 1
      });
    },

    getRegionList(regionId) {
      let that = this;
      let regionType = that.regionType;
      util.request(api.RegionList, {
        pid: regionId
      }).then(function (res) {
        if (res.errno === 0) {
          that.setData({
            regionList: res.data.map(item => {
              //标记已选择的
              if (regionType == item.type && that.selectRegionList[regionType - 1].id == item.id) {
                item.selected = true;
              } else {
                item.selected = false;
              }

              return item;
            })
          });
        }
      });
    },

    cancelAddress() {
      uni.navigateBack();
    },

    saveAddress() {
      console.log(this.address);
      let address = this.address;

      if (address.name == '') {
        util.showErrorToast('请输入姓名');
        return false;
      }

      if (address.mobile == '') {
        util.showErrorToast('请输入手机号码');
        return false;
      }

      if (address.areaId == 0) {
        util.showErrorToast('请输入省市区');
        return false;
      }

      if (address.address == '') {
        util.showErrorToast('请输入详细地址');
        return false;
      }

      if (!check.isValidPhone(address.mobile)) {
        util.showErrorToast('手机号不正确');
        return false;
      }

      let that = this;
      util.request(api.AddressSave, {
        id: address.id,
        name: address.name,
        mobile: address.mobile,
        provinceId: address.provinceId,
        cityId: address.cityId,
        areaId: address.areaId,
        address: address.address,
        isDefault: address.isDefault
      }, 'POST').then(function (res) {
        if (res.errno === 0) {
          //返回之前，先取出上一页对象，并设置addressId
          var pages = getCurrentPages();
          var prevPage = pages[pages.length - 2];
          console.log(prevPage);

          if (prevPage.route == "pages/checkout/checkout") {
            prevPage.setData({
              addressId: res.data
            });

            try {
              uni.setStorageSync('addressId', res.data);
            } catch (e) {}

            console.log("set address");
          }

          uni.navigateBack();
        }
      });
    }

  }
};
</script>
<style>
page {
  height: 100%;
  background: #f4f4f4;
}

.add-address .add-form {
  background: #fff;
  width: 100%;
  height: auto;
  overflow: hidden;
}

.add-address .form-item {
  height: 116rpx;
  padding-left: 31.25rpx;
  border-bottom: 1px solid #d9d9d9;
  display: flex;
  align-items: center;
  padding-right: 31.25rpx;
}

.add-address .input {
  flex: 1;
  height: 44rpx;
  line-height: 44rpx;
  overflow: hidden;
}

.add-address .form-default {
  border-bottom: 1px solid #d9d9d9;
  height: 96rpx;
  background: #fafafa;
  padding-top: 28rpx;
  font-size: 28rpx;
}

.default-input {
  margin: 0 auto;
  display: block;
  width: 240rpx;
  height: 40rpx;
  padding-left: 50rpx;
  line-height: 40rpx;
  background: url("http://yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/sprites/checkbox-sed825af9d3-a6b8540d42.png") 1rpx -448rpx no-repeat;
  background-size: 38rpx 486rpx;
  font-size: 28rpx;
}

.default-input.selected {
  background: url("http://yanxuan.nosdn.127.net/hxm/yanxuan-wap/p/20161201/style/img/sprites/checkbox-sed825af9d3-a6b8540d42.png") 0 -192rpx no-repeat;
  background-size: 38rpx 486rpx;
}

.add-address .btns {
  position: fixed;
  bottom: 0;
  left: 0;
  overflow: hidden;
  display: flex;
  height: 100rpx;
  width: 100%;
}

.add-address .cannel, .add-address .save {
  flex: 1;
  height: 100rpx;
  text-align: center;
  line-height: 100rpx;
  font-size: 28rpx;
  color: #fff;
  border: none;
  border-radius: 0;
}

.add-address .cannel {
  background: #333;
}

.add-address .save {
  background: #b4282d;
}

.region-select {
  width: 100%;
  height: 600rpx;
  background: #fff;
  position: fixed;
  z-index: 10;
  left: 0;
  bottom: 0;
}

.region-select .hd {
  height: 108rpx;
  width: 100%;
  border-bottom: 1px solid #f4f4f4;
  padding: 46rpx 30rpx 0 30rpx;
}

.region-select .region-selected {
  float: left;
  height: 60rpx;
  display: flex;
}

.region-select .region-selected .item {
  max-width: 140rpx;
  margin-right: 30rpx;
  text-align: left;
  line-height: 60rpx;
  height: 100%;
  color: #333;
  font-size: 28rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.region-select .region-selected .item.disabled {
  color: #999;
}

.region-select .region-selected .item.selected {
  color: #b4282d;
}

.region-select .done {
  float: right;
  height: 60rpx;
  width: 60rpx;
  border: none;
  background: #fff;
  line-height: 60rpx;
  text-align: center;
  color: #333;
  font-size: 28rpx;
}

.region-select .done.disabled {
  color: #999;
}

.region-select .bd {
  height: 492rpx;
  width: 100%;
  padding: 0 30rpx;
}

.region-select .region-list {
  height: 492rpx;
}

.region-select .region-list .item {
  width: 100%;
  height: 104rpx;
  line-height: 104rpx;
  text-align: left;
  color: #333;
  font-size: 28rpx;
}

.region-select .region-list .item.selected {
  color: #b4282d;
}

.bg-mask {
  height: 100%;
  width: 100%;
  background: rgba(0, 0, 0, 0.4);
  position: fixed;
  top: 0;
  left: 0;
  z-index: 8;
}

</style>