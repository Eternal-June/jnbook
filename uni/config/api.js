// 以下是业务服务器API地址
var WxApiRoot = 'http://localhost:8080/demo/wx/';
//47.102.194.182
module.exports = {
  IndexUrl: WxApiRoot + 'home/index',
  //首页数据接口
  SchoolUrl: WxApiRoot + 'home/school',
  //同校数据接口
  SchoolBooksCount: WxApiRoot + 'books/schoolCount',
  //统计书籍总数
  CatalogList: WxApiRoot + 'catalog/index',
  //分类目录全部分类数据接口
  CatalogCurrent: WxApiRoot + 'catalog/current',
  //分类目录当前分类数据接口
  AuthLoginByWeixin: WxApiRoot + 'auth/login_by_weixin',
  //微信登录
  AuthLoginByAccount: WxApiRoot + 'auth/login',
  //账号登录
  AuthLogout: WxApiRoot + 'auth/logout',
  //账号登出
  AuthRegister: WxApiRoot + 'auth/register',
  //账号注册
  AuthReset: WxApiRoot + 'auth/reset',
  //账号密码重置
  AuthRegisterCaptcha: WxApiRoot + 'auth/regCaptcha',
  //验证码
  AuthBindPhone: WxApiRoot + 'auth/bindPhone',
  //绑定微信手机号
  BooksSell: WxApiRoot + 'books/sell',
  //卖书
  BookContact: WxApiRoot + 'books/contact',
  //联系卖家
  BooksCount: WxApiRoot + 'books/count',
  //统计书籍总数
  BooksList: WxApiRoot + 'books/list',
  //统计书籍总数
  BooksListSchool: WxApiRoot + 'books/listschool',
  //获得书籍列表
  BooksCategory: WxApiRoot + 'books/category',
  //获得分类数据
  BooksDetail: WxApiRoot + 'books/detail',
  //获得书籍的详情
  BooksRelated: WxApiRoot + 'books/related',
  //品牌详情
  CartList: WxApiRoot + 'cart/index',
  //获取购物车的数据
  CartAdd: WxApiRoot + 'cart/add',
  // 添加书籍到购物车
  CartFastAdd: WxApiRoot + 'cart/fastadd',
  // 立即购买书籍
  CartUpdate: WxApiRoot + 'cart/update',
  // 更新购物车的书籍
  CartDelete: WxApiRoot + 'cart/delete',
  // 删除购物车的书籍
  CartChecked: WxApiRoot + 'cart/checked',
  // 选择或取消选择书籍
  CartBooksCount: WxApiRoot + 'cart/bookscount',
  // 获取购物车书籍件数
  CartCheckout: WxApiRoot + 'cart/checkout',
  // 下单前信息确认
  CollectList: WxApiRoot + 'collect/list',
  //收藏列表
  CollectAddOrDelete: WxApiRoot + 'collect/addordelete',
  //添加或取消收藏
  CommentList: WxApiRoot + 'comment/list',
  //评论列表
  CommentCount: WxApiRoot + 'comment/count',
  //评论总数
  CommentPost: WxApiRoot + 'comment/post',
  //发表评论
  TopicList: WxApiRoot + 'topic/list',
  //专题列表
  TopicDetail: WxApiRoot + 'topic/detail',
  //专题详情
  TopicRelated: WxApiRoot + 'topic/related',
  //相关专题
  SearchIndex: WxApiRoot + 'search/index',
  //搜索关键字
  SearchIndexSchool: WxApiRoot + 'search/indexschool',
  //搜索关键字
  SearchResult: WxApiRoot + 'search/result',
  //搜索结果
  SearchHelper: WxApiRoot + 'search/helper',
  //搜索帮助
  SearchClearHistory: WxApiRoot + 'search/clearhistory',
  //搜索历史清楚
  AddressList: WxApiRoot + 'address/list',
  //收货地址列表
  AddressDetail: WxApiRoot + 'address/detail',
  //收货地址详情
  AddressSave: WxApiRoot + 'address/save',
  //保存收货地址
  AddressDelete: WxApiRoot + 'address/delete',
  //保存收货地址
  ExpressQuery: WxApiRoot + 'express/query',
  //物流查询
  RegionList: WxApiRoot + 'region/list',
  //获取区域列表
  OrderSubmit: WxApiRoot + 'order/submit',
  // 提交订单
  OrderPrepay: WxApiRoot + 'order/prepay',
  //模拟付款
  OrderImitatepay: WxApiRoot + 'order/imitatepay',
  // 订单的预支付会话
  OrderList: WxApiRoot + 'order/list',
  //订单列表
  OrderDetail: WxApiRoot + 'order/detail',
  //订单详情
  ExpressTrace: WxApiRoot + 'order/expressTrace',
  //订单物流
  OrderCancel: WxApiRoot + 'order/cancel',
  //取消订单
  OrderRefund: WxApiRoot + 'order/refund',
  //退款取消订单
  OrderDelete: WxApiRoot + 'order/delete',
  //删除订单
  OrderConfirm: WxApiRoot + 'order/confirm',
  //确认收货
  OrderBooks: WxApiRoot + 'order/books',
  // 代评价书籍信息
  OrderComment: WxApiRoot + 'order/comment',
  // 评价订单书籍信息
  FeedbackAdd: WxApiRoot + 'feedback/submit',
  //添加反馈
  FootprintList: WxApiRoot + 'footprint/list',
  //足迹列表
  FootprintDelete: WxApiRoot + 'footprint/delete',
  //删除足迹
  StorageUpload: WxApiRoot + 'storage/upload',
  //图片上传,
  UserIndex: WxApiRoot + 'user/index',
  //获取我卖过的图书
  BooksSelled: WxApiRoot + 'user/sell',
  //修改用户个人信息
  UserEditInfo: WxApiRoot +'user/editInfo',
  //佣金账号提现记录
  ArticleDetail: WxApiRoot + 'article/detail',
  //公告详情
  ApplyAgency: WxApiRoot + 'user/applyAgency',
  //代理申请
};
