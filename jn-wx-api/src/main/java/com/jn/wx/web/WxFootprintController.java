package com.jn.wx.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jn.db.domain.JnBooks;
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
import com.jn.core.util.JacksonUtil;
import com.jn.core.util.ResponseUtil;
import com.jn.db.domain.JnFootprint;
import com.jn.db.service.JnFootprintService;
import com.jn.db.service.JnBooksService;
import com.jn.wx.annotation.LoginUser;

/**
 * 用户访问足迹服务
 */
@RestController
@RequestMapping("/wx/footprint")
@Validated
public class WxFootprintController {
	private static final Logger logger = LoggerFactory.getLogger(WxFootprintController.class);

	@Autowired
	private JnFootprintService footprintService;
	@Autowired
	private JnBooksService booksService;

	/**
	 * 删除用户足迹
	 *
	 * @param userId
	 *            用户ID
	 * @param body
	 *            请求内容， { id: xxx }
	 * @return 删除操作结果
	 */
	@PostMapping("delete")
	public Object delete(@LoginUser Integer userId, @RequestBody String body) {
		logger.info("请求开始=>删除用户足迹,请求参数,userId:{},body:{}", userId, body);

		if (userId == null) {
			logger.error("删除用户足迹:用户未登录！！！");
			return ResponseUtil.unlogin();
		}
		if (body == null) {
			return ResponseUtil.badArgument();
		}

		Integer footprintId = JacksonUtil.parseInteger(body, "id");
		if (footprintId == null) {
			return ResponseUtil.badArgument();
		}
		JnFootprint footprint = footprintService.findById(footprintId);

		if (footprint == null) {
			return ResponseUtil.badArgumentValue();
		}
		if (!footprint.getUserId().equals(userId)) {
			return ResponseUtil.badArgumentValue();
		}

		footprintService.deleteById(footprintId);

		logger.info("请求结束=>删除用户足迹成功!");
		return ResponseUtil.ok();
	}

	/**
	 * 用户足迹列表
	 *
	 * @param page
	 *            分页页数
	 * @param size
	 *            分页大小
	 * @return 用户足迹列表
	 */
	@GetMapping("list")
	public Object list(@LoginUser Integer userId, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer size) {
		logger.info("请求开始=>用户足迹列表查询,请求参数,userId:{}", userId);

		if (userId == null) {
			logger.error("删除用户足迹:用户未登录！！！");
			return ResponseUtil.unlogin();
		}

		List<JnFootprint> footprintList = footprintService.queryByAddTime(userId, page, size);
		long count = PageInfo.of(footprintList).getTotal();
		int totalPages = (int) Math.ceil((double) count / size);

		List<Object> footprintVoList = new ArrayList<>(footprintList.size());
		for (JnFootprint footprint : footprintList) {
			Map<String, Object> c = new HashMap<String, Object>();
			c.put("id", footprint.getId());
			c.put("booksId", footprint.getBooksId());
			c.put("addTime", footprint.getAddTime());

			JnBooks books = booksService.findById(footprint.getBooksId());
			logger.info(books.getName());
			c.put("name", books.getName());
			c.put("brief", books.getBrief());
			c.put("picUrl", books.getPicUrl());
			c.put("retailPrice", books.getRetailPrice());

			footprintVoList.add(c);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("footprintList", footprintVoList);
		result.put("totalPages", totalPages);

		logger.info("请求结束=>添加意见反馈,响应结果:{}", JSONObject.toJSONString(result));
		return ResponseUtil.ok(result);
	}

}
