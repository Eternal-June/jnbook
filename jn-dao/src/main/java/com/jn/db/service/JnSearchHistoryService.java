package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnSearchHistoryMapper;
import com.jn.db.domain.JnSearchHistory;
import com.jn.db.domain.JnSearchHistoryExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnSearchHistoryService {
	@Resource
	private JnSearchHistoryMapper searchHistoryMapper;

	public void save(JnSearchHistory searchHistory) {
		searchHistory.setAddTime(LocalDateTime.now());
		searchHistory.setUpdateTime(LocalDateTime.now());
		searchHistoryMapper.insertSelective(searchHistory);
	}

	public List<JnSearchHistory> queryByUid(int uid) {
		JnSearchHistoryExample example = new JnSearchHistoryExample();
		example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
		example.setDistinct(true);
		return searchHistoryMapper.selectByExampleSelective(example, JnSearchHistory.Column.keyword);
	}

	public void deleteByUid(int uid) {
		JnSearchHistoryExample example = new JnSearchHistoryExample();
		example.or().andUserIdEqualTo(uid);
		searchHistoryMapper.logicalDeleteByExample(example);
	}

	public List<JnSearchHistory> querySelective(String userId, String keyword, Integer page, Integer size, String sort,
			String order) {
		JnSearchHistoryExample example = new JnSearchHistoryExample();
		JnSearchHistoryExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(keyword)) {
			criteria.andKeywordLike("%" + keyword + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return searchHistoryMapper.selectByExample(example);
	}
}
