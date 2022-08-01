package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnIssueMapper;
import com.jn.db.domain.JnIssue;
import com.jn.db.domain.JnIssueExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnIssueService {
	@Resource
	private JnIssueMapper issueMapper;

	public List<JnIssue> query() {
		JnIssueExample example = new JnIssueExample();
		example.or().andDeletedEqualTo(false);
		return issueMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		issueMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnIssue issue) {
		issue.setAddTime(LocalDateTime.now());
		issue.setUpdateTime(LocalDateTime.now());
		issueMapper.insertSelective(issue);
	}

	public List<JnIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
		JnIssueExample example = new JnIssueExample();
		JnIssueExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(question)) {
			criteria.andQuestionLike("%" + question + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return issueMapper.selectByExample(example);
	}

	public int updateById(JnIssue issue) {
		issue.setUpdateTime(LocalDateTime.now());
		return issueMapper.updateByPrimaryKeySelective(issue);
	}

	public JnIssue findById(Integer id) {
		return issueMapper.selectByPrimaryKey(id);
	}
}
