<template>
  <view>
    <view class="user">
<view class="wrapper">
 <view class="myService">
 <head style="font-size:4ex">江南E书APP(2021年5月)</head>

 <text style="font-size:2ex">
目录
一. 江南E书App
1. 软件概述
2. 运行环境
3. 使用方法
   3.1 软件安装与运行
   3.2 用户注册与登录
   3.3 图书商城
    3.3.1书记搜索
    3.3.2书籍分类
    3.3.3书籍浏览
3.4 书籍购买
3.5书籍售卖
二.江南E书管理后台
1.首页
2.用户管理
3.分类管理
4.旧书管理
5.推广管理
6.系统管理
7.统计
</text>
<head style="font-size:3ex">一、江南E书APP</head>
<text style="font-size:2ex">
1.	软件概述
江南E书是一个基于uni-app的二手书交易平台，通过江南E书让买家和卖家交流实现交易，将旧书循环利用，减轻学生的经济负担，减少资源的浪费。
 本软件主要实现旧书出售，旧书购买，旧书查询，旧书浏览，旧书分类，卖家发布旧书，后台账户书籍管理。
2.	运行环境
本软件能在Android或IOS手机上运行，亦可以通过各种小程序打开。
3.	使用方法
3.1软件安装与运行
下载江南E书的apk文件，点击图标即可运行.
3.2用户注册登录
打开App后，进入个人中心界面，点击右上角的设置按钮，进入登陆界面.
登陆界面左下角即可用邮箱注册账号，注册之后即可用用户名登录。在设置界面中亦可修改自己的个人信息
3.3 图书商城
3.3.1 书籍搜索
在首页，分类，同校界面上方都有搜索栏，点击进入搜索界面.
输入书名或者书名中的关键字即可查询图书。另外，在搜索栏的下方还列出历史搜索记录(可删除)和热门搜索书籍。
3.3.2 书籍分类
在主页界面中间列出了书籍的大致分类，分别是法律、工业、医学、历史、文学、计算机科学、艺术、军事以及更多分类，在分类界面中还有更具体的分类，点击具体分类图标，即可浏览该分类的全部书籍.
3.3.3 书籍浏览
在首页界面上方是系统公告，首页界面下方，列出了推荐书单、首发新品、和人气推荐,在找到选中的书籍后，点击进入书籍的详情界面，该界面显示书籍的封面，价格，图书参数，常见问题，以及书籍推荐等，还可以联系客服，收藏这本书，或者加入购物单.在个人中心界面中也可以查询自己的浏览记录，和收集的书籍
3.4 书籍购买
在书籍详情界面，点击购买选项，选中地址，标准和数量后，即可购买，或加入购物车后，在购物车界面中选择购买。
地址可在购买时添加，或者可在个人中心界面中我的服务里的地址管理中添加修改。
购买后订单可在个人中心界面中的我的订单处查询，包含了待付款，待发货，待收货及待评价订单。
3.5 书籍售卖
在个人中心界面中，点击我要卖书，进入后台网站，在旧书管理中选择上架旧书，在该界面中即可编辑你所要售卖书籍的详细信息。

</text>

<head style="font-size:3ex">二、 江南E书管理后台</head>

<text style="font-size:2ex">
在App的个人中心界面，点击我要卖书，即可进入后台网站，或者直接打开Hazesky.fun。账号密码和App的登录账号密码相同。
1.首页
界面主要内容是对用户、书籍、库存、订单的数量统计。
2.用户管理
在会员管理界面，可以查看各个用户的具体身份信息。
收货地址界面，则囊括了所有用户的收货地址信息。
也有对用户收藏的书籍，浏览记录、搜索历史和用户意见反馈的统计.各个界面都能通过用户ID或者其他具体ID来搜索，还可以将各个数据列表下载导出。
3.分类管理
分类管理分为行政区域、书籍类目、通用问题，关键词四个界面。
行政区域:列表中含有大部分城市的名称及区域编码。
书籍类目：存储了书籍类目的ID、名称、图标、及父类ID
通用问题：囊括了用户购买时常遇到的问题及回复。
关键词：可对搜索关键词，及其会跳转的链接进行修改
各个界面都能通过界面上方搜索栏来搜索，还可以将各个数据列表下载导出。
4.	旧书管理
旧书管理提供订单管理、旧书列表、上架旧书、查看评论功能。
订单管理：能够查看用户的订单的状态，金额，物流单号，支付时间等。
旧书列表界面能够查看旧书库存，评论界面则可以查看各个用户在不同时间所发表的评论。上架旧书界面，卖家在该界面中即可编辑所要售卖书籍的详细信息。
5.推广管理
推广管理界面主要对App首页界面中的公告，广告，书籍专题进行管理
6.系统管理
本界面中主要存储了管理员、普通用户等角色管理，另外存储了APP中所有的图标图片
7.统计
对每月增长的用户进行了统计，并列出了条形图，
对每个月的订单量，下单用户，订单总额、客单价进行统计，并画出了折线图。
对订单量，下单货品数量和下单货品总额进行统计，
</text>
 </view>
</view>



</view>
</view>
</template>

<style>

page{
  height: 100%;
  background-color: #F2f2f2;
}
.page-view{
  height: 100%;
}
.banner-image{
  width: 100%;
  height: 350rpx;
  background: #ee1;
  margin-bottom: 30rpx;
  border-bottom: solid #f2f2f2 0.5dp;
}

.about-item{
  background: white;
  border-top: solid #f2f2f2 0.2rpx;
  border-bottom: solid #f2f2f2 0.2rpx;
  width: 100%;
  height: 100rpx;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.item-left{
  font-size: 30rpx;
  margin-left: 15rpx;
  margin-top: auto;
  margin-bottom: auto;
}
.item-right{

  margin-right: 15rpx;
  margin-top: auto;
  margin-bottom: auto;
}
.right-icon{
  width: 40rpx;
  height: 40rpx;
}


a-new .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 0 31rpx 45rpx 31rpx;
}

.a-new .b .item {
  float: left;
  width: 324rpx;
  background: #f9f9f9;
  margin: 11rpx;
}

.a-new .b .item-b {
  margin-left: 42rpx;
}

.a-new .b .img {
  width: 324rpx;
  height: 324rpx;
}

.a-new .b .name {
  text-align: center;
  display: block;
  width: 324rpx;
  height: 42rpx;
  padding: 10rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.a-new .b .price {
  display: block;
  text-align: center;
  line-height: 30rpx;
  padding-bottom: 25rpx;
}

.book-grid .b {
  width: 750rpx;
  padding: 0 6.25rpx;
  height: auto;
  overflow: hidden;
}

.book-grid .b .item {
  float: left;
  background: #fff;
  width: 365rpx;
  margin-bottom: 6.25rpx;
  height: 452rpx;
  overflow: hidden;
  text-align: center;
}

.book-grid .b .item .a {
  height: 452rpx;
  width: 100%;
}

.book-grid .b .item-b {
  margin-left: 6.25rpx;
}
.user .header {
  padding: 0 30rpx;
  height: 190rpx;
  position: relative;
}
.user .header:after {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -98rpx;
  z-index: -1;
  content: "";
  height: 100rpx;
  width: 100%;
  border-radius: 0 0 50% 50%;
  background-color: #388ceb;
}
.user .header .picTxt .pictrue {
  width: 120rpx;
  height: 120rpx;
}
.user .header .picTxt .pictrue image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #f5f5f5;
}
.user .header .picTxt .text {
  width: 434rpx;
  color: rgba(255, 255, 255, 1);
  margin-left: 35rpx;
}
.user .header .picTxt .text .name {
  color: #fff;
  font-size: 36rpx;
  max-width: 470rpx;
}
.user .header .picTxt .text .member {
  padding: 0 10rpx;
  height: 36rpx;
  background-color: rgba(0, 0, 0, 0.2);
  font-size: 20rpx;
  border-radius: 30rpx;
  margin-left: 17rpx;
}
.user .header .picTxt .text .member image {
  width: 22rpx;
  height: 22rpx;
  font-size: 20rpx;
  margin-right: 8rpx;
}
.user .header .picTxt .text .id {
  color: rgba(255, 255, 255, 0.6);
  font-size: 26rpx;
  margin-top: 15rpx;
}
.user .header .picTxt .text .id .iconfont {
  font-size: 30rpx;
  margin-left: 12rpx;
}
.user .header .icon-shezhi {
  font-size: 38rpx;
  color: #fff;
  margin-top: 15rpx;
  margin-right: 30rpx;
}
.user .wrapper {
  padding: 0 20rpx;
}
.user .wrapper .nav {
  background-color: #fff;
  border-radius: 20rpx;
  height: 140rpx;
}
.user .wrapper .nav .item {
  flex: 1;
  text-align: center;
}
.user .wrapper .nav .item ~ .item {
  border-left: 1px solid #eee;
}
.user .wrapper .nav .item .title {
  font-size: 28rpx;
  color: #666;
}
.user .wrapper .nav .item .num {
  margin-top: 16rpx;
  font-size: 36rpx;
  color: rgb(223, 8, 8);
}
.user .wrapper .nav .item .text {
  margin-top: 16rpx;
  font-size: 30rpx;
}
.user .wrapper .myOrder {
  background-color: #fff;
  border-radius: 10rpx;
  margin-top: 15rpx;
}
.user .wrapper .myOrder .title,
.user .wrapper .myService .title {
  height: 88rpx;
  padding: 0 30rpx;
  border-bottom: 1px dashed #ddd;
  font-size: 30rpx;
  color: #282828;
}
.user .wrapper .myOrder .title .allOrder {
  font-size: 26rpx;
  color: #666;
}
.user .wrapper .myOrder .title .allOrder .iconfont {
  font-size: 30rpx;
  margin-left: 7rpx;
}
.user .wrapper .myOrder .orderState {
  height: 160rpx;
}
.user .wrapper .myOrder .orderState .item {
  font-size: 26rpx;
  color: #454545;
  flex: 1;
  text-align: center;
}
.user .wrapper .myOrder .orderState .item .pictrue {
  width: 64rpx;
  height: 64rpx;
  margin: 0 auto 18rpx auto;
}
.user .wrapper .myOrder .orderState .item .pictrue image {
  width: 100%;
  height: 100%;
}
.user .wrapper .myService {
  background-color: #fff;
  margin-top: 15rpx;
  border-radius: 10rpx;
}
.user .wrapper .myService .serviceList {
  padding: 8rpx 0 27rpx 0;
}
.user .wrapper .myService .serviceList .item {
  width: 25%;
  text-align: center;
  font-size: 26rpx;
  color: #333;
  margin-top: 30rpx;
}
.user .wrapper .myService .serviceList .item .pictrue {
  width: 64rpx;
  height: 64rpx;
  margin: 0 auto 16rpx auto;
}
.user .wrapper .myService .serviceList .item .pictrue image {
  width: 100%;
  height: 100%;
}
.user .support {
  width: 219rpx;
  height: 74rpx;
  margin: 54rpx auto;
  display: block;
}
.item_badge {
  height: 32rpx;
  width: 32rpx;
  position: absolute;
  background: #b4282d;
  color: #fff;
  font-size: 28rpx;
  border-radius: 50%;
  margin-top: 0rpx;
  margin-left: 40rpx;
}

.a-section {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  background: #fff;
  margin-top: 20rpx;
}

.a-section .h {
  display: flex;
  flex-flow: row nowrap;
  align-items: center;
  justify-content: center;
  height: 130rpx;
}

.a-section .h .txt {
  padding-right: 30rpx;
  background-size: 16.656rpx 27rpx;
  display: inline-block;
  height: 36rpx;
  line-height: 36rpx;
}

.a-new .b {
  width: 750rpx;
  height: auto;
  overflow: hidden;
  padding: 0 31rpx 45rpx 31rpx;
}

.a-new .b .item {
  float: left;
  width: 324rpx;
  background: #f9f9f9;
  margin: 11rpx;
}

.a-new .b .item-b {
  margin-left: 42rpx;
}

.a-new .b .img {
  width: 324rpx;
  height: 324rpx;
}

.a-new .b .name {
  text-align: center;
  display: block;
  width: 324rpx;
  height: 42rpx;
  padding: 10rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.a-new .b .price {
  display: block;
  text-align: center;
  line-height: 30rpx;
  padding-bottom: 25rpx;
}

.container .a-section .title {
  height: 120rpx;
  border-top: 0rpx solid #eee;
  padding-top: 14rpx;
  margin: 0 30rpx;
}
.container .a-section .title .text {
  width: 530rpx;
}
.container .a-section .title .text .name {
  margin-bottom: 5rpx;
  position: relative;
}
.container .a-section .title .text .name .new {
  position: absolute;
  top: 6rpx;
  left: 210rpx;
  font-size: 20rpx;
  font-weight: bold;
}
.container .a-section .title .more .iconfont {
  margin-left: 9rpx;
  font-size: 26rpx;
  vertical-align: 3rpx;
}
.container .a-section .title .text .name .new {
  position: absolute;
  top: 6rpx;
  left: 210rpx;
  font-size: 20rpx;
  font-weight: bold;
}
.a-popular .b .desc {
  margin-top: 18rpx;
  width: 436rpx;
  display: block;
  line-height: 38rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
.a-topic .b .desc {
  display: block;
  height: 30rpx;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.container .a-section .title .more .iconfont {
  margin-left: 9rpx;
  font-size: 26rpx;
  vertical-align: 3rpx;
}
.container .a-section .hot-bg .title .more {
  font-size: 26rpx;
  color: #fff;
}
.container .a-section .hot-bg .title .more .iconfont {
  font-size: 25rpx;
  vertical-align: 2rpx;
  margin-left: 10rpx;
  color: #fff;
}
</style>
