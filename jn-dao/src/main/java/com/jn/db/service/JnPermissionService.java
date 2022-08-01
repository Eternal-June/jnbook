package com.jn.db.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnPermissionMapper;
import com.jn.db.domain.JnPermission;
import com.jn.db.domain.JnPermissionExample;

@Service
public class JnPermissionService {
	@Resource
	private JnPermissionMapper permissionMapper;

	public Set<String> queryByRoleIds(Integer[] roleIds) {
		Set<String> permissions = new HashSet<String>();
		if (roleIds.length == 0) {
			return permissions;
		}

		JnPermissionExample example = new JnPermissionExample();
		example.or().andRoleIdIn(Arrays.asList(roleIds)).andDeletedEqualTo(false);
		List<JnPermission> permissionList = permissionMapper.selectByExample(example);

		for (JnPermission permission : permissionList) {
			permissions.add(permission.getPermission());
		}

		return permissions;
	}

	public Set<String> queryByRoleId(Integer roleId) {
		Set<String> permissions = new HashSet<String>();
		if (roleId == null) {
			return permissions;
		}

		JnPermissionExample example = new JnPermissionExample();
		example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
		List<JnPermission> permissionList = permissionMapper.selectByExample(example);

		for (JnPermission permission : permissionList) {
			permissions.add(permission.getPermission());
		}

		return permissions;
	}

	public boolean checkSuperPermission(Integer roleId) {
		if (roleId == null) {
			return false;
		}

		JnPermissionExample example = new JnPermissionExample();
		example.or().andRoleIdEqualTo(roleId).andPermissionEqualTo("*").andDeletedEqualTo(false);
		return permissionMapper.countByExample(example) != 0;
	}

	public void deleteByRoleId(Integer roleId) {
		JnPermissionExample example = new JnPermissionExample();
		example.or().andRoleIdEqualTo(roleId).andDeletedEqualTo(false);
		permissionMapper.logicalDeleteByExample(example);
	}

	public void add(JnPermission JnPermission) {
		JnPermission.setAddTime(LocalDateTime.now());
		JnPermission.setUpdateTime(LocalDateTime.now());
		permissionMapper.insertSelective(JnPermission);
	}
}
