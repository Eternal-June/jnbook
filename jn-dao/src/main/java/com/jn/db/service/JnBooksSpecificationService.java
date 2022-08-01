package com.jn.db.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnBooksSpecificationMapper;
import com.jn.db.domain.JnBooksSpecification;
import com.jn.db.domain.JnBooksSpecificationExample;

@Service
public class JnBooksSpecificationService {
	@Resource
	private JnBooksSpecificationMapper booksSpecificationMapper;

	public List<JnBooksSpecification> queryByGid(Integer id) {
		JnBooksSpecificationExample example = new JnBooksSpecificationExample();
		example.or().andBooksIdEqualTo(id).andDeletedEqualTo(false);
		return booksSpecificationMapper.selectByExample(example);
	}

	public JnBooksSpecification findById(Integer id) {
		return booksSpecificationMapper.selectByPrimaryKey(id);
	}

	public void deleteByGid(Integer gid) {
		JnBooksSpecificationExample example = new JnBooksSpecificationExample();
		example.or().andBooksIdEqualTo(gid);
		booksSpecificationMapper.logicalDeleteByExample(example);
	}

	public void add(JnBooksSpecification booksSpecification) {
		booksSpecification.setAddTime(LocalDateTime.now());
		booksSpecification.setUpdateTime(LocalDateTime.now());
		booksSpecificationMapper.insertSelective(booksSpecification);
	}

	/**
	 * [ { name: '', valueList: [ {}, {}] }, { name: '', valueList: [ {}, {}] } ]
	 *
	 * @param id
	 * @return
	 */
	public Object getSpecificationVoList(Integer id) {
		List<JnBooksSpecification> booksSpecificationList = queryByGid(id);

		Map<String, VO> map = new HashMap<>();
		List<VO> specificationVoList = new ArrayList<>();

		for (JnBooksSpecification booksSpecification : booksSpecificationList) {
			String specification = booksSpecification.getSpecification();
			VO booksSpecificationVo = map.get(specification);
			if (booksSpecificationVo == null) {
				booksSpecificationVo = new VO();
				booksSpecificationVo.setName(specification);
				List<JnBooksSpecification> valueList = new ArrayList<>();
				valueList.add(booksSpecification);
				booksSpecificationVo.setValueList(valueList);
				map.put(specification, booksSpecificationVo);
				specificationVoList.add(booksSpecificationVo);
			} else {
				List<JnBooksSpecification> valueList = booksSpecificationVo.getValueList();
				valueList.add(booksSpecification);
			}
		}

		return specificationVoList;
	}

	private class VO {
		private String name;
		private List<JnBooksSpecification> valueList;

		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<JnBooksSpecification> getValueList() {
			return valueList;
		}

		public void setValueList(List<JnBooksSpecification> valueList) {
			this.valueList = valueList;
		}
	}

}
