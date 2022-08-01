package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnRegionMapper;
import com.jn.db.domain.JnRegion;
import com.jn.db.domain.JnRegionExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JnRegionService {

	@Resource
	private JnRegionMapper regionMapper;

	public List<JnRegion> getAll() {
		JnRegionExample example = new JnRegionExample();
		byte b = 4;
		example.or().andTypeNotEqualTo(b);
		return regionMapper.selectByExample(example);
	}

	public List<JnRegion> queryByPid(Integer parentId) {
		JnRegionExample example = new JnRegionExample();
		example.or().andPidEqualTo(parentId);
		return regionMapper.selectByExample(example);
	}

	public JnRegion findById(Integer id) {
		return regionMapper.selectByPrimaryKey(id);
	}

	public List<JnRegion> querySelective(String name, Integer code, Integer page, Integer size, String sort,
			String order) {
		JnRegionExample example = new JnRegionExample();
		JnRegionExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(name)) {
			criteria.andNameLike("%" + name + "%");
		}
		if (!StringUtils.isEmpty(code)) {
			criteria.andCodeEqualTo(code);
		}

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return regionMapper.selectByExample(example);
	}
}
