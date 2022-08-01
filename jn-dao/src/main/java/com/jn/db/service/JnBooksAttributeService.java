package com.jn.db.service;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnBooksAttributeMapper;
import com.jn.db.domain.JnBooksAttribute;
import com.jn.db.domain.JnBooksAttributeExample;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnBooksAttributeService {
	@Resource
	private JnBooksAttributeMapper booksAttributeMapper;

	public List<JnBooksAttribute> queryByGid(Integer booksId) {
		JnBooksAttributeExample example = new JnBooksAttributeExample();
		example.or().andBooksIdEqualTo(booksId).andDeletedEqualTo(false);
		return booksAttributeMapper.selectByExample(example);
	}

	public void add(JnBooksAttribute booksAttribute) {
		booksAttribute.setAddTime(LocalDateTime.now());
		booksAttribute.setUpdateTime(LocalDateTime.now());
		booksAttributeMapper.insertSelective(booksAttribute);
	}

	public JnBooksAttribute findById(Integer id) {
		return booksAttributeMapper.selectByPrimaryKey(id);
	}

	public void deleteByGid(Integer gid) {
		JnBooksAttributeExample example = new JnBooksAttributeExample();
		example.or().andBooksIdEqualTo(gid);
		booksAttributeMapper.logicalDeleteByExample(example);
	}
}
