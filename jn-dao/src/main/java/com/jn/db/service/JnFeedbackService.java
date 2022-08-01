package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnFeedbackMapper;
import com.jn.db.domain.JnFeedback;
import com.jn.db.domain.JnFeedbackExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qiguliuxing
 * @date 2017/12/27
 */
@Service
public class JnFeedbackService {
	@Autowired
	private JnFeedbackMapper feedbackMapper;

	public Integer add(JnFeedback feedback) {
		feedback.setAddTime(LocalDateTime.now());
		feedback.setUpdateTime(LocalDateTime.now());
		return feedbackMapper.insertSelective(feedback);
	}

	public List<JnFeedback> querySelective(Integer userId, String username, Integer page, Integer limit, String sort,
			String order) {
		JnFeedbackExample example = new JnFeedbackExample();
		JnFeedbackExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(username)) {
			criteria.andUsernameLike("%" + username + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return feedbackMapper.selectByExample(example);
	}
}
