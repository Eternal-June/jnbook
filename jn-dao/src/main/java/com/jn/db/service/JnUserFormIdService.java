package com.jn.db.service;

import org.springframework.stereotype.Service;

import com.jn.db.dao.JnUserFormidMapper;
import com.jn.db.domain.JnUserFormid;
import com.jn.db.domain.JnUserFormidExample;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class JnUserFormIdService {
	@Resource
	private JnUserFormidMapper formidMapper;

	/**
	 * 查找是否有可用的FormId
	 *
	 * @param openId
	 * @return
	 */
	public JnUserFormid queryByOpenId(String openId) {
		JnUserFormidExample example = new JnUserFormidExample();
		// 符合找到该用户记录，且可用次数大于1，且还未过期
		example.or().andOpenidEqualTo(openId).andExpireTimeGreaterThan(LocalDateTime.now());
		return formidMapper.selectOneByExample(example);
	}

	/**
	 * 更新或删除FormId
	 *
	 * @param userFormid
	 */
	public int updateUserFormId(JnUserFormid userFormid) {
		// 更新或者删除缓存
		if (userFormid.getIsprepay() && userFormid.getUseamount() > 1) {
			userFormid.setUseamount(userFormid.getUseamount() - 1);
			userFormid.setUpdateTime(LocalDateTime.now());
			return formidMapper.updateByPrimaryKey(userFormid);
		} else {
			return formidMapper.deleteByPrimaryKey(userFormid.getId());
		}
	}

	/**
	 * 添加一个 FormId
	 *
	 * @param userFormid
	 */
	public void addUserFormid(JnUserFormid userFormid) {
		userFormid.setAddTime(LocalDateTime.now());
		userFormid.setUpdateTime(LocalDateTime.now());
		formidMapper.insertSelective(userFormid);
	}
}
