package com.jn.db.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jn.db.dao.ex.BooksProductMapper;
import com.jn.db.dao.JnBooksProductMapper;
import com.jn.db.domain.JnBooksProduct;
import com.jn.db.domain.JnBooksProductExample;

@Service
public class JnBooksProductService {
	@Resource
	private JnBooksProductMapper jnBooksProductMapper;
	@Resource
	private BooksProductMapper booksProductMapper;

	public List<JnBooksProduct> queryByGid(Integer gid) {
		JnBooksProductExample example = new JnBooksProductExample();
		example.or().andBooksIdEqualTo(gid).andDeletedEqualTo(false);
		return jnBooksProductMapper.selectByExample(example);
	}

	public JnBooksProduct findById(Integer id) {
		return jnBooksProductMapper.selectByPrimaryKey(id);
	}

	public int updateByOne(JnBooksProduct booksProduct){
		return jnBooksProductMapper.updateByPrimaryKey(booksProduct);
	}

	public List<JnBooksProduct> findByBooksId(Integer booksId) {
		JnBooksProductExample example=new JnBooksProductExample();
		example.or().andBooksIdEqualTo(booksId);
		return jnBooksProductMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		jnBooksProductMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnBooksProduct booksProduct) {
		booksProduct.setAddTime(LocalDateTime.now());
		booksProduct.setUpdateTime(LocalDateTime.now());
		jnBooksProductMapper.insertSelective(booksProduct);
	}

	public int count() {
		JnBooksProductExample example = new JnBooksProductExample();
		example.or().andDeletedEqualTo(false);
		return (int) jnBooksProductMapper.countByExample(example);
	}

	public void deleteByGid(Integer gid) {
		JnBooksProductExample example = new JnBooksProductExample();
		example.or().andBooksIdEqualTo(gid);
		jnBooksProductMapper.logicalDeleteByExample(example);
	}

	public int addStock(Integer id, Short num) {
		return booksProductMapper.addStock(id, num);
	}

	public int addBrowse(Integer id, Short num) {
		return booksProductMapper.addBrowse(id, num);// 新增书籍流量量
	}

	public int reduceStock(Integer id, Integer booksId, Short num) {
		return booksProductMapper.reduceStock(id, num);
	}
}
