package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnCommentMapper;
import com.jn.db.domain.JnComment;
import com.jn.db.domain.JnCommentExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnCommentService {
	@Resource
	private JnCommentMapper commentMapper;

	public List<JnComment> queryBooksByGid(Integer id, int offset, int limit) {
		JnCommentExample example = new JnCommentExample();
		example.setOrderByClause(JnComment.Column.addTime.desc());
		example.or().andValueIdEqualTo(id).andTypeEqualTo((byte) 0).andDeletedEqualTo(false);
		PageHelper.startPage(offset, limit);
		return commentMapper.selectByExample(example);
	}

	public List<JnComment> query(Byte type, Integer valueId, Integer showType, Integer offset, Integer limit) {
		JnCommentExample example = new JnCommentExample();
		example.setOrderByClause(JnComment.Column.addTime.desc());
		if (showType == 0) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
		} else if (showType == 1) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true)
					.andDeletedEqualTo(false);
		} else {
			throw new RuntimeException("showType不支持");
		}
		PageHelper.startPage(offset, limit);
		return commentMapper.selectByExample(example);
	}

	public int count(Byte type, Integer valueId, Integer showType) {
		JnCommentExample example = new JnCommentExample();
		if (showType == 0) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andDeletedEqualTo(false);
		} else if (showType == 1) {
			example.or().andValueIdEqualTo(valueId).andTypeEqualTo(type).andHasPictureEqualTo(true)
					.andDeletedEqualTo(false);
		} else {
			throw new RuntimeException("showType不支持");
		}
		return (int) commentMapper.countByExample(example);
	}

	public int save(JnComment comment) {
		comment.setAddTime(LocalDateTime.now());
		comment.setUpdateTime(LocalDateTime.now());
		return commentMapper.insertSelective(comment);
	}

	public List<JnComment> querySelective(String userId, String valueId, Integer page, Integer size, String sort,
			String order) {
		JnCommentExample example = new JnCommentExample();
		JnCommentExample.Criteria criteria = example.createCriteria();

		// type=2 是订单书籍回复，这里过滤
		criteria.andTypeNotEqualTo((byte) 2);

		if (!StringUtils.isEmpty(userId)) {
			criteria.andUserIdEqualTo(Integer.valueOf(userId));
		}
		if (!StringUtils.isEmpty(valueId)) {
			criteria.andValueIdEqualTo(Integer.valueOf(valueId)).andTypeEqualTo((byte) 0);
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return commentMapper.selectByExample(example);
	}

	public void deleteById(Integer id) {
		commentMapper.logicalDeleteByPrimaryKey(id);
	}

	public String queryReply(Integer id) {
		JnCommentExample example = new JnCommentExample();
		example.or().andTypeEqualTo((byte) 2).andValueIdEqualTo(id);
		List<JnComment> commentReply = commentMapper.selectByExampleSelective(example, JnComment.Column.content);
		// 目前业务只支持回复一次
		if (commentReply.size() == 1) {
			return commentReply.get(0).getContent();
		}
		return null;
	}

	public JnComment findById(Integer id) {
		return commentMapper.selectByPrimaryKey(id);
	}
}
