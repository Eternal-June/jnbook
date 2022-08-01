package com.jn.wx.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jn.db.domain.*;
import com.jn.db.service.*;
import com.jn.wx.service.WxOrderService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jn.core.system.SystemConfig;
import com.jn.core.util.JacksonUtil;
import com.jn.core.util.ResponseUtil;
import com.jn.wx.annotation.LoginUser;
import com.jn.wx.util.WxResponseUtil;

import static com.jn.wx.util.WxResponseCode.*;

/**
 * 用户购物车服务
 */
@RestController
@RequestMapping("/wx/cart")
@Validated
public class WxCartController {
    private static final Logger logger = LoggerFactory.getLogger(WxCartController.class);

    @Autowired
    private JnCartService cartService;
    @Autowired
    private JnBooksService booksService;
    @Autowired
    private JnBooksProductService productService;
    @Autowired
    private JnAddressService addressService;
    @Autowired
    private JnCompusService compusService;
    @Autowired
    private JnUserService userService;
    @Autowired
    private WxOrderService wxOrderService;

    /**
     * 用户购物车信息
     *
     * @param userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("index")
    public Object index(@LoginUser Integer userId) {
        logger.info("请求开始=>用户购物车信息列表,请求参数,userId:{}", userId);
        if (userId == null) {
            logger.error("获取用户购物车信息列表失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }

        List<JnCart> cartList = cartService.queryByUid(userId);
        Integer booksCount = 0;
        BigDecimal booksAmount = new BigDecimal(0.00);
        Integer checkedBooksCount = 0;
        BigDecimal checkedBooksAmount = new BigDecimal(0.00);
        for (JnCart cart : cartList) {
            JnBooks book = booksService.queryById(cart.getBooksId());
            JnCompus compus = compusService.findById(book.getCompuId());
            cart.setCompusname(compus.getCompusname());
            booksCount += cart.getNumber();
            booksAmount = booksAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedBooksCount += cart.getNumber();
                checkedBooksAmount = checkedBooksAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("booksCount", booksCount);
        cartTotal.put("booksAmount", booksAmount);
        cartTotal.put("checkedBooksCount", checkedBooksCount);
        cartTotal.put("checkedBooksAmount", checkedBooksAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartTotal", cartTotal);

        JnUser jnUser = userService.findById(userId);
        result.put("isMultiOrderModel", 0);
        result.put("cartList", cartList);
        result.put("mycompus", jnUser.getCompusname());
        logger.info("请求结束=>用户购物车信息列表,响应结果：{}", JSONObject.toJSONString(result));
        return ResponseUtil.ok(result);
    }

    /**
     * 加入书籍到购物车
     * <p>
     * 如果已经存在购物车货品，则增加数量； 否则添加新的购物车货品项。
     *
     * @param userId 用户ID
     * @param cart   购物车书籍信息， { booksId: xxx, productId: xxx, number: xxx }
     * @return 加入购物车操作结果
     */
    @PostMapping("add")
    public Object add(@LoginUser Integer userId, @RequestBody JnCart cart) {
        logger.info("请求开始=>加入书籍到购物车,请求参数,userId:{},cart:{}", userId, JSONObject.toJSONString(cart));

        if (userId == null) {
            logger.error("加入书籍到购物车失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer booksId = cart.getBooksId();
        JnBooks jnBooks=booksService.findById(booksId);
        if(jnBooks.getUserId().equals(userId)){
            return WxResponseUtil.fail(IS_MY_BOOK);
        }
        if (!ObjectUtils.allNotNull(productId, number, booksId)) {
            return ResponseUtil.badArgument();
        }

        // 判断书籍是否可以购买
        JnBooks books = booksService.findById(booksId);
        if (books == null || !books.getIsOnSale()) {
            logger.error("加入书籍到购物车失败:{}", BOOKS_UNSHELVE.desc());
            return WxResponseUtil.fail(BOOKS_UNSHELVE);
        }

        JnBooksProduct product = productService.findById(productId);
        // 判断购物车中是否存在此规格书籍
        JnCart existCart = cartService.queryExist(booksId, productId, userId);
        if (existCart == null) {
            // 取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                logger.error("加入书籍到购物车失败:{}", BOOKS_NO_STOCK.desc());
                return WxResponseUtil.fail(BOOKS_NO_STOCK);
            }

            cart.setId(null);
            cart.setBooksSn(books.getBooksSn());

            cart.setBooksName((books.getName()));
            cart.setPicUrl(books.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            // 取得规格的信息,判断规格库存
            int num = existCart.getNumber() + number;
            if (num > product.getNumber()) {
                logger.error("加入书籍到购物车失败:{}", BOOKS_NO_STOCK.desc());
                return WxResponseUtil.fail(BOOKS_NO_STOCK);
            }
            existCart.setNumber((short) num);
            if (cartService.updateById(existCart) == 0) {
                logger.error("加入书籍到购物车失败:更新购物车信息失败!");
                return ResponseUtil.updatedDataFailed();
            }
        }
        logger.info("请求结束=>加入书籍到购物车成功!");
        return bookscount(userId);
    }

    /**
     * 立即购买
     * <p>
     * 和add方法的区别在于： 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖 2.
     * 添加成功以后，前者的逻辑是返回当前购物车书籍数量，这里的逻辑是返回对应购物车项的ID
     *
     * @param userId 用户ID
     * @param cart   购物车书籍信息， { booksId: xxx, productId: xxx, number: xxx }
     * @return 立即购买操作结果
     */
    @PostMapping("fastadd")
    public Object fastadd(@LoginUser Integer userId, @RequestBody JnCart cart) {
        logger.info("请求开始=>立即购买,请求参数,userId:{},cart:{}", userId, JSONObject.toJSONString(cart));

        if (userId == null) {
            logger.error("立即购买:用户未登录！！！");
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer booksId = cart.getBooksId();
        JnBooks jnBooks=booksService.findById(booksId);
        if(jnBooks.getUserId().equals(userId)){
            return WxResponseUtil.fail(IS_MY_BOOK);
        }
        if (!ObjectUtils.allNotNull(productId, number, booksId)) {
            return ResponseUtil.badArgument();
        }

        // 判断书籍是否可以购买
        JnBooks books = booksService.findById(booksId);
        if (books == null || !books.getIsOnSale()) {
            logger.error("立即购买失败:{}", BOOKS_UNSHELVE.desc());
            return WxResponseUtil.fail(BOOKS_UNSHELVE);
        }

        JnBooksProduct product = productService.findById(productId);
        // 判断购物车中是否存在此规格书籍
        JnCart existCart = cartService.queryExist(booksId, productId, userId);
        if (existCart == null) {
            // 取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                logger.error("立即购买失败:{}", BOOKS_NO_STOCK.desc());
                return WxResponseUtil.fail(BOOKS_NO_STOCK);
            }

            cart.setId(null);
            cart.setBooksSn(books.getBooksSn());
            cart.setBooksName((books.getName()));
            cart.setPicUrl(books.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cartService.add(cart);
        } else {
            // 取得规格的信息,判断规格库存
            int num = number;
            if (num > product.getNumber()) {
                logger.error("立即购买失败:{}", BOOKS_NO_STOCK.desc());
                return WxResponseUtil.fail(BOOKS_NO_STOCK);
            }
            existCart.setNumber((short) num);
            if (cartService.updateById(existCart) == 0) {
                logger.error("立即购买失败:更新购物车信息失败!");
                return ResponseUtil.updatedDataFailed();
            }
        }

        logger.info("请求结束=>立即购买成功!");
        return ResponseUtil.ok(existCart != null ? existCart.getId() : cart.getId());
    }

    /**
     * 修改购物车书籍货品数量
     *
     * @param userId 用户ID
     * @param cart   购物车书籍信息， { id: xxx, booksId: xxx, productId: xxx, number: xxx }
     * @return 修改结果
     */
    @PostMapping("update")
    public Object update(@LoginUser Integer userId, @RequestBody JnCart cart) {
        logger.info("请求开始=>修改购物车,请求参数,userId:{},cart:{}", userId, JSONObject.toJSONString(cart));

        if (userId == null) {
            logger.error("修改购物车:用户未登录！！！");
            return ResponseUtil.unlogin();
        }
        if (cart == null) {
            return ResponseUtil.badArgument();
        }
        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer booksId = cart.getBooksId();
        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id, productId, number, booksId)) {
            return ResponseUtil.badArgument();
        }

        // 判断是否存在该订单
        // 如果不存在，直接返回错误
        JnCart existCart = cartService.findById(id);
        if (existCart == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 判断booksId和productId是否与当前cart里的值一致
        if (!existCart.getBooksId().equals(booksId)) {
            return ResponseUtil.badArgumentValue();
        }
        if (!existCart.getProductId().equals(productId)) {
            return ResponseUtil.badArgumentValue();
        }

        // 判断书籍是否可以购买
        JnBooks books = booksService.findById(booksId);
        if (books == null || !books.getIsOnSale()) {
            logger.error("修改购物车失败:{}", BOOKS_UNSHELVE.desc());
            return WxResponseUtil.fail(BOOKS_UNSHELVE);
        }

        // 取得规格的信息,判断规格库存
        JnBooksProduct product = productService.findById(productId);
        if (product == null || product.getNumber() < number) {
            logger.error("修改购物车失败:{}", BOOKS_NO_STOCK.desc());
            return WxResponseUtil.fail(BOOKS_NO_STOCK);
        }

        existCart.setNumber(number.shortValue());
        if (cartService.updateById(existCart) == 0) {
            logger.error("修改购物车失败:更新购物车信息失败!");
            return ResponseUtil.updatedDataFailed();
        }
        logger.info("请求结束=>修改购物车成功!");
        return ResponseUtil.ok();
    }

    /**
     * 购物车书籍货品勾选状态
     * <p>
     * 如果原来没有勾选，则设置勾选状态；如果书籍已经勾选，则设置非勾选状态。
     *
     * @param userId 用户ID
     * @param body   购物车书籍信息， { productIds: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
    @PostMapping("checked")
    public Object checked(@LoginUser Integer userId, @RequestBody String body) {
        logger.info("请求开始=>勾选购物车书籍,请求参数,userId:{},cart:{}", userId, JSONObject.toJSONString(body));

        if (userId == null) {
            logger.error("勾选购物车书籍失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }

        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
        if (productIds == null) {
            return ResponseUtil.badArgument();
        }

        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return ResponseUtil.badArgument();
        }
        Boolean isChecked = (checkValue == 1);
        try {
            cartService.updateCheck(userId, productIds, isChecked);
        } catch (Exception e) {
            logger.error("勾选购物车书籍失败:更新购物车书籍的勾选状态失败！");
            e.printStackTrace();
        }

        logger.info("请求结束=>勾选购物车书籍成功!");
        return index(userId);
    }

    /**
     * 购物车书籍删除
     *
     * @param userId 用户ID
     * @param body   购物车书籍信息， { productIds: xxx }
     * @return 购物车信息 成功则 { errno: 0, errmsg: '成功', data: xxx } 失败则 { errno: XXX,
     * errmsg: XXX }
     */
    @PostMapping("delete")
    public Object delete(@LoginUser Integer userId, @RequestBody String body) {
        logger.info("请求开始=>购物车书籍删除,请求参数,userId:{},cart:{}", userId, JSONObject.toJSONString(body));

        if (userId == null) {
            logger.error("购物车书籍删除:用户未登录！！！");
            return ResponseUtil.unlogin();
        }

        if (body == null) {
            return ResponseUtil.badArgument();
        }

        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");

        if (productIds == null || productIds.size() == 0) {
            return ResponseUtil.badArgument();
        }

        try {
            cartService.delete(productIds, userId);
        } catch (Exception e) {
            logger.error("购物车书籍删除失败:操作数据库删除用户书籍失败！");
            e.printStackTrace();
        }

        logger.info("请求结束=>购物车书籍删除成功:清理的productIds:{}", JSONObject.toJSONString(productIds));
        return index(userId);
    }

    /**
     * 购物车书籍货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车书籍货品数量
     */
    @GetMapping("bookscount")
    public Object bookscount(@LoginUser Integer userId) {
        logger.info("请求开始=>登录用户购物车书籍数量,请求参数,userId:{}", userId);

        if (userId == null) {// 如果用户未登录，则直接显示购物书籍数量为0
            return ResponseUtil.ok(0);
        }

        int booksCount = 0;
        try {
            List<JnCart> cartList = cartService.queryByUid(userId);
            for (JnCart cart : cartList) {
                booksCount += cart.getNumber();
            }
        } catch (Exception e) {
            logger.error("获取登录用户购物车书籍数量失败！");
            e.printStackTrace();
        }

        logger.info("请求结束=>获取登录用户购物车书籍数量成功:书籍总数：{}", booksCount);
        return ResponseUtil.ok(booksCount);
    }

    /**
     * 购物车下单
     *
     * @param userId    用户ID
     * @param cartId    购物车书籍ID： 如果购物车书籍ID是空，则下单当前用户所有购物车书籍； 如果购物车书籍ID非空，则只下单当前购物车书籍。
     * @param addressId 收货地址ID： 如果收货地址ID是空，则查询当前用户的默认地址。
     * @return 购物车操作结果
     */
    @GetMapping("checkout")
    public Object checkout(@LoginUser Integer userId, Integer cartId, Integer addressId) {
        logger.info("请求开始=>用户购物车下单,请求参数,userId:{}", userId);

        if (userId == null) {
            logger.error("用户购物车下单失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }

        // 收货地址
        JnAddress checkedAddress = null;
        if (addressId == null || addressId.equals(0)) {
            checkedAddress = addressService.findDefault(userId);
            // 如果仍然没有地址，则是没有收获地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new JnAddress();
                checkedAddress.setId(0);
                addressId = 0;
            } else {
                addressId = checkedAddress.getId();
            }

        } else {
            checkedAddress = addressService.findById(addressId);
            // 如果null, 则报错
            if (checkedAddress == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        // 书籍价格
        List<JnCart> checkedBooksList = null;
        if (cartId == null || cartId.equals(0)) {// 如果未从购物车发起的下单，则获取用户选好的书籍
            checkedBooksList = cartService.queryByUidAndChecked(userId);
        } else {
            JnCart cart = cartService.findById(cartId);
            if (cart == null) {
                return ResponseUtil.badArgumentValue();
            }
            checkedBooksList = new ArrayList<>(1);
            checkedBooksList.add(cart);
        }

        Map<String, Object> data = new HashMap<>();

        BigDecimal booksTotalPrice = new BigDecimal(0.00);// 书籍总价 （包含团购减免，即减免团购后的书籍总价，多店铺需将所有书籍相加）
        BigDecimal totalFreightPrice = new BigDecimal(0.00);// 总配送费 （单店铺模式一个，多店铺模式多个配送费的总和）


        Boolean isAllInOneCompus = true;
        JnCompus tmpcompus = null;
        JnUser user = userService.findById(userId);
        for (JnCart cart : checkedBooksList) {
            JnBooks book = booksService.queryById(cart.getBooksId());
            JnCompus compus = compusService.findById(book.getCompuId());
            tmpcompus = compus;
            cart.setCompusname(compus.getCompusname());
            logger.info(compus.getCompusname(), user.getCompusname());
            if (!user.getCompusname().equals(cart.getCompusname())) {
                isAllInOneCompus = false;
            }
            booksTotalPrice = booksTotalPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
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

        data.put("isMultiOrderModel", 0);
        data.put("booksTotalPrice", booksTotalPrice);
        data.put("freightPrice", totalFreightPrice);

        data.put("checkedBooksList", checkedBooksList);


        // 用户积分减免
        BigDecimal integralPrice = new BigDecimal(0.00);

        BigDecimal orderTotalPrice = booksTotalPrice.add(totalFreightPrice);
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        // 返回界面的通用数据
        data.put("addressId", addressId);
        data.put("checkedAddress", checkedAddress);


        data.put("orderTotalPrice", orderTotalPrice);// 订单总价：booksTotalPrice + totalFreightPrice
        data.put("actualPrice", actualPrice);// 订单实际付款金额：orderTotalPrice - integralPrice

        logger.info("请求结束=>用户购物车下单,响应结果：{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }
}
