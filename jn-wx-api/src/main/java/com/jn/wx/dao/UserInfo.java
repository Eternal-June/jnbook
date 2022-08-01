package com.jn.wx.dao;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author CHENBO
 * @since 1.0.0
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -5813029516433359765L;

    private Integer userId;
    private String nickName;
    private String userName;
    private String compusname;
    private String avatarUrl;
    private String openId;
    private String country;
    private String province;
    private String city;
    private String email;
    private String language;
    private String phone;
    private Byte userLevel;// 用户层级 0 普通用户，1 VIP用户，2 区域代理用户
    private String userLevelDesc;// 代理用户描述

    private Byte status;//状态
    private String registerDate;//注册日期

    public String getCompusname() {
        return compusname;
    }

    public void setCompusname(String compusname) {
        this.compusname = compusname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userNamel) {
        this.userName = userNamel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String OpenId) {
        this.openId = OpenId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Byte userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelDesc() {
        return userLevelDesc;
    }

    public void setUserLevelDesc(String userLevelDesc) {
        this.userLevelDesc = userLevelDesc;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

}
