package com.jn.admin.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jn.db.dao.JnUserAccountMapper;
import com.jn.db.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.pagehelper.PageInfo;
import com.jn.admin.util.AdminResponseUtil;
import com.jn.core.express.ExpressService;
import com.jn.core.notify.NotifyService;
import com.jn.core.notify.NotifyType;
import com.jn.core.util.JacksonUtil;
import com.jn.core.util.ResponseUtil;
import com.jn.db.service.JnCommentService;
import com.jn.db.service.JnBooksProductService;
import com.jn.db.service.JnOrderBooksService;
import com.jn.db.service.JnOrderService;
import com.jn.db.service.JnUserService;
import com.jn.db.util.OrderUtil;
import com.jn.core.util.mail;

import javax.annotation.Resource;

import static com.jn.admin.util.AdminResponseCode.*;


/*
 * 订单管理服务
 * */
@Service
public class AdminOrderService {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderService.class);

    @Autowired
    private JnOrderBooksService orderBooksService;
    @Autowired
    private JnOrderService orderService;
    @Autowired
    private JnBooksProductService productService;
    @Autowired
    private JnUserService userService;
    @Autowired
    private JnCommentService commentService;
    @Autowired
    private ExpressService expressService;

    @Resource
    JnUserAccountMapper userAccountMapper;
    /*
     * @Autowired private WxPayService wxPayService;
     */
    @Autowired
    private NotifyService notifyService;

    public Object list(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page, Integer limit,
                       String sort, String order) {
        List<JnOrder> orderList = orderService.querySelective(userId, orderSn, orderStatusArray, page, limit, sort,
                order);
        long total = PageInfo.of(orderList).getTotal();

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", orderList);

        logger.info("请求结束=>旧书管理->订单管理->查询,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        JnOrder order = orderService.findById(id);
        List<JnOrderBooks> orderBooks = orderBooksService.queryByOid(id);
        UserVo user = userService.findUserVoById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderBooks", orderBooks);
        data.put("user", user);

        logger.info("请求结束=>旧书管理->订单管理->详情,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    /**
     * 订单退款
     * <p>
     * 1. 检测当前订单是否能够退款; 2. 微信退款操作; 3. 设置订单退款确认状态； 4. 订单书籍库存回库。
     * <p>
     * TODO 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤： 1.
     * 管理员登录微信官方支付平台点击退款操作进行退款 2. 管理员登录Jn管理后台点击退款操作进行订单状态修改和书籍库存回库
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    public Object refund(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return ResponseUtil.badArgumentValue();
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            logger.info("旧书管理->订单管理->订单退款失败:{}", ORDER_REFUND_FAILED.desc());
            return AdminResponseUtil.fail(ORDER_REFUND_FAILED);
        }

        JnUser jnUser = userService.findById(order.getUserId());
        JnUserAccountExample example = new JnUserAccountExample();
        example.or().andUserIdEqualTo(jnUser.getId());
        JnUserAccount userAccount = userAccountMapper.selectOneByExample(example);
        userAccount.setRemainAmount(userAccount.getRemainAmount().add(BigDecimal.valueOf(Integer.parseInt(refundMoney))));
        userAccountMapper.updateByPrimaryKey(userAccount);
        // 设置订单取消状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.info("旧书管理->订单管理->订单退款失败:{}", "更新数据已失效");
            throw new RuntimeException("更新数据已失效");
        }

        // 书籍货品数量增加
        List<JnOrderBooks> orderBooksList = orderBooksService.queryByOid(orderId);
        for (JnOrderBooks orderBooks : orderBooksList) {
            Integer productId = orderBooks.getProductId();
            Short number = orderBooks.getNumber();
            if (productService.addStock(productId, number) == 0) {
                logger.info("旧书管理->订单管理->订单退款失败:{}", "书籍货品库存增加失败");
                throw new RuntimeException("书籍货品库存增加失败");
            }
        }

        String txt = "您的订单：" + order.getOrderSn() + "已退款成功！";
        try {
            mail.sendEmail(jnUser.getEmail(), txt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("请求结束=>旧书管理->订单管理->订单退款,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    /**
     * 发货 1. 检测当前订单是否能够发货 2. 设置订单发货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object ship(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null || shipSn == null || shipChannel == null) {
            return ResponseUtil.badArgument();
        }

        JnOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            logger.info("旧书管理->订单管理->订单发货失败:{}", ORDER_CONFIRM_NOT_ALLOWED.desc());
            return AdminResponseUtil.fail(ORDER_CONFIRM_NOT_ALLOWED);
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.info("旧书管理->订单管理->订单发货失败:{}", "更新数据失败!");
            return ResponseUtil.updatedDateExpired();
        }

        JnUser jnUser = userService.findById(order.getUserId());

        String txt = "您的订单：" + order.getOrderSn() + "已发货成功！\n" + "快递公司：" + order.getShipChannel() +
                "  \n快递编号：" + order.getShipSn() + "\n发货时间：" + order.getShipTime();
        try {
            mail.sendEmail(jnUser.getEmail(), txt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("请求结束=>旧书管理->订单管理->订单发货,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    /**
     * 回复订单书籍
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object reply(String body) {
        Integer commentId = JacksonUtil.parseInteger(body, "commentId");
        if (commentId == null || commentId == 0) {
            return ResponseUtil.badArgument();
        }
        JnComment temp = commentService.findById(commentId);
        Integer fatherId = 0;
        logger.info("1");
        if (temp != null) {
            logger.info("2");
            fatherId = temp.getFatherId();
            // 目前只支持回复一次
            if (fatherId != 0) {
                logger.info("旧书管理->订单管理->订单书籍回复:{}", ORDER_REPLY_EXIST.desc());
                return AdminResponseUtil.fail(ORDER_REPLY_EXIST);
            } else {
                logger.info("1");
                String content = JacksonUtil.parseString(body, "content");
                if (StringUtils.isEmpty(content)) {
                    return ResponseUtil.badArgument();
                }
                // 创建评价回复
                JnComment comment = new JnComment();
                comment.setType((byte) 2);
                comment.setValueId(temp.getValueId());
                comment.setFatherId(commentId);
                comment.setContent(content);
                comment.setUserId(0); // 评价回复没有用
                comment.setStar((short) 0); // 评价回复没有用
                comment.setHasPicture(false); // 评价回复没有用
                comment.setPicUrls(new String[]{}); // 评价回复没有用
                commentService.save(comment);

                logger.info("请求结束=>旧书管理->订单管理->订单书籍回复,响应结果:{}", "成功!");
                return ResponseUtil.ok();
            }
        } else {
            return AdminResponseUtil.fail(ORDER_REPLY_FAIL);
        }

    }

    /**
     * 快递公司列表
     *
     * @return
     */
    public Object listShipChannel() {
        List<Map<String, String>> vendorMaps = expressService.getAllVendor();
        List<Map<String, Object>> shipChannelList = new ArrayList<Map<String, Object>>(vendorMaps == null ? 0 : vendorMaps.size());
        for (Map<String, String> map : vendorMaps) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", map.get("code"));
            b.put("label", map.get("name"));
            shipChannelList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("shipChannelList", shipChannelList);
        logger.info("获取已配置的快递公司总数：{}", shipChannelList.size());
        return ResponseUtil.ok(data);
    }

}
