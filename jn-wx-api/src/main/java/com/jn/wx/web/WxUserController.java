package com.jn.wx.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.jn.core.consts.CommConsts;
import com.jn.core.notify.NotifyService;
import com.jn.core.system.SystemConfig;
import com.jn.core.type.UserTypeEnum;
import com.jn.core.util.JacksonUtil;
import com.jn.core.util.RegexUtil;
import com.jn.core.util.bcrypt.BCryptPasswordEncoder;
import com.jn.db.service.JnBooksService;
import com.jn.wx.dao.UserInfo;
import com.jn.wx.dao.UserToken;
import com.jn.wx.service.CaptchaCodeManager;
import com.jn.wx.service.HomeCacheManager;
import com.jn.wx.service.UserTokenManager;
import com.jn.wx.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.jn.core.util.ResponseUtil;
import com.jn.db.domain.JnUser;
import com.jn.db.domain.JnUserAccount;
import com.jn.db.service.JnAccountService;
import com.jn.db.service.JnOrderService;
import com.jn.db.service.JnUserService;
import com.jn.wx.annotation.LoginUser;
import com.jn.wx.util.WxResponseCode;
import com.jn.wx.util.WxResponseUtil;

import javax.servlet.http.HttpServletRequest;

import static com.jn.wx.util.WxResponseCode.*;
import static com.jn.wx.util.WxResponseCode.AUTH_OPENID_BINDED;

/**
 * 用户服务
 */
@RestController
@RequestMapping("/wx/user")
@Validated
public class WxUserController {
    private static final Logger logger = LoggerFactory.getLogger(WxUserController.class);

    @Autowired
    private JnBooksService booksService;

    @Autowired
    private JnOrderService orderService;

    @Autowired
    private JnAccountService accountService;

    @Autowired
    private JnUserService userService;

    @Autowired
    private WxMaService wxService;


    /**
     * 用户个人页面数据
     * <p>
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("index")
    public Object list(@LoginUser Integer userId) {
        logger.info("【请求开始】用户个人页面数据,请求参数,userId:{}", userId);

        if (userId == null) {
            logger.error("用户个人页面数据查询失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("order", orderService.orderInfo(userId));

        // 查询用户账号的总金额和剩余金额
        JnUserAccount userAccount = accountService.findShareUserAccountByUserId(userId);
        BigDecimal totalAmount = new BigDecimal(0.00);
        BigDecimal remainAmount = new BigDecimal(0.00);
        if (userAccount != null) {
            totalAmount = userAccount.getTotalAmount();
            remainAmount = userAccount.getRemainAmount();
        }
        data.put("totalAmount", totalAmount);
        data.put("remainAmount", remainAmount);


        logger.info("【请求结束】用户个人页面数据,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    /**
     * 申请代理用户
     * <p>
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @PostMapping("applyAgency")
    public Object applyAgency(@LoginUser Integer userId) {
        logger.info("请求开始=>用户个人页面代理申请,请求参数,userId:{}", userId);

        if (userId == null) {
            logger.error("用户个人页面代理申请:用户未登录！！！");
            return ResponseUtil.unlogin();
        }

        JnUser user = userService.findById(userId);

        //用户存在且未注销，未禁用
        if (user != null && user.getStatus().intValue() != 1 && user.getStatus().intValue() != 2) {
            // 查询用户账号,不存在则删除，如已经存在，不管状态如何都不做改变
            JnUserAccount userAccount = accountService.findShareUserAccountByUserId(userId);
            if (userAccount == null) {//如果不存在则新建一个账户
                userAccount = new JnUserAccount();
                userAccount.setRemainAmount(new BigDecimal(0));
                userAccount.setStatus((byte) 1);//生效
                userAccount.setTotalAmount(new BigDecimal(0));
                userAccount.setUserId(userId);
                accountService.add(userAccount);
            }
            user.setStatus((byte) 3);//代理申请中
            userService.updateById(user);
        } else {
            logger.error("用户个人页面代理申请出错");
            return WxResponseUtil.fail(WxResponseCode.INVALID_USER);
        }

        logger.info("请求结束=>用户个人页面代理申请,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    /**
     * 获取用户
     * <p>
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("getSharedUrl")
    public Object getSharedUrl(@LoginUser Integer userId) {
        logger.info("请求开始=>获取用户推广二维码图片URL,请求参数,userId:{}", userId);

        Map<String, Object> data = new HashMap<>();
        data.put("userSharedUrl", "");//默认设置没有
        if (userId == null) {
            logger.error("获取用户推广二维码图片URL:用户未登录！！！");
        } else {
            JnUserAccount userAccount = accountService.findShareUserAccountByUserId(userId);
        }

        logger.info("请求结束=>获取用户推广二维码图片URL,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    @GetMapping("sell")
    public Object sell(@LoginUser Integer userId) {
        logger.info("【请求开始】用户发布书籍数据,请求参数,userId:{}", userId);

        if (userId == null) {
            logger.error("用户个人页面数据查询失败:用户未登录！！！");
            return ResponseUtil.unlogin();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {
            Callable<List> myBooksListCallable = () -> booksService.queryByMe(userId, 0, SystemConfig.getNewLimit());
            FutureTask<List> myBooksListTask = new FutureTask<>(myBooksListCallable);
            executorService.submit(myBooksListTask);
            data.put("myBooksList", myBooksListTask.get());

            executorService.shutdown();

        } catch (Exception e) {
            logger.error("首页信息获取失败：{}", e.getMessage());
            e.printStackTrace();
        }

        logger.info("请求结束=>访问首页成功!");//暂不打印首页信息
        return ResponseUtil.ok(data);
    }


    /**
     * 个人信息修改
     */
    @PostMapping("editInfo")
    public Object register(@RequestBody String body, HttpServletRequest request) throws Exception {
        logger.info("请求开始=>修改信息,请求参数，body:{}", body);

        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        String email = JacksonUtil.parseString(body, "email");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");
        String nickname = JacksonUtil.parseString(body, "nickname");
        String compusname = JacksonUtil.parseString(body, "compusname");
        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
		String avatar=null;
        if (picUrls.size() != 0) {
            avatar = picUrls.get(0);
        }
        logger.info(username);
        List<JnUser> userList;
        if (!StringUtils.isEmpty(email)) {
            userList = userService.queryByEmail(email);
            if (userList.size() > 0) {
                logger.error("请求出错:{}", AUTH_EMAIL_REGISTERED.desc());
                return WxResponseUtil.fail(AUTH_EMAIL_REGISTERED);
            }
            if (!RegexUtil.isEmail(email)) {
                logger.error("请求出错:{}", AUTH_INVALID_EMAIL.desc());
                return WxResponseUtil.fail(AUTH_INVALID_EMAIL);
            }
            // 判断验证码是否正确
            String cacheCode = CaptchaCodeManager.getCachedCaptcha(email);
            logger.info(cacheCode);
            if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
                logger.error("请求出错:{}", AUTH_CAPTCHA_UNMATCH.desc());
                return WxResponseUtil.fail(AUTH_CAPTCHA_UNMATCH);
            }
        }

        JnUser user = userService.queryOneByUsername(username);
        logger.info(user.getId().toString());
        if (!StringUtils.isEmpty(password)) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);
        }
        if (!StringUtils.isEmpty(mobile)) {
            user.setPassword(mobile);
        }
        if (!StringUtils.isEmpty(compusname)) {
            user.setCompusname(compusname);
        }
        if (!StringUtils.isEmpty(email)) {
            user.setEmail(email);
        }
        if (!StringUtils.isEmpty(nickname)) {
            user.setNickname(nickname);
        }
        if(!StringUtils.isEmpty(avatar)){
            user.setAvatar(avatar);
        }
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.client(request));
        userService.updateById(user);

        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setCompusname(user.getCompusname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatarUrl(user.getAvatar());
        userInfo.setOpenId(user.getWeixinOpenid());
        userInfo.setUserName(user.getUsername());
        userInfo.setNickName(user.getNickname());
        userInfo.setPhone(user.getMobile());
        userInfo.setAvatarUrl(user.getAvatar());
        userInfo.setRegisterDate(new SimpleDateFormat("yyyy-MM-dd")
                .format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())));
        userInfo.setStatus(user.getStatus());
        userInfo.setUserId(user.getId());
        userInfo.setUserLevel(user.getUserLevel());// 用户层级
        userInfo.setUserLevelDesc(UserTypeEnum.getInstance(user.getUserLevel()).getDesc());// 用户层级描述

        UserToken userToken = null;
        try {
            userToken = UserTokenManager.generateToken(user.getId());
        } catch (Exception e) {
            logger.error("账号注册失败,生成token失败：{}", user.getId());
            e.printStackTrace();
            return ResponseUtil.fail();
        }

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", userToken.getToken());
        result.put("tokenExpire", userToken.getExpireTime().toString());
        result.put("userInfo", userInfo);

        logger.info("请求结束=>修改信息,响应结果:{}", JSONObject.toJSONString(result));
        return ResponseUtil.ok(result);
    }
}
