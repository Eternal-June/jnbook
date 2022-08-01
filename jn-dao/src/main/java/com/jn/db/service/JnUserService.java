package com.jn.db.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.Resource;

import com.jn.db.dao.JnAdminMapper;
import com.jn.db.dao.JnCompusMapper;
import com.jn.db.domain.*;
import com.jn.db.util.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnUserAccountMapper;
import com.jn.db.dao.JnUserMapper;

@Service
public class JnUserService {

    @Resource
    private JnUserMapper userMapper;

    @Resource
    private JnUserAccountMapper userAccountMapper;

    @Resource
    private JnAdminMapper adminMapper;

    @Autowired
    private JnAdminService adminService;

    public JnUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserVo findUserVoById(Integer userId) {
        JnUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        return userVo;
    }

    public JnUser queryByOid(String openId) {
        JnUserExample example = new JnUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(JnUser user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(JnUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<JnUser> querySelective(String username, String mobile, String compusname, Integer page, Integer size, String sort,
                                       String order) {
        JnUserExample example = new JnUserExample();
        JnUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(compusname)) {
            criteria.andCompusnameLike("%" + compusname + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return userMapper.selectByExample(example);
    }

    public int count() {
        JnUserExample example = new JnUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<JnUser> queryByUsername(String username) {
        JnUserExample example = new JnUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public JnUser queryOneByUsername(String username) {
        JnUserExample example = new JnUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        List<JnUser> list = userMapper.selectByExample(example);
        return list.get(0);
    }

    public boolean checkByUsername(String username) {
        JnUserExample example = new JnUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<JnUser> queryByMobile(String mobile) {
        JnUserExample example = new JnUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<JnUser> queryByEmail(String email) {
        JnUserExample example = new JnUserExample();
        example.or().andEmailEqualTo(email).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<JnUser> queryByOpenid(String openid) {
        JnUserExample example = new JnUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }

    public void banById(JnUser jnUser) {
        if (jnUser.getStatus() == 0) {
            jnUser.setStatus((byte) 1);
            updateById(jnUser);
        } else if (jnUser.getStatus() == 1) {
            jnUser.setStatus((byte) 0);
            updateById(jnUser);
        }
    }

    public boolean approveAgencyById(JnUser jnUser) {
        if (jnUser.getCompusname() != null) {
            jnUser.setStatus((byte) 0);
            jnUser.setUserLevel((byte) 2);
            JnAdmin admin = new JnAdmin();
            admin.setAddTime(LocalDateTime.now());
            admin.setAvatar(jnUser.getAvatar());
            admin.setDeleted(false);
            if (jnUser.getWeixinOpenid().equals(jnUser.getUsername())) {
                admin.setUsername(jnUser.getNickname());
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encodedPassword = encoder.encode(jnUser.getNickname());
                admin.setPassword(encodedPassword);
            } else {
                admin.setUsername(jnUser.getUsername());
                admin.setPassword(jnUser.getPassword());
            }
            Integer[] role = new Integer[1];
            role[0] = 6;
            admin.setRoleIds(role);
            admin.setTel(jnUser.getMobile());
            admin.setMail(jnUser.getEmail());
            admin.setUserId(jnUser.getId());
            adminMapper.insert(admin);
            userMapper.updateByPrimaryKey(jnUser);
            return true;
        }
        return false;
    }

    public boolean cancelAgencyById(JnUser jnUser) {
        if (jnUser.getCompusname() != null) {
            jnUser.setStatus((byte) 0);
            jnUser.setUserLevel((byte) 1);
            JnAdmin admin = adminService.findByUserId(jnUser.getId());
            adminMapper.logicalDeleteByPrimaryKey(admin.getId());
            userMapper.updateByPrimaryKey(jnUser);
            return true;
        }
        return false;
    }

    public JnUserAccount detailApproveByUserId(Integer userId) {
        // 获取账户数据
        JnUserAccountExample example = new JnUserAccountExample();
        example.or().andUserIdEqualTo(userId);

        JnUserAccount dbAccount = userAccountMapper.selectOneByExample(example);
        return dbAccount;
    }
}
