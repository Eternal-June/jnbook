package com.jn.admin.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jn.admin.annotation.RequiresPermissionsDesc;
import com.jn.core.util.ResponseUtil;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;
import com.jn.db.domain.JnUser;
import com.jn.db.service.JnUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 用户管理
 * */
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private JnUserService userService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile, String compusname, @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        logger.info("请求开始=>用户管理->会员管理->查询,请求参数,username:{},code:{},page:{}", username, mobile, page);

        List<JnUser> userList = userService.querySelective(username, mobile, compusname, page, limit, sort, order);
        long total = PageInfo.of(userList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", userList);

        logger.info("请求结束=>用户管理->会员管理->查询:响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    @RequiresPermissions("admin:user:delete")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody JnUser jnUser) {
        logger.info("请求开始=>用户管理->会员管理->禁用,请求参数:{}", JSONObject.toJSONString(jnUser));

        userService.deleteById(jnUser.getId());

        logger.info("请求结束=>推广管理->专题管理->删除,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:user:ban")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "禁用")
    @PostMapping("/ban")
    public Object ban(@RequestBody JnUser jnUser) {
        logger.info("请求开始=>用户管理->会员管理->禁用,请求参数:{}", JSONObject.toJSONString(jnUser));

        userService.banById(jnUser);

        logger.info("请求结束=>推广管理->专题管理->禁用,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:user:read")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "取消代理")
    @PostMapping("/cancelAgency")
    public Object cancelAgency(@RequestBody JnUser jnUser) {
        logger.info("请求开始=>用户管理->会员管理->取消,请求参数:{}", JSONObject.toJSONString(jnUser));

        if (userService.cancelAgencyById(jnUser)) {
            logger.info("请求开始=>用户管理->会员管理->取消,响应结果:{}", "成功!");
            return ResponseUtil.ok();
        } else {
            logger.info("请求开始=>用户管理->会员管理->取消,响应结果:{}", "失败!");
            return ResponseUtil.badArgument();
        }
    }


    @RequiresPermissions("admin:user:approveAgency")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "代理审批")
    @PostMapping("/approveAgency")
    public Object approveAgency(@RequestBody JnUser jnUser) {
        logger.info("请求开始=>用户管理->会员管理->禁用,请求参数:{}", JSONObject.toJSONString(jnUser));

        if (userService.approveAgencyById(jnUser)) {
            logger.info("请求结束=>推广管理->专题管理->审批,响应结果:{}", "成功!");
            return ResponseUtil.ok();
        } else {
            logger.info("请求结束=>推广管理->专题管理->审批,响应结果:{}", "失败!");
            return ResponseUtil.badArgument();
        }
    }
}
