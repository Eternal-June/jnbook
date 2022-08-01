package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnStorageMapper;
import com.jn.db.domain.JnStorage;
import com.jn.db.domain.JnStorageExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnStorageService {
	@Autowired
	private JnStorageMapper storageMapper;

	public void deleteByKey(String key) {
		JnStorageExample example = new JnStorageExample();
		example.or().andKeyEqualTo(key);
		storageMapper.logicalDeleteByExample(example);
	}

	public void add(JnStorage storageInfo) {
		storageInfo.setAddTime(LocalDateTime.now());
		storageInfo.setUpdateTime(LocalDateTime.now());
		storageMapper.insertSelective(storageInfo);
	}

	public JnStorage findByKey(String key) {
		JnStorageExample example = new JnStorageExample();
		example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
		return storageMapper.selectOneByExample(example);
	}

	public int update(JnStorage storageInfo) {
		storageInfo.setUpdateTime(LocalDateTime.now());
		return storageMapper.updateByPrimaryKeySelective(storageInfo);
	}

	public JnStorage findById(Integer id) {
		return storageMapper.selectByPrimaryKey(id);
	}

	public List<JnStorage> querySelective(String key, String name, Integer page, Integer limit, String sort,
			String order) {
		JnStorageExample example = new JnStorageExample();
		JnStorageExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(key)) {
			criteria.andKeyEqualTo(key);
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return storageMapper.selectByExample(example);
	}
}
