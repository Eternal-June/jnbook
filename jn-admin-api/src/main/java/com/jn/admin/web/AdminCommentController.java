package com.jn.admin.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jn.db.domain.JnComment;
import com.jn.db.service.JnCommentService;

/*
* 书籍评论管理（回复）
* */
@RestController
@RequestMapping("/admin/comment")
@Validated
public class AdminCommentController {
	private static final Logger logger = LoggerFactory.getLogger(AdminCommentController.class);

	@Autowired
	private JnCommentService commentService;

	@RequiresPermissions("admin:comment:list")
	@RequiresPermissionsDesc(menu = { "书籍管理", "评论管理" }, button = "查询")
	@GetMapping("/list")
	public Object list(String userId, String valueId, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort @RequestParam(defaultValue = "add_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("请求开始=>书籍管理->评论管理->查询,请求参数:userId:{},page:{}", userId, page);

		List<JnComment> brandList = commentService.querySelective(userId, valueId, page, limit, sort, order);
		long total = PageInfo.of(brandList).getTotal();
		Map<String, Object> data = new HashMap<>();
		data.put("total", total);
		data.put("items", brandList);

		logger.info("请求结束=>书籍管理->评论管理->查询:total:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	@RequiresPermissions("admin:comment:delete")
	@RequiresPermissionsDesc(menu = { "书籍管理", "评论管理" }, button = "删除")
	@PostMapping("/delete")
	public Object delete(@RequestBody JnComment comment) {
		logger.info("请求开始=>书籍管理->评论管理->删除,请求参数:{}", JSONObject.toJSONString(comment));

		Integer id = comment.getId();
		if (id == null) {
			return ResponseUtil.badArgument();
		}
		commentService.deleteById(id);

		logger.info("请求结束=>书籍管理->评论管理->删除:响应结果:{}", "成功!");
		return ResponseUtil.ok();
	}

}
