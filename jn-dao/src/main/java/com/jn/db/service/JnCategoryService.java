package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnCategoryMapper;
import com.jn.db.domain.JnCategory;
import com.jn.db.domain.JnCategoryExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnCategoryService {
	@Resource
	private JnCategoryMapper categoryMapper;
	private JnCategory.Column[] CHANNEL = { JnCategory.Column.id, JnCategory.Column.name,
			JnCategory.Column.iconUrl };

	public List<JnCategory> queryL1WithoutRecommend(int offset, int limit) {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andLevelEqualTo("L1").andNameNotEqualTo("推荐").andDeletedEqualTo(false);
		PageHelper.startPage(offset, limit);
		return categoryMapper.selectByExample(example);
	}

	public List<JnCategory> queryL1(int offset, int limit) {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
		PageHelper.startPage(offset, limit);
		return categoryMapper.selectByExample(example);
	}

	public List<JnCategory> queryL1() {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
		return categoryMapper.selectByExample(example);
	}

	public List<JnCategory> queryByPid(Integer pid) {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andPidEqualTo(pid).andDeletedEqualTo(false);
		return categoryMapper.selectByExample(example);
	}

	public List<JnCategory> queryL2ByIds(List<Integer> ids) {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andIdIn(ids).andLevelEqualTo("L2").andDeletedEqualTo(false);
		return categoryMapper.selectByExample(example);
	}

	public JnCategory findById(Integer id) {
		return categoryMapper.selectByPrimaryKey(id);
	}

	public List<JnCategory> querySelective(String id, String name, Integer page, Integer size, String sort,
			String order) {
		JnCategoryExample example = new JnCategoryExample();
		JnCategoryExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(id)) {
			criteria.andIdEqualTo(Integer.valueOf(id));
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return categoryMapper.selectByExample(example);
	}

	public int updateById(JnCategory category) {
		category.setUpdateTime(LocalDateTime.now());
		return categoryMapper.updateByPrimaryKeySelective(category);
	}

	public void deleteById(Integer id) {
		categoryMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnCategory category) {
		category.setAddTime(LocalDateTime.now());
		category.setUpdateTime(LocalDateTime.now());
		categoryMapper.insertSelective(category);
	}

	public List<JnCategory> queryChannel() {
		JnCategoryExample example = new JnCategoryExample();
		example.or().andLevelEqualTo("L1").andDeletedEqualTo(false);
		PageHelper.startPage(1, 9);// 设置分页10
		return categoryMapper.selectByExampleSelective(example, CHANNEL);
	}
}
