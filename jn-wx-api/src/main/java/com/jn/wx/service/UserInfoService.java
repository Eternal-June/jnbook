package com.jn.wx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.jn.db.domain.JnUser;
import com.jn.db.service.JnUserService;
import com.jn.wx.dao.UserInfo;

@Service
public class UserInfoService {
	@Autowired
	private JnUserService userService;

	public UserInfo getInfo(Integer userId) {
		JnUser user = userService.findById(userId);
		Assert.state(user != null, "用户不存在");
		UserInfo userInfo = new UserInfo();
		userInfo.setNickName(user.getNickname());
		userInfo.setAvatarUrl(user.getAvatar());
		return userInfo;
	}
}
