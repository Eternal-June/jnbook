package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnFootprintMapper;
import com.jn.db.domain.JnFootprint;
import com.jn.db.domain.JnFootprintExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnFootprintService {
	@Resource
	private JnFootprintMapper footprintMapper;

	public List<JnFootprint> queryByAddTime(Integer userId, Integer page, Integer size) {
		JnFootprintExample example = new JnFootprintExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
		example.setOrderByClause(JnFootprint.Column.addTime.desc());
		PageHelper.startPage(page, size);
		return footprintMapper.selectByExample(example);
	}

	public JnFootprint findById(Integer id) {
		return footprintMapper.selectByPrimaryKey(id);
	}

	public void deleteById(Integer id) {
		footprintMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnFootprint footprint) {

		footprint.setAddTime(LocalDateTime.now());
		footprint.setUpdateTime(LocalDateTime.now());
		footprintMapper.insertSelective(footprint);
	}

	public List<JnFootprint> querySelective(String userId, String booksId, Integer page, Integer size, String sort,
			String order) {
		JnFootprintExample example = new JnFootprintExample();
		JnFootprintExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(booksId)) {
			criteria.andIdEqualTo(Integer.valueOf(booksId));
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return footprintMapper.selectByExample(example);
	}
}
