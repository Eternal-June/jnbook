package com.jn.db.service;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnOrderBooksMapper;
import com.jn.db.domain.JnOrderBooks;
import com.jn.db.domain.JnOrderBooksExample;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnOrderBooksService {
	@Resource
	private JnOrderBooksMapper orderBooksMapper;

	public int add(JnOrderBooks orderBooks) {
		orderBooks.setAddTime(LocalDateTime.now());
		orderBooks.setUpdateTime(LocalDateTime.now());
		return orderBooksMapper.insertSelective(orderBooks);
	}

	public List<JnOrderBooks> queryByOid(Integer orderId) {
		JnOrderBooksExample example = new JnOrderBooksExample();
		example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
		return orderBooksMapper.selectByExample(example);
	}

	public List<JnOrderBooks> findByOidAndGid(Integer orderId, Integer booksId) {
		JnOrderBooksExample example = new JnOrderBooksExample();
		example.or().andOrderIdEqualTo(orderId).andBooksIdEqualTo(booksId).andDeletedEqualTo(false);
		return orderBooksMapper.selectByExample(example);
	}

	public JnOrderBooks findById(Integer id) {
		return orderBooksMapper.selectByPrimaryKey(id);
	}

	public void updateById(JnOrderBooks orderBooks) {
		orderBooks.setUpdateTime(LocalDateTime.now());
		orderBooksMapper.updateByPrimaryKeySelective(orderBooks);
	}

	public Short getComments(Integer orderId) {
		JnOrderBooksExample example = new JnOrderBooksExample();
		example.or().andOrderIdEqualTo(orderId).andDeletedEqualTo(false);
		long count = orderBooksMapper.countByExample(example);
		return (short) count;
	}

	public boolean checkExist(Integer booksId) {
		JnOrderBooksExample example = new JnOrderBooksExample();
		example.or().andBooksIdEqualTo(booksId).andDeletedEqualTo(false);
		return orderBooksMapper.countByExample(example) != 0;
	}
}
