package com.jn.db.service;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnSystemMapper;
import com.jn.db.domain.JnSystem;
import com.jn.db.domain.JnSystemExample;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JnSystemConfigService {
	@Resource
	private JnSystemMapper systemMapper;

	public List<JnSystem> queryAll() {
		JnSystemExample example = new JnSystemExample();
		example.or();
		return systemMapper.selectByExample(example);
	}
}
