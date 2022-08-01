package com.jn.admin.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jn.core.util.ResponseUtil;
import com.jn.db.service.JnBooksProductService;
import com.jn.db.service.JnBooksService;
import com.jn.db.service.JnOrderService;
import com.jn.db.service.JnUserService;

/*
 * 首页管理
 * */
@RestController
@RequestMapping("/admin/dashboard")
@Validated
public class AdminDashbordController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashbordController.class);

    @Autowired
    private JnUserService userService;
    @Autowired
    private JnBooksService booksService;
    @Autowired
    private JnBooksProductService productService;
    @Autowired
    private JnOrderService orderService;

    @GetMapping("")
    public Object info() {
        logger.info("请求开始=>系统管理->首页仪表盘查询");

        int userTotal = userService.count();
        int booksTotal = booksService.count();
        int productTotal = productService.count();
        int orderTotal = orderService.count();
        Map<String, Integer> data = new HashMap<>();
        data.put("userTotal", userTotal);
        data.put("booksTotal", booksTotal);
        data.put("productTotal", productTotal);
        data.put("orderTotal", orderTotal);

        logger.info("请求结束=>系统管理->首页仪表盘查询:响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

}
