package com.jn.wx.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jn.core.util.RegexUtil;
import com.jn.core.util.ResponseUtil;
import com.jn.db.domain.JnFeedback;
import com.jn.db.domain.JnUser;
import com.jn.db.service.JnFeedbackService;
import com.jn.db.service.JnUserService;
import com.jn.wx.annotation.LoginUser;

/**
 * 意见反馈服务
 *
 * @author CHENBO
 * @since 1.0.0
 * @QQ 623659388
 */
@RestController
@RequestMapping("/wx/feedback")
@Validated
public class WxFeedbackController {
	private static final Logger logger = LoggerFactory.getLogger(WxFeedbackController.class);

	@Autowired
	private JnFeedbackService feedbackService;
	@Autowired
	private JnUserService userService;

	private Object validate(JnFeedback feedback) {
		String content = feedback.getContent();
		if (StringUtils.isEmpty(content)) {
			return ResponseUtil.badArgument();
		}

		String type = feedback.getFeedType();
		if (StringUtils.isEmpty(type)) {
			return ResponseUtil.badArgument();
		}

		Boolean hasPicture = feedback.getHasPicture();
		if (hasPicture == null || !hasPicture) {
			feedback.setPicUrls(new String[0]);
		}

		// 测试手机号码是否正确
		String mobile = feedback.getMobile();
		if (StringUtils.isEmpty(mobile)) {
			return ResponseUtil.badArgument();
		}
		if (!RegexUtil.isMobileExact(mobile)) {
			return ResponseUtil.badArgument();
		}
		return null;
	}

	/**
	 * 添加意见反馈
	 *
	 * @param userId
	 *            用户ID
	 * @param feedback
	 *            意见反馈
	 * @return 操作结果
	 */
	@PostMapping("submit")
	public Object submit(@LoginUser Integer userId, @RequestBody JnFeedback feedback) {
		logger.info("请求开始=>添加意见反馈,请求参数,userId:{},size:{}", userId, JSONObject.toJSONString(feedback));

		if (userId == null) {
			logger.error("添加意见反馈失败:用户未登录！！！");
			return ResponseUtil.unlogin();
		}
		Object error = validate(feedback);
		if (error != null) {
			return error;
		}

		JnUser user = userService.findById(userId);
		String username = user.getUsername();
		feedback.setId(null);
		feedback.setUserId(userId);
		feedback.setUsername(username);
		// 状态默认是0，1表示状态已发生变化
		feedback.setStatus(1);
		feedbackService.add(feedback);

		logger.info("请求结束=>添加意见反馈,响应结果:{}", JSONObject.toJSONString(feedback));
		return ResponseUtil.ok();
	}

}
