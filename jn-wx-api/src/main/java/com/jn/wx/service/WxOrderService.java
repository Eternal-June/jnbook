package com.jn.wx.service;

import static com.jn.wx.util.WxResponseCode.AUTH_OPENID_UNACCESS;
import static com.jn.wx.util.WxResponseCode.ORDER_COMMENTED;
import static com.jn.wx.util.WxResponseCode.ORDER_COMMENT_EXPIRED;
import static com.jn.wx.util.WxResponseCode.ORDER_CONFIRM_OPERATION;
import static com.jn.wx.util.WxResponseCode.ORDER_DEL_OPERATION;
import static com.jn.wx.util.WxResponseCode.ORDER_INVALID;
import static com.jn.wx.util.WxResponseCode.ORDER_INVALID_OPERATION;
import static com.jn.wx.util.WxResponseCode.ORDER_NOT_COMMENT;
import static com.jn.wx.util.WxResponseCode.ORDER_PAY_FAIL;
import static com.jn.wx.util.WxResponseCode.ORDER_UNKNOWN;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.jn.core.validator.Order;
import com.jn.db.dao.JnUserAccountMapper;
import com.jn.db.domain.*;
import com.jn.db.service.*;
import com.jn.wx.util.mail;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.PageInfo;
import com.jn.core.consts.CommConsts;
import com.jn.core.express.ExpressService;
import com.jn.core.express.dao.ExpressInfo;
import com.jn.core.express.dao.Traces;
import com.jn.core.notify.NotifyService;
import com.jn.core.notify.NotifyType;
import com.jn.core.system.SystemConfig;
import com.jn.core.util.DateTimeUtil;
import com.jn.core.util.JacksonUtil;
import com.jn.core.util.ResponseUtil;
import com.jn.db.util.OrderHandleOption;
import com.jn.db.util.OrderUtil;
import com.jn.wx.util.IpUtil;
import com.jn.wx.util.WxResponseCode;
import com.jn.wx.util.WxResponseUtil;

/**
 * 订单服务
 *
 * <p>
 * 订单状态： 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消 201
 * 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消； 301 商家发货，用户未确认； 401 用户确认收货； 402
 * 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作： 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价书籍，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价书籍，或者再次购买
 *
 * <p>
 * 注意：目前不支持订单退货和售后服务
 */
@Service
public class WxOrderService {
    private static final Logger logger = LoggerFactory.getLogger(WxOrderService.class);

    @Autowired
    private JnUserService userService;
    @Autowired
    private JnOrderService orderService;
    @Autowired
    private JnOrderBooksService orderBooksService;
    @Autowired
    private JnAddressService addressService;
    @Autowired
    private JnCartService cartService;
    @Autowired
    private JnRegionService regionService;
    @Autowired
    private JnBooksProductService productService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private JnUserFormIdService formIdService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private JnCommentService commentService;
    @Autowired
    private JnAccountService accountService;
    @Autowired
    private JnBooksService booksService;
    @Autowired
    private JnCompusService compusService;
    @Autowired
    private JnBooksProductService booksProductService;
    @Resource
    private JnUserAccountMapper userAccountMapper;

    private String detailedAddress(JnAddress JnAddress) {
        Integer provinceId = JnAddress.getProvinceId();
        Integer cityId = JnAddress.getCityId();
        Integer areaId = JnAddress.getAreaId();
        String provinceName = regionService.findById(provinceId).getName();
        String cityName = regionService.findById(cityId).getName();
        String areaName = regionService.findById(areaId).getName();
        String fullRegion = provinceName + " " + cityName + " " + areaName;
        return fullRegion + " " + JnAddress.getAddress();
    }

    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 订单信息： 0，全部订单； 1，待付款； 2，待发货； 3，待收货； 4，待评价。
     * @param page     分页页数
     * @param size     分页大小
     * @return 订单列表
     */
    public Object list(Integer userId, Integer showType, Integer page, Integer size) {
        if (userId == null) {
            logger.error("订单列表获取失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        List<Short> orderStatus = OrderUtil.orderStatus(showType);
        List<JnOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus, page, size);
        long count = PageInfo.of(orderList).getTotal();
        int totalPages = (int) Math.ceil((double) count / size);

        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (JnOrder order : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", order.getId());
            orderVo.put("orderSn", order.getOrderSn());
            orderVo.put("addTime", order.getAddTime());
            orderVo.put("consignee", order.getConsignee());
            orderVo.put("mobile", order.getMobile());
            orderVo.put("address", order.getAddress());
            orderVo.put("booksPrice", order.getBooksPrice());
            orderVo.put("freightPrice", order.getFreightPrice());
            orderVo.put("actualPrice", order.getActualPrice());
            orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
            orderVo.put("handleOption", OrderUtil.build(order));
            orderVo.put("expCode", order.getShipChannel());
            orderVo.put("expNo", order.getShipSn());


            List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());
            /*
             * List<Map<String, Object>> orderBooksVoList = new
             * ArrayList<>(orderBooksList.size()); for (JnOrderBooks orderBooks :
             * orderBooksList) { Map<String, Object> orderBooksVo = new HashMap<>();
             * orderBooksVo.put("id", orderBooks.getId()); orderBooksVo.put("booksName",
             * orderBooks.getBooksName()); orderBooksVo.put("number",
             * orderBooks.getNumber()); orderBooksVo.put("picUrl", orderBooks.getPicUrl());
             * orderBooksVo.put("price", orderBooks.getPrice()); orderBooksVo.put("picUrl",
             * orderBooks.getPicUrl()); orderBooksVoList.add(orderBooksVo); }
             */
            orderVo.put("booksList", orderBooksList);

            orderVoList.add(orderVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("data", orderVoList);
        result.put("totalPages", totalPages);

        logger.info("请求结束=>获取订单列表,响应结果:{}", JSONObject.toJSONString(result));
        return ResponseUtil.ok(result);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    public Object detail(Integer userId, Integer orderId) {
        if (userId == null) {
            logger.error("获取订单详情失败：用户未登录!");
            return ResponseUtil.unlogin();
        }

        // 订单信息
        JnOrder order = orderService.findById(orderId);
        if (null == order) {
            logger.error("获取订单详情失败：{}", ORDER_UNKNOWN.desc());
            return WxResponseUtil.fail(ORDER_UNKNOWN);
        }
        if (!order.getUserId().equals(userId)) {
            logger.error("获取订单详情失败：{}", ORDER_INVALID.desc());
            return WxResponseUtil.fail(ORDER_INVALID);
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("booksPrice", order.getBooksPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("orderPrice", order.getOrderPrice());
        orderVo.put("actualPrice", order.getOrderPrice());
        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());

        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderBooks", orderBooksList);

        // 订单状态为已发货且物流信息不为空
        // "YTO", "800669400640887922"
        /*
         * if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) { ExpressInfo ei =
         * expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
         * result.put("expressInfo", ei); }
         */

        logger.info("请求结束=>获取订单详情,响应结果:{}", JSONObject.toJSONString(result));
        return ResponseUtil.ok(result);

    }

    /**
     * 提交订单
     */
    @Transactional
    public Object submit(Integer userId, String body) {
        if (userId == null) {
            logger.error("提交订单详情失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        Integer cartId = JacksonUtil.parseInteger(body, "cartId");
        Integer addressId = JacksonUtil.parseInteger(body, "addressId");
        String message = JacksonUtil.parseString(body, "message");


        if (cartId == null || addressId == null) {
            return ResponseUtil.badArgument();
        }

        // 收货地址
        JnAddress checkedAddress = addressService.findById(addressId);
        if (checkedAddress == null) {
            return ResponseUtil.badArgument();
        }


        // 货品价格
        List<JnCart> checkedBooksList = null;
        if (cartId.equals(0)) {
            checkedBooksList = cartService.queryByUidAndChecked(userId);
        } else {
            JnCart cart = cartService.findById(cartId);
            checkedBooksList = new ArrayList<>(1);
            checkedBooksList.add(cart);
        }
        if (checkedBooksList.size() == 0) {
            return ResponseUtil.badArgumentValue();
        }

        BigDecimal booksTotalPrice = new BigDecimal(0.00);// 书籍总价 （包含团购减免，即减免团购后的书籍总价，多店铺需将所有书籍相加）
        BigDecimal totalFreightPrice = new BigDecimal(0.00);// 总配送费 （单店铺模式一个，多店铺模式多个配送费的总和）

        Boolean isAllInOneCompus = true;
        JnUser user = userService.findById(userId);
        JnCompus tmpcompus = null;
        for (JnCart checkBooks : checkedBooksList) {
            JnBooks book = booksService.queryById(checkBooks.getBooksId());
            JnCompus compus = compusService.findById(book.getCompuId());
            tmpcompus = compus;
            checkBooks.setCompusname(compus.getCompusname());
            if (!user.getCompusname().equals(checkBooks.getCompusname())) {
                isAllInOneCompus = false;
            }
            booksTotalPrice = booksTotalPrice
                    .add(checkBooks.getPrice().multiply(new BigDecimal(checkBooks.getNumber())));
        }

        if (!checkedAddress.getAreaId().equals(tmpcompus.getAreaId())) {
            isAllInOneCompus = false;
        }
        if (!checkedAddress.getAddress().contains(user.getCompusname())) {
            isAllInOneCompus = false;
        }
        // 根据订单书籍总价计算运费，满50则免运费，同校免运费
        if (isAllInOneCompus || (booksTotalPrice.compareTo(SystemConfig.getFreightLimit()) > 0)) {
            totalFreightPrice = BigDecimal.valueOf(0);
        } else {
            totalFreightPrice = SystemConfig.getFreight();
        }

        // 可以使用的其他钱，例如用户积分
        BigDecimal integralPrice = new BigDecimal(0.00);

        // 订单费用
        BigDecimal orderTotalPrice = booksTotalPrice.add(totalFreightPrice);
        // 最终支付费用
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        Integer orderId = null;
        JnOrder order = null;
        // 订单
        order = new JnOrder();
        order.setUserId(userId);
        order.setOrderSn(orderService.generateOrderSn(userId));
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setConsignee(checkedAddress.getName());
        order.setMobile(checkedAddress.getMobile());
        order.setMessage(message);
        String detailedAddress = detailedAddress(checkedAddress);
        order.setAddress(detailedAddress);
        order.setBooksPrice(booksTotalPrice);
        order.setFreightPrice(totalFreightPrice);

        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);

        Integer shareUserId = 1;//
        if (user != null && user.getShareUserId() != null) {
            shareUserId = user.getShareUserId();
        }
        // 添加订单表项
        orderService.add(order);
        orderId = order.getId();

        // 添加订单书籍表项
        for (JnCart cartBooks : checkedBooksList) {
            // 订单书籍
            JnOrderBooks orderBooks = new JnOrderBooks();
            orderBooks.setOrderId(order.getId());
            orderBooks.setBooksId(cartBooks.getBooksId());
            orderBooks.setBooksSn(cartBooks.getBooksSn());
            orderBooks.setProductId(cartBooks.getProductId());
            orderBooks.setBooksName(cartBooks.getBooksName());
            orderBooks.setPicUrl(cartBooks.getPicUrl());
            orderBooks.setPrice(cartBooks.getPrice());
            orderBooks.setNumber(cartBooks.getNumber());
            orderBooks.setSpecifications(cartBooks.getSpecifications());
            orderBooks.setAddTime(LocalDateTime.now());


            orderBooksService.add(orderBooks);
        }

        // 删除购物车里面的书籍信息
        cartService.clearBooks(userId);

        // 书籍货品数量减少
        for (JnCart checkBooks : checkedBooksList) {
            Integer productId = checkBooks.getProductId();
            JnBooksProduct product = productService.findById(productId);

            Integer remainNumber = product.getNumber() - checkBooks.getNumber();
            if (remainNumber < 0) {
                throw new RuntimeException("下单的书籍货品数量大于库存量");
            }
            if (productService.reduceStock(productId, checkBooks.getBooksId(), checkBooks.getNumber()) == 0) {
                throw new RuntimeException("书籍货品库存减少失败");
            }
        }


        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);

        logger.info("请求结束=>提交订单,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    /**
     * 取消订单
     * <p>
     * 1. 检测当前订单是否能够取消； 2. 设置订单取消状态； 3. 书籍货品库存恢复； 4. TODO 优惠券； 5. TODO 团购活动。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
    @Transactional
    public Object cancel(Integer userId, String body) {
        if (userId == null) {
            logger.error("取消订单失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isCancel()) {
            logger.error("取消订单失败：{}", ORDER_INVALID_OPERATION.desc());
            return WxResponseUtil.fail(ORDER_INVALID_OPERATION);
        }

        // 设置订单已取消状态
        order.setOrderStatus(OrderUtil.STATUS_CANCEL);
        order.setEndTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 书籍货品数量增加
        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(orderId);
        for (JnOrderBooks orderBooks : orderBooksList) {
            Integer productId = orderBooks.getProductId();
            Short number = orderBooks.getNumber();
            if (productService.addStock(productId, number) == 0) {
                throw new RuntimeException("书籍货品库存增加失败");
            }
        }

        logger.info("请求结束=>用户取消订单,响应结果:{}", "成功");
        return ResponseUtil.ok();
    }

    /**
     * 付款订单的预支付会话标识
     */
    @Transactional
    public Object prepay(Integer userId, String body, HttpServletRequest request) {
        if (userId == null) {
            logger.error("付款订单的预支付会话标识失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            logger.error("付款订单的预支付会话标识失败：{}", WxResponseCode.ORDER_REPAY_OPERATION.desc());
            return WxResponseUtil.fail(WxResponseCode.ORDER_REPAY_OPERATION);
        }

        JnUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
            logger.error("付款订单的预支付会话标识失败：{}", AUTH_OPENID_UNACCESS.desc());
            return WxResponseUtil.fail(AUTH_OPENID_UNACCESS);
        }
//        WxPayMpOrderResult result;
        try {
//            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
//            orderRequest.setOutTradeNo(order.getOrderSn());
//            orderRequest.setOpenid(openid);
//            orderRequest.setBody(CommConsts.DEFAULT_ORDER_FIX + order.getOrderSn());
//            // 元转成分
//            int fee = 0;
//            BigDecimal actualPrice = order.getActualPrice();
//            fee = actualPrice.multiply(new BigDecimal(100)).intValue();
//            orderRequest.setTotalFee(fee);
//            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));

            //result = wxPayService.createOrder(orderRequest);
            //支付不可用（需要营业执照），此处为账户模拟支付结果
//            result.setAppId("wx9fb318f05da2ff1d");
//            result.setNonceStr("testofme");
//            result.setPackageValue("com.jn.wx");
//            result.setPaySign("testofme");
//            result.setSignType("MD5");
//            result.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//            String prepayId = result.getPackageValue();
            String prepayId = "dsjkl";
            logger.info("test3!");
            prepayId = prepayId.replace("prepay_id=", "");
            JnUserFormid userFormid = new JnUserFormid();
            logger.info("test4!");
            userFormid.setOpenid(user.getWeixinOpenid());
            userFormid.setFormid(prepayId);
            userFormid.setIsprepay(true);
            userFormid.setUseamount(3);
            userFormid.setExpireTime(LocalDateTime.now().plusDays(7));
            formIdService.addUserFormid(userFormid);

        } catch (Exception e) {
            logger.error("付款订单的预支付会话标识失败：{}", ORDER_PAY_FAIL.desc());
            e.printStackTrace();
            return WxResponseUtil.fail(ORDER_PAY_FAIL);
        }

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.error("付款订单的预支付会话标识失败：{}", "更新订单信息失败");
            return ResponseUtil.updatedDateExpired();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));

        logger.info("请求结束=>购物车书籍删除成功:清理的productIds:{}", JSONObject.toJSONString(order.getId()));
        return ResponseUtil.ok(result);
    }


    /**
     * 订单服务
     *
     * <p>
     * 订单状态： 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消 201
     * 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消； 301 商家发货，用户未确认； 401 用户确认收货； 402
     * 用户没有确认收货超过一定时间，系统自动确认收货；
     *
     * <p>
     * 用户操作： 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
     * 当301商家已发货时，此时用户可以有确认收货的操作 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价书籍，或者再次购买
     * 当402系统自动确认收货以后，此时用户可以删除订单，评价书籍，或者再次购买
     *
     * <p>
     * 注意：目前不支持订单退货和售后服务
     */
    /**
     * 付款订单的预支付会话标识
     */
    @Transactional
    public Object imitatepay(Integer userId, String body, HttpServletRequest request) {
        if (userId == null) {
            logger.error("付款订单的预支付会话标识失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isPay()) {
            logger.error("付款订单的预支付会话标识失败：{}", WxResponseCode.ORDER_REPAY_OPERATION.desc());
            return WxResponseUtil.fail(WxResponseCode.ORDER_REPAY_OPERATION);
        }

        JnUser user = userService.findById(userId);
        String openid = user.getWeixinOpenid();
        if (openid == null) {
            logger.error("付款订单的预支付会话标识失败：{}", AUTH_OPENID_UNACCESS.desc());
            return WxResponseUtil.fail(AUTH_OPENID_UNACCESS);
        }
        try {
            order.setOrderStatus((short) 201);
            JnUserAccount userAccount = accountService.findByUserId(userId);
            if ((userAccount.getRemainAmount().subtract(order.getOrderPrice())).compareTo(BigDecimal.valueOf(0)) > 0) {
                userAccount.setRemainAmount(userAccount.getRemainAmount().subtract(order.getOrderPrice()));
            } else {
                return WxResponseUtil.fail();
            }
            List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());
            for (JnOrderBooks book : orderBooksList) {
                List<JnBooksProduct> jnBooksProductList = booksProductService.findByBooksId(book.getBooksId());
                for (JnBooksProduct jnBooksProduct : jnBooksProductList) {
                    jnBooksProduct.setNumber((jnBooksProduct.getNumber() - 1));
                    booksProductService.updateByOne(jnBooksProduct);
                }
            }
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                logger.error("付款订单的预支付会话标识失败：{}", "更新订单信息失败");
                return ResponseUtil.updatedDateExpired();
            }
            accountService.updateByOne(userAccount);
        } catch (Exception e) {
            logger.error("付款订单的预支付会话标识失败：{}", ORDER_PAY_FAIL.desc());
            e.printStackTrace();
            return WxResponseUtil.fail(ORDER_PAY_FAIL);
        }

        logger.info("请求结束=>购物车书籍删除成功:清理的productIds:{}", JSONObject.toJSONString(order.getId()));
        return ResponseUtil.ok();
    }

    /**
     * 微信付款成功或失败回调接口
     * <p>
     * 1. 检测当前订单是否是付款状态; 2. 设置订单付款成功状态相关信息; 3. 响应微信商户平台.
     *
     * @param request  请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @Transactional
    public Object jnPayNotify(HttpServletRequest request, HttpServletResponse response) {
        String xmlResult = null;
        try {
            xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        } catch (IOException e) {
            logger.error("微信付款成功或失败回调失败：{}", "获取回调消息内容错误!");
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        WxPayOrderNotifyResult result = null;
        try {
            result = wxPayService.parseOrderNotifyResult(xmlResult);
        } catch (WxPayException e) {
            logger.error("微信付款成功或失败回调失败：{}", "格式化消息内容错误!");
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        logger.info("处理腾讯支付平台的订单支付：{}", JSONObject.toJSONString(result));

        String orderSn = result.getOutTradeNo();
        String payId = result.getTransactionId();

        // 分转化成元
        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
        JnOrder order = orderService.findBySn(orderSn);
        if (order == null) {
            logger.error("微信付款成功或失败回调失败：{}", "订单不存在 sn=" + orderSn);
            return WxPayNotifyResponse.fail("订单不存在 sn=" + orderSn);
        }

        // 检查这个订单是否已经处理过
        if (OrderUtil.isPayStatus(order) && order.getPayId() != null) {
            logger.warn("警告：微信付款成功或失败回调：{}", "订单已经处理成功!");
            return WxPayNotifyResponse.success("订单已经处理成功!");
        }

        // 检查支付订单金额
        if (!totalFee.equals(order.getActualPrice().toString())) {
            logger.error("微信付款成功或失败回调失败：{}", order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
            return WxPayNotifyResponse.fail(order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
        }


        /**
         * modify by CHENBO 2019-08-09 对于多店铺模式，支付成功后，如果这个订单包含多个商铺的书籍，则考虑拆分订单
         * 1.原订单删除，需要按店铺拆成多个单，每个订单的订单序列码用 orderSn_1,orderSn_2,orderSn_3... 2.调整原订单书籍表
         * jn_order_books 中的订单编号 3.调整团购的订单编号，注意，不管是否为多商铺模式，每个团购书籍只会归属于一个订单或一个子订单
         * 4.调整用户优惠券对应的订单，用户优惠券对应的订单可能会有多个，因为多店铺模式，每个大订单可能只有一个优惠券，会按实际收款金额比例进行平摊
         */
        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());

        // 邮件通知的订单id
        String orderIds = "";


        orderIds = orderIds + "," + order.getId().intValue();
        order.setPayId(payId);
        order.setPayTime(LocalDateTime.now());
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            // 这里可能存在这样一个问题，用户支付和系统自动取消订单发生在同时
            // 如果数据库首先因为系统自动取消订单而更新了订单状态；
            // 此时用户支付完成回调这里也要更新数据库，而由于乐观锁机制这里的更新会失败
            // 因此，这里会重新读取数据库检查状态是否是订单自动取消，如果是则更新成支付状态。
            order = orderService.findBySn(orderSn);
            int updated = 0;
            if (OrderUtil.isAutoCancelStatus(order)) {
                order.setPayId(payId);
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus(OrderUtil.STATUS_PAY);
                updated = orderService.updateWithOptimisticLocker(order);
            }

            // 如果updated是0，那么数据库更新失败
            if (updated == 0) {
                return WxPayNotifyResponse.fail("更新数据已失效");
            }
        }

        // TODO 发送邮件和短信通知，这里采用异步发送
        // 订单支付成功以后，会发送短信给用户，以及发送邮件给管理员
        // notifyService.notifyMail("新订单通知", order.toString());
        notifyService.notifySslMail("新订单通知", OrderUtil.orderHtmlText(order, orderIds, orderBooksList));
        // 这里微信的短信平台对参数长度有限制，所以将订单号只截取后6位，暂时屏蔽，因为已有微信模板提醒
        // notifyService.notifySmsTemplateSync(order.getMobile(),
        // NotifyType.PAY_SUCCEED, new String[]{orderSn.substring(8, 14)});

        // 请依据自己的模版消息配置更改参数
        String[] parms = new String[]{order.getOrderSn(), order.getOrderPrice().toString(),
                DateTimeUtil.getDateTimeDisplayString(order.getAddTime()), order.getConsignee(), order.getMobile(),
                order.getAddress()};

        // notifyService.notifyWxTemplate(result.getOpenid(), NotifyType.PAY_SUCCEED,
        // parms, "pages/index/index?orderId=" + order.getId());
        notifyService.notifyWxTemplate(result.getOpenid(), NotifyType.PAY_SUCCEED, parms, "/pages/ucenter/order/order");

        logger.info("请求结束=>微信付款成功或失败回调:响应结果:{}", "处理成功!");
        return WxPayNotifyResponse.success("处理成功!");
    }

    /**
     * 将不变的订单属性复制到子订单
     *
     * @param order
     * @param childOrder
     * @return
     */
    private JnOrder copyfixedOrderAttr(JnOrder order, JnOrder childOrder) {
        if (childOrder != null && order != null) {
            childOrder.setAddress(order.getAddress());
            childOrder.setAddTime(order.getAddTime());
            childOrder.setConsignee(order.getConsignee());
            childOrder.setMessage(order.getMessage());
            childOrder.setMobile(order.getMobile());
            childOrder.setUserId(order.getUserId());
        }
        return childOrder;
    }

    /**
     * 订单申请退款
     * <p>
     * 1. 检测当前订单是否能够退款； 2. 设置订单申请退款状态。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    public Object refund(Integer userId, String body) throws Exception {
        if (userId == null) {
            logger.error("订单申请退款失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isRefund()) {
            logger.error("订单申请退款失败：{}", ORDER_INVALID_OPERATION.desc());
            return WxResponseUtil.fail(ORDER_INVALID_OPERATION);
        }

        // 设置订单申请退款状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND);
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.error("订单申请退款失败：{}", "更新订单信息失败");
            return ResponseUtil.updatedDateExpired();
        }
        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());
        HashSet<String> email = new HashSet<>();
        for (JnOrderBooks book : orderBooksList) {
            JnBooks jnBooks = booksService.findById(book.getBooksId());
            JnUser jnUser = userService.findById(jnBooks.getUserId());
            email.add(jnUser.getEmail());
        }
        for (String e : email) {
            mail.sendEmail(e, "000000", 1);
        }
        logger.info("请求结束=>订单申请退款成功！");
        return ResponseUtil.ok();
    }

    /**
     * 确认收货
     * <p>
     * 1. 检测当前订单是否能够确认收货； 2. 设置订单确认收货状态。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object confirm(Integer userId, String body) {
        if (userId == null) {
            logger.error("订单确认收货失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            logger.error("订单确认收货失败：{}", ORDER_CONFIRM_OPERATION.desc());
            return WxResponseUtil.fail(ORDER_CONFIRM_OPERATION);
        }

        Short comments = orderBooksService.getComments(orderId);
        order.setComments(comments);

        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setConfirmTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.error("订单确认收货失败：{}", "更新订单信息失败");
            return ResponseUtil.updatedDateExpired();
        }

        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());
        for (int i = 0; i < orderBooksList.size(); i++) {
            JnBooks books = booksService.findById(orderBooksList.get(i).getBooksId());
            JnUser jnUser = userService.findById(books.getUserId());
            JnUserAccountExample example = new JnUserAccountExample();
            example.or().andUserIdEqualTo(jnUser.getId());
            JnUserAccount userAccount = userAccountMapper.selectOneByExample(example);
            userAccount.setRemainAmount(userAccount.getRemainAmount().add(orderBooksList.get(i).getPrice()));
            userAccount.setTotalAmount(userAccount.getTotalAmount().add(orderBooksList.get(i).getPrice()));
            userAccountMapper.updateByPrimaryKey(userAccount);
            String txt = "您的书籍：" + books.getName() + "已成功卖出！" + "此次收益为：" + orderBooksList.get(i).getPrice();
            try {
                com.jn.core.util.mail.sendEmail(jnUser.getEmail(), txt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JnUser jnUser = userService.findById(order.getUserId());
        String txt = "您的订单：" + order.getOrderSn() + "已成功收货！";
        try {
            com.jn.core.util.mail.sendEmail(jnUser.getEmail(), txt);
        } catch (Exception e) {
            e.printStackTrace();
        }


        logger.info("请求结束=>订单确认收货成功！");
        return ResponseUtil.ok();
    }

    /**
     * 删除订单
     * <p>
     * 1. 检测当前订单是否可以删除； 2. 删除订单。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object delete(Integer userId, String body) {
        if (userId == null) {
            logger.error("删除订单失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isDelete()) {
            logger.error("删除订单失败：{}", ORDER_DEL_OPERATION.desc());
            return WxResponseUtil.fail(ORDER_DEL_OPERATION);
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的delete字段表示是否删除
        orderService.deleteById(orderId);

        logger.info("请求结束=>删除订单成功！");
        return ResponseUtil.ok();
    }

    /**
     * 待评价订单书籍信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param booksId 书籍ID
     * @return 待评价订单书籍信息
     */
    public Object books(Integer userId, Integer orderId, Integer booksId) {
        if (userId == null) {
            logger.error("获取待评价订单书籍订单失败：用户未登录!");
            return ResponseUtil.unlogin();
        }

        List<JnOrderBooks> orderBooksList = orderBooksService.findByOidAndGid(orderId, booksId);
        int size = orderBooksList.size();

        Assert.state(size < 2, "存在多个符合条件的订单书籍");

        if (size == 0) {
            return ResponseUtil.badArgumentValue();
        }

        JnOrderBooks orderBooks = orderBooksList.get(0);

        logger.info("请求结束=>获取待评价订单书籍订单成功！");
        return ResponseUtil.ok(orderBooks);
    }

    /**
     * 评价订单书籍
     * <p>
     * 确认书籍收货或者系统自动确认书籍收货后7天内可以评价，过期不能评价。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object comment(Integer userId, String body) {
        if (userId == null) {
            logger.error("评价订单书籍失败：用户未登录!");
            return ResponseUtil.unlogin();
        }

        Integer orderBooksId = JacksonUtil.parseInteger(body, "orderBooksId");
        if (orderBooksId == null) {
            return ResponseUtil.badArgument();
        }
        JnOrderBooks orderBooks = orderBooksService.findById(orderBooksId);
        if (orderBooks == null) {
            return ResponseUtil.badArgumentValue();
        }
        Integer orderId = orderBooks.getOrderId();
        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }

        if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
            logger.error("评价订单书籍失败：{}", ORDER_NOT_COMMENT.desc());
            return WxResponseUtil.fail(ORDER_NOT_COMMENT);
        }
        if (!order.getUserId().equals(userId)) {
            logger.error("评价订单书籍失败：{}", ORDER_INVALID.desc());
            return WxResponseUtil.fail(ORDER_INVALID);
        }
        Integer commentId = orderBooks.getComment();
        if (commentId == -1) {
            logger.error("评价订单书籍失败：{}", ORDER_COMMENT_EXPIRED.desc());
            return WxResponseUtil.fail(ORDER_COMMENT_EXPIRED);
        }
        if (commentId != 0) {
            logger.error("评价订单书籍失败：{}", ORDER_COMMENTED.desc());
            return WxResponseUtil.fail(ORDER_COMMENTED);
        }

        String content = JacksonUtil.parseString(body, "content");
        Integer star = JacksonUtil.parseInteger(body, "star");
        if (star == null || star < 0 || star > 5) {
            return ResponseUtil.badArgumentValue();
        }
        Boolean hasPicture = JacksonUtil.parseBoolean(body, "hasPicture");
        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
        if (hasPicture == null || !hasPicture) {
            picUrls = new ArrayList<>(0);
        }

        // 1. 创建评价
        JnComment comment = new JnComment();
        comment.setUserId(userId);
        comment.setType((byte) 0);
        comment.setValueId(orderBooks.getBooksId());
        comment.setStar(star.shortValue());
        comment.setContent(content);
        comment.setHasPicture(hasPicture);
        comment.setPicUrls(picUrls.toArray(new String[]{}));
        commentService.save(comment);

        // 2. 更新订单书籍的评价列表
        orderBooks.setComment(comment.getId());
        orderBooksService.updateById(orderBooks);

        // 3. 更新订单中未评价的订单书籍可评价数量
        Short commentCount = order.getComments();
        if (commentCount > 0) {
            commentCount--;
        }
        order.setComments(commentCount);
        orderService.updateWithOptimisticLocker(order);

        logger.info("请求结束=>评价订单书籍成功！");
        return ResponseUtil.ok();
    }

    /**
     * 推广订单列表
     *
     * @param userId   用户代理用户ID
     * @param showType 订单信息： 0，全部订单； 1，有效订单； 2，失效订单； 3，结算订单； 4，待结算订单。
     * @param page     分页页数
     * @param size     分页大小
     * @return 推广订单列表
     */
    public Object settleOrderList(Integer userId, Integer showType, Integer page, Integer size) {
        if (userId == null) {
            logger.error("获取推广订单列表失败：用户未登录!");
            return ResponseUtil.unlogin();
        }
        List<Short> orderStatus = OrderUtil.settleOrderStatus(showType);
        List<Short> settlementStatus = OrderUtil.settlementStatus(showType);

        List<JnOrder> orderList = accountService.querySettlementOrder(userId, orderStatus, settlementStatus, page,
                size);
        long count = PageInfo.of(orderList).getTotal();
        int totalPages = (int) Math.ceil((double) count / size);

        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (JnOrder order : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", order.getId());
            orderVo.put("orderSn", order.getOrderSn());
            orderVo.put("actualPrice", order.getActualPrice());
            orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
            orderVo.put("handleOption", OrderUtil.build(order));

            List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(order.getId());
            List<Map<String, Object>> orderBooksVoList = new ArrayList<>(orderBooksList.size());
            for (JnOrderBooks orderBooks : orderBooksList) {
                Map<String, Object> orderBooksVo = new HashMap<>();
                orderBooksVo.put("id", orderBooks.getId());
                orderBooksVo.put("booksName", orderBooks.getBooksName());
                orderBooksVo.put("number", orderBooks.getNumber());
                orderBooksVo.put("picUrl", orderBooks.getPicUrl());
                orderBooksVoList.add(orderBooksVo);
            }
            orderVo.put("booksList", orderBooksVoList);

            orderVoList.add(orderVo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("data", orderVoList);
        result.put("totalPages", totalPages);

        logger.info("请求结束=>获取推广订单列表成功！,推广订单数：{}", orderVoList.size());
        return ResponseUtil.ok(result);
    }

    /**
     * 分页查询取款结算记录
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public Object extractList(Integer userId, Integer page, Integer size) {
        if (userId == null) {
            logger.error("分页查询取款结算记录失败：用户未登录!");
            return ResponseUtil.unlogin();
        }

        List<JnAccountTrace> accountTraceList = accountService.queryAccountTraceList(userId, page, size);
        long count = PageInfo.of(accountTraceList).getTotal();
        int totalPages = (int) Math.ceil((double) count / size);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("accountTraceList", accountTraceList);
        result.put("totalPages", totalPages);

        logger.info("请求结束=>获取推广订单列表成功！,推广订单数：{}", count);
        return ResponseUtil.ok(result);
    }

    /**
     * 订单物流跟踪
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Object expressTrace(Integer userId, @NotNull Integer orderId) {
        if (userId == null) {
            logger.error("订单物流跟踪失败：用户未登录!");
            return ResponseUtil.unlogin();
        }

        // 订单信息
        JnOrder order = orderService.findById(orderId);
        if (null == order) {
            logger.error("订单物流跟踪失败：{}", ORDER_UNKNOWN.desc());
            return WxResponseUtil.fail(ORDER_UNKNOWN);
        }
        if (!order.getUserId().equals(userId)) {
            logger.error("订单物流跟踪失败：{}", ORDER_INVALID.desc());
            return WxResponseUtil.fail(ORDER_INVALID);
        }

        Map<String, Object> result = new HashMap<>();
        DateTimeFormatter dateSdf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeSdf = DateTimeFormatter.ofPattern("HH:mm");

        result.put("shipDate", dateSdf.format(order.getShipTime()));
        result.put("shipTime", timeSdf.format(order.getShipTime()));
        result.put("shipperCode", order.getShipSn());
        result.put("address", order.getAddress());

        // "YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            if (ei != null) {
                result.put("state", ei.getState());
                result.put("shipperName", ei.getShipperName());
                List<Traces> eiTrace = ei.getTraces();
                List<Map<String, Object>> traces = new ArrayList<Map<String, Object>>();
                for (Traces trace : eiTrace) {
                    Map<String, Object> traceMap = new HashMap<String, Object>();
                    traceMap.put("date", trace.getAcceptTime().substring(0, 10));
                    traceMap.put("time", trace.getAcceptTime().substring(11, 16));
                    traceMap.put("acceptTime", trace.getAcceptTime());
                    traceMap.put("acceptStation", trace.getAcceptStation());
                    traces.add(traceMap);
                }
                result.put("traces", traces);
            }
        }

        logger.info("请求结束=>订单物流跟踪成功！");
        return ResponseUtil.ok(result);

    }
}
