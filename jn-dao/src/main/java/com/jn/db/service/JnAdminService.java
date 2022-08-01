package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnAdminMapper;
import com.jn.db.domain.JnAdmin;
import com.jn.db.domain.JnAdminExample;
import com.jn.db.domain.JnAdmin.Column;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnAdminService {
	private final Column[] result = new Column[] { Column.id, Column.username, Column.avatar, Column.roleIds };
	@Resource
	private JnAdminMapper adminMapper;

	public List<JnAdmin> findAdmin(String username) {
		JnAdminExample example = new JnAdminExample();
		example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
		return adminMapper.selectByExample(example);
	}

	public JnAdmin findAdmin(Integer id) {
		return adminMapper.selectByPrimaryKey(id);
	}

	public List<JnAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order) {
		JnAdminExample example = new JnAdminExample();
		JnAdminExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(username)) {
			criteria.andUsernameLike("%" + username + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return adminMapper.selectByExampleSelective(example, result);
	}

	public int updateById(JnAdmin admin) {
		admin.setUpdateTime(LocalDateTime.now());
		return adminMapper.updateByPrimaryKeySelective(admin);
	}

	public void deleteById(Integer id) {
		adminMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnAdmin admin) {
		admin.setAddTime(LocalDateTime.now());
		admin.setUpdateTime(LocalDateTime.now());
		adminMapper.insertSelective(admin);
	}

	public JnAdmin findById(Integer id) {
		return adminMapper.selectByPrimaryKeySelective(id, result);
	}

	public JnAdmin findByUserId(Integer userId) {
		JnAdminExample example=new JnAdminExample();
		example.or().andUserIdEqualTo(userId);
		return adminMapper.selectOneByExample(example);
	}

	public List<JnAdmin> allAdmin() {
		JnAdminExample example = new JnAdminExample();
		example.or().andDeletedEqualTo(false);
		return adminMapper.selectByExample(example);
	}
}
