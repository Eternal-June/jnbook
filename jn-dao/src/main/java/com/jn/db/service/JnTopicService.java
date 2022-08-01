package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnTopicMapper;
import com.jn.db.domain.JnTopic;
import com.jn.db.domain.JnTopicExample;
import com.jn.db.domain.JnTopic.Column;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnTopicService {
	@Resource
	private JnTopicMapper topicMapper;
	private Column[] columns = new Column[] { Column.id, Column.title, Column.subtitle, Column.price, Column.picUrl,
			Column.readCount };

	public List<JnTopic> queryList(int offset, int limit) {
		return queryList(offset, limit, "add_time", "desc");
	}

	public List<JnTopic> queryList(int offset, int limit, String sort, String order) {
		JnTopicExample example = new JnTopicExample();
		example.or().andDeletedEqualTo(false);
		example.setOrderByClause(sort + " " + order);
		PageHelper.startPage(offset, limit);
		return topicMapper.selectByExampleSelective(example, columns);
	}

	public int queryTotal() {
		JnTopicExample example = new JnTopicExample();
		example.or().andDeletedEqualTo(false);
		return (int) topicMapper.countByExample(example);
	}

	public JnTopic findById(Integer id) {
		JnTopicExample example = new JnTopicExample();
		example.or().andIdEqualTo(id).andDeletedEqualTo(false);
		return topicMapper.selectOneByExampleWithBLOBs(example);
	}

	public List<JnTopic> queryRelatedList(Integer id, int offset, int limit) {
		JnTopicExample example = new JnTopicExample();
		example.or().andIdEqualTo(id).andDeletedEqualTo(false);
		List<JnTopic> topics = topicMapper.selectByExample(example);
		if (topics.size() == 0) {
			return queryList(offset, limit, "add_time", "desc");
		}
		JnTopic topic = topics.get(0);

		example = new JnTopicExample();
		example.or().andIdNotEqualTo(topic.getId()).andDeletedEqualTo(false);
		PageHelper.startPage(offset, limit);
		List<JnTopic> relateds = topicMapper.selectByExampleWithBLOBs(example);
		if (relateds.size() != 0) {
			return relateds;
		}

		return queryList(offset, limit, "add_time", "desc");
	}

	public List<JnTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort,
			String order) {
		JnTopicExample example = new JnTopicExample();
		JnTopicExample.Criteria criteria = example.createCriteria();

		if (!StringUtils.isEmpty(title)) {
			criteria.andTitleLike("%" + title + "%");
		}
		if (!StringUtils.isEmpty(subtitle)) {
			criteria.andSubtitleLike("%" + subtitle + "%");
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, limit);
		return topicMapper.selectByExampleWithBLOBs(example);
	}

	public int updateById(JnTopic topic) {
		topic.setUpdateTime(LocalDateTime.now());
		JnTopicExample example = new JnTopicExample();
		example.or().andIdEqualTo(topic.getId());
		return topicMapper.updateByExampleSelective(topic, example);
	}

	public void deleteById(Integer id) {
		topicMapper.logicalDeleteByPrimaryKey(id);
	}

	public void add(JnTopic topic) {
		topic.setAddTime(LocalDateTime.now());
		topic.setUpdateTime(LocalDateTime.now());
		topicMapper.insertSelective(topic);
	}

}
