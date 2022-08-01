package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnCollectMapper;
import com.jn.db.domain.JnCollect;
import com.jn.db.domain.JnCollectExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnCollectService {
	@Resource
	private JnCollectMapper collectMapper;

	public int count(int uid, Integer gid) {
		JnCollectExample example = new JnCollectExample();
		example.or().andUserIdEqualTo(uid).andValueIdEqualTo(gid).andDeletedEqualTo(false);
		return (int) collectMapper.countByExample(example);
	}

	public List<JnCollect> queryByType(Integer userId, Byte type, Integer page, Integer size) {
		JnCollectExample example = new JnCollectExample();
		example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false);
		example.setOrderByClause(JnCollect.Column.addTime.desc());
		PageHelper.startPage(page, size);
		return collectMapper.selectByExample(example);
	}

	public int countByType(Integer userId, Byte type) {
		JnCollectExample example = new JnCollectExample();
		example.or().andUserIdEqualTo(userId).andTypeEqualTo(type).andDeletedEqualTo(false);
		return (int) collectMapper.countByExample(example);
	}

	public JnCollect queryByTypeAndValue(Integer userId, Byte type, Integer valueId) {
		JnCollectExample example = new JnCollectExample();
		example.or().andUserIdEqualTo(userId).andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
		return collectMapper.selectOneByExample(example);
	}

	public void deleteById(Integer id) {
		collectMapper.logicalDeleteByPrimaryKey(id);
	}

	public int add(JnCollect collect) {
		collect.setAddTime(LocalDateTime.now());
		collect.setUpdateTime(LocalDateTime.now());
		return collectMapper.insertSelective(collect);
	}

	public List<JnCollect> querySelective(String userId, String valueId, Integer page, Integer size, String sort,
			String order) {
		JnCollectExample example = new JnCollectExample();
		JnCollectExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(valueId)) {
			criteria.andValueIdEqualTo(Integer.valueOf(valueId));
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return collectMapper.selectByExample(example);
	}
}
