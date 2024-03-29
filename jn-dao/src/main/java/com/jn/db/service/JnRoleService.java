package com.jn.db.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnRoleMapper;
import com.jn.db.domain.JnRole;
import com.jn.db.domain.JnRoleExample;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JnRoleService {
	@Resource
	private JnRoleMapper roleMapper;

	public Set<String> queryByIds(Integer[] roleIds) {
		Set<String> roles = new HashSet<String>();
		if (roleIds.length == 0) {
			return roles;
		}

		JnRoleExample example = new JnRoleExample();
		example.or().andIdIn(Arrays.asList(roleIds)).andEnabledEqualTo(true).andDeletedEqualTo(false);
		List<JnRole> roleList = roleMapper.selectByExample(example);

		for (JnRole role : roleList) {
			roles.add(role.getName());
		}

		return roles;

	}

	public List<JnRole> querySelective(String roleName, Integer page, Integer size, String sort, String order) {
		JnRoleExample example = new JnRoleExample();
		JnRoleExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(roleName)) {
			criteria.andNameEqualTo("%" + roleName + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return roleMapper.selectByExample(example);
	}

	public JnRole findById(Integer id) {
		return roleMapper.selectByPrimaryKey(id);
	}

	public void add(JnRole role) {
		role.setAddTime(LocalDateTime.now());
		role.setUpdateTime(LocalDateTime.now());
		roleMapper.insertSelective(role);
	}

	public void deleteById(Integer id) {
		roleMapper.logicalDeleteByPrimaryKey(id);
	}

	public void updateById(JnRole role) {
		role.setUpdateTime(LocalDateTime.now());
		roleMapper.updateByPrimaryKeySelective(role);
	}

	public boolean checkExist(String name) {
		JnRoleExample example = new JnRoleExample();
		example.or().andNameEqualTo(name).andDeletedEqualTo(false);
		return roleMapper.countByExample(example) != 0;
	}

	public List<JnRole> queryAll() {
		JnRoleExample example = new JnRoleExample();
		example.or().andDeletedEqualTo(false);
		return roleMapper.selectByExample(example);
	}
}
