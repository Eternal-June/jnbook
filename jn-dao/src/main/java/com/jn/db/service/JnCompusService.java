package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnCompusMapper;
import com.jn.db.domain.JnCompus;
import com.jn.db.domain.JnCompusExample;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnCompusService {
    @Resource
    private JnCompusMapper compusMapper;

    public JnCompus findByCompusname(String compusname) {
        JnCompusExample example = new JnCompusExample();
        example.or().andCompusnameEqualTo(compusname).andDeletedEqualTo(false);
        return compusMapper.selectByExample(example).get(0);
    }

    public JnCompus findById(Integer id) {
        return compusMapper.selectByPrimaryKey(id);
    }

    public int add(JnCompus compus) {
        compus.setAddTime(LocalDateTime.now());
        compus.setUpdateTime(LocalDateTime.now());
        return compusMapper.insertSelective(compus);
    }

    public int update(JnCompus compus) {
        compus.setUpdateTime(LocalDateTime.now());
        return compusMapper.updateByPrimaryKeySelective(compus);
    }

    public void delete(Integer id) {
        compusMapper.logicalDeleteByPrimaryKey(id);
    }



    public List<JnCompus> querySelective(String name, Integer page, Integer limit, String sort,
                                          String order) {
        JnCompusExample example = new JnCompusExample();
        JnCompusExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andCompusnameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return compusMapper.selectByExample(example);
    }
}
