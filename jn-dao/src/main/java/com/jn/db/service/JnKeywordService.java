package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnKeywordMapper;
import com.jn.db.domain.JnKeyword;
import com.jn.db.domain.JnKeywordExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnKeywordService {
	@Resource
	private JnKeywordMapper keywordsMapper;

	public JnKeyword queryDefault() {
		JnKeywordExample example = new JnKeywordExample();
		example.or().andIsDefaultEqualTo(true).andDeletedEqualTo(false);
		return keywordsMapper.selectOneByExample(example);
	}

	public List<JnKeyword> queryHots() {
		JnKeywordExample example = new JnKeywordExample();
		example.or().andIsHotEqualTo(true).andDeletedEqualTo(false);
		return keywordsMapper.selectByExample(example);
	}

	public List<JnKeyword> queryByKeyword(String keyword, Integer page, Integer size) {
		JnKeywordExample example = new JnKeywordExample();
		example.setDistinct(true);
		example.or().andKeywordLike("%" + keyword + "%").andDeletedEqualTo(false);
		PageHelper.startPage(page, size);
		return keywordsMapper.selectByExampleSelective(example, JnKeyword.Column.keyword);
	}

	public List<JnKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort,
			String order) {
		JnKeywordExample example = new JnKeywordExample();
		JnKeywordExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(keyword)) {
			criteria.andKeywordLike("%" + keyword + "%");
		}
		if (!StringUtils.isEmpty(url)) {
			criteria.andUrlLike("%" + url + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return keywordsMapper.selectByExample(example);
	}

	public void add(JnKeyword keywords) {
		keywords.setAddTime(LocalDateTime.now());
		keywords.setUpdateTime(LocalDateTime.now());
		keywordsMapper.insertSelective(keywords);
	}

	public JnKeyword findById(Integer id) {
		return keywordsMapper.selectByPrimaryKey(id);
	}

	public int updateById(JnKeyword keywords) {
		keywords.setUpdateTime(LocalDateTime.now());
		return keywordsMapper.updateByPrimaryKeySelective(keywords);
	}

	public void deleteById(Integer id) {
		keywordsMapper.logicalDeleteByPrimaryKey(id);
	}
}
