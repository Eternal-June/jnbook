package com.jn.wx.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.jn.db.domain.JnBooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jn.core.util.ResponseUtil;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;
import com.jn.db.domain.JnTopic;
import com.jn.db.service.JnBooksService;
import com.jn.db.service.JnTopicService;

/**
 * 专题服务
 */
@RestController
@RequestMapping("/wx/topic")
@Validated
public class WxTopicController {
	private static final Logger logger = LoggerFactory.getLogger(WxTopicController.class);

	@Autowired
	private JnTopicService topicService;
	@Autowired
	private JnBooksService booksService;

	/**
	 * 专题列表
	 *
	 * @param page
	 *            分页页数
	 * @param size
	 *            分页大小
	 * @return 专题列表
	 */
	@GetMapping("list")
	public Object list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size,
			@Sort @RequestParam(defaultValue = "add_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("请求开始=>获取专题列表,请求参数,page:{},size:{}", page, size);

		List<JnTopic> topicList = topicService.queryList(page, size, sort, order);
		int total = topicService.queryTotal();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data", topicList);
		data.put("count", total);

		logger.info("请求结束=>获取专题列表,响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	/**
	 * 专题详情
	 *
	 * @param id
	 *            专题ID
	 * @return 专题详情
	 */
	@GetMapping("detail")
	public Object detail(@NotNull Integer id) {
		logger.info("请求开始=>获取专题详情,请求参数,id:{}", id);

		Map<String, Object> data = new HashMap<>();
		JnTopic topic = topicService.findById(id);
		data.put("topic", topic);
		List<JnBooks> books = new ArrayList<>();
		for (String idOrSn : topic.getBooks()) {
			try {
				Long sn = Long.parseLong(idOrSn);
				JnBooks good = null;
				if (sn.intValue() < Integer.MAX_VALUE) {
					good = booksService.findByIdVO(sn.intValue());
				}
				if (good == null) {//如果配置的不是id,则可能是SN
					good = booksService.findBySnVO(idOrSn);
				}
				if (null != good) books.add(good);
			} catch (Exception e) {
				logger.info("获取专题详情,根据配置的书籍id或sn获取书籍详情出错:{}", e.getMessage());
				e.printStackTrace();
			}
		}
		data.put("books", books);

		logger.info("请求结束=>获取专题详情,响应结果:{}", "成功");
		return ResponseUtil.ok(data);
	}

	/**
	 * 相关专题
	 *
	 * @param id
	 *            专题ID
	 * @return 相关专题
	 */
	@GetMapping("related")
	public Object related(@NotNull Integer id) {
		logger.info("请求开始=>相关专题列表,请求参数,id:{}", id);

		List<JnTopic> topicRelatedList = topicService.queryRelatedList(id, 0, 6);

		logger.info("请求结束=>相关专题列表,响应结果:相关专题数{}", topicRelatedList == null ? 0 : topicRelatedList.size());
		return ResponseUtil.ok(topicRelatedList);
	}
}
