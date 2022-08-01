package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnAddressMapper;
import com.jn.db.domain.JnAddress;
import com.jn.db.domain.JnAddressExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnAddressService {
	@Resource
	private JnAddressMapper addressMapper;

	public List<JnAddress> queryByUid(Integer uid) {
		JnAddressExample example = new JnAddressExample();
		example.or().andUserIdEqualTo(uid).andDeletedEqualTo(false);
		return addressMapper.selectByExample(example);
	}

	public JnAddress findById(Integer id) {
		return addressMapper.selectByPrimaryKey(id);
	}

	public int add(JnAddress address) {
		address.setAddTime(LocalDateTime.now());
		address.setUpdateTime(LocalDateTime.now());
		return addressMapper.insertSelective(address);
	}

	public int update(JnAddress address) {
		address.setUpdateTime(LocalDateTime.now());
		return addressMapper.updateByPrimaryKeySelective(address);
	}

	public void delete(Integer id) {
		addressMapper.logicalDeleteByPrimaryKey(id);
	}

	public JnAddress findDefault(Integer userId) {
		JnAddressExample example = new JnAddressExample();
		example.or().andUserIdEqualTo(userId).andIsDefaultEqualTo(true).andDeletedEqualTo(false);
		return addressMapper.selectOneByExample(example);
	}

	/**
	 * 取消用户的默认地址配置
	 * 
	 * @param userId
	 */
	public void resetDefault(Integer userId) {
		JnAddress address = new JnAddress();
		address.setIsDefault(false);
		address.setUpdateTime(LocalDateTime.now());
		JnAddressExample example = new JnAddressExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false).andIsDefaultEqualTo(true);
		addressMapper.updateByExampleSelective(address, example);
	}

	public List<JnAddress> querySelective(Integer userId, String name, Integer page, Integer limit, String sort,
			String order) {
		JnAddressExample example = new JnAddressExample();
		JnAddressExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return addressMapper.selectByExample(example);
	}
}
