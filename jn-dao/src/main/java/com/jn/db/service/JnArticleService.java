package com.jn.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnArticleMapper;
import com.jn.db.domain.JnArticle;
import com.jn.db.domain.JnArticle.Column;
import com.jn.db.domain.JnArticleExample;

@Service
public class JnArticleService {

	@Resource
	private JnArticleMapper articleMapper;

	private Column[] columns = new Column[] { Column.id, Column.title, Column.addTime, Column.type };

	public JnArticle findById(Integer id) {
		return articleMapper.selectByPrimaryKey(id);
	}

	public List<JnArticle> queryList(int offset, int limit, String sort, String order) {
		JnArticleExample example = new JnArticleExample();
		example.or().andDeletedEqualTo(false);
		example.setOrderByClause(sort + " " + order);
		PageHelper.startPage(offset, limit);
		return articleMapper.selectByExampleSelective(example, columns);
	}
	
	public boolean checkExistByTitle(String title) {
		JnArticleExample example = new JnArticleExample();
		example.or().andTitleEqualTo(title).andDeletedEqualTo(false);
		return articleMapper.countByExample(example) != 0;
	}

	public void add(JnArticle article) {
		article.setAddTime(LocalDateTime.now());
		article.setUpdateTime(LocalDateTime.now());
		articleMapper.insertSelective(article);
	}

	public List<JnArticle> querySelective(String title, Integer page, Integer size, String sort, String order) {
		JnArticleExample example = new JnArticleExample();
		JnArticleExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(title)) {
			criteria.andTitleLike("%" + title + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return articleMapper.selectByExampleWithBLOBs(example);
	}

	public int updateById(JnArticle article) {
		article.setUpdateTime(LocalDateTime.now());
		return articleMapper.updateByPrimaryKeySelective(article);
	}

	public void deleteById(Integer id) {
		articleMapper.logicalDeleteByPrimaryKey(id);
	}
}
