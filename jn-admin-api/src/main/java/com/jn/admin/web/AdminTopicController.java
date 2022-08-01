package com.jn.admin.web;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jn.admin.annotation.RequiresPermissionsDesc;
import com.jn.core.util.ResponseUtil;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;
import com.jn.db.domain.JnTopic;
import com.jn.db.service.JnTopicService;

/*
 * 推广专题管理
 * */
@RestController
@RequestMapping("/admin/topic")
@Validated
public class AdminTopicController {
    private static final Logger logger = LoggerFactory.getLogger(AdminTopicController.class);

    @Autowired
    private JnTopicService topicService;

    @RequiresPermissions("admin:topic:list")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String title, String subtitle, @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        logger.info("请求开始=>推广管理->专题管理->查询,请求参数,title:{},subtitle:{},page:{}", title, subtitle, page);

        List<JnTopic> topicList = topicService.querySelective(title, subtitle, page, limit, sort, order);
        long total = PageInfo.of(topicList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", topicList);

        logger.info("请求结束=>推广管理->专题管理->查询:响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    private Object validate(JnTopic topic) {
        String title = topic.getTitle();
        if (StringUtils.isEmpty(title)) {
            return ResponseUtil.badArgument();
        }
        String content = topic.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal price = topic.getPrice();
        if (price == null) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:topic:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody JnTopic topic) {
        logger.info("请求开始=>推广管理->专题管理->添加,请求参数:{}", JSONObject.toJSONString(topic));

        Object error = validate(topic);
        if (error != null) {
            return error;
        }

        topicService.add(topic);

        logger.info("请求结束=>推广管理->专题管理->添加:响应结果:{}", JSONObject.toJSONString(topic));
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:read")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        logger.info("请求开始=>推广管理->专题管理->详情,请求参数,id:{}", id);

        JnTopic topic = topicService.findById(id);

        logger.info("请求结束=>推广管理->专题管理->详情:响应结果:{}", JSONObject.toJSONString(topic));
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:update")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody JnTopic topic) {
        logger.info("请求开始=>推广管理->专题管理->编辑,请求参数:{}", JSONObject.toJSONString(topic));

        Object error = validate(topic);
        if (error != null) {
            return error;
        }

        if (topicService.updateById(topic) == 0) {
            logger.error("推广管理->专题管理->编辑 错误:{}", "更新数据失败!");
            return ResponseUtil.updatedDataFailed();
        }

        logger.info("请求结束=>推广管理->专题管理->编辑,响应结果:{}", JSONObject.toJSONString(topic));
        return ResponseUtil.ok(topic);
    }

    @RequiresPermissions("admin:topic:delete")
    @RequiresPermissionsDesc(menu = {"推广管理", "专题管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody JnTopic topic) {
        logger.info("请求开始=>推广管理->专题管理->删除,请求参数:{}", JSONObject.toJSONString(topic));

        topicService.deleteById(topic.getId());

        logger.info("请求结束=>推广管理->专题管理->删除,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

}
