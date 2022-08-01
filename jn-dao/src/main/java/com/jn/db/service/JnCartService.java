package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnCartMapper;
import com.jn.db.domain.JnCart;
import com.jn.db.domain.JnCartExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnCartService {
	@Resource
	private JnCartMapper cartMapper;

	public JnCart queryExist(Integer booksId, Integer productId, Integer userId) {
		JnCartExample example = new JnCartExample();
		example.or().andBooksIdEqualTo(booksId).andProductIdEqualTo(productId).andUserIdEqualTo(userId)
				.andDeletedEqualTo(false);
		return cartMapper.selectOneByExample(example);
	}

	public void add(JnCart cart) {
		cart.setAddTime(LocalDateTime.now());
		cart.setUpdateTime(LocalDateTime.now());
		cartMapper.insertSelective(cart);
	}

	public int updateById(JnCart cart) {
		cart.setUpdateTime(LocalDateTime.now());
		return cartMapper.updateByPrimaryKeySelective(cart);
	}

	public List<JnCart> queryByUid(int userId) {
		JnCartExample example = new JnCartExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
		return cartMapper.selectByExample(example);
	}

	public List<JnCart> queryByUidAndChecked(Integer userId) {
		JnCartExample example = new JnCartExample();
		example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true).andDeletedEqualTo(false);
		return cartMapper.selectByExample(example);
	}

	public int delete(List<Integer> productIdList, int userId) {
		JnCartExample example = new JnCartExample();
		example.or().andUserIdEqualTo(userId).andProductIdIn(productIdList);
		return cartMapper.logicalDeleteByExample(example);
	}

	public JnCart findById(Integer id) {
		return cartMapper.selectByPrimaryKey(id);
	}

	public int updateCheck(Integer userId, List<Integer> idsList, Boolean checked) {
		JnCartExample example = new JnCartExample();
		example.or().andUserIdEqualTo(userId).andProductIdIn(idsList).andDeletedEqualTo(false);
		JnCart cart = new JnCart();
		cart.setChecked(checked);
		cart.setUpdateTime(LocalDateTime.now());
		return cartMapper.updateByExampleSelective(cart, example);
	}

	public void clearBooks(Integer userId) {
		JnCartExample example = new JnCartExample();
		example.or().andUserIdEqualTo(userId).andCheckedEqualTo(true);
		JnCart cart = new JnCart();
		cart.setDeleted(true);
		cartMapper.updateByExampleSelective(cart, example);
	}

	public List<JnCart> querySelective(Integer userId, Integer booksId, Integer page, Integer limit, String sort,
			String order) {
		JnCartExample example = new JnCartExample();
		JnCartExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(booksId)) {
			criteria.andBooksIdEqualTo(booksId);
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return cartMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		cartMapper.logicalDeleteByPrimaryKey(id);
	}

	public boolean checkExist(Integer booksId) {
		JnCartExample example = new JnCartExample();
		example.or().andBooksIdEqualTo(booksId).andCheckedEqualTo(true).andDeletedEqualTo(false);
		return cartMapper.countByExample(example) != 0;
	}
}
