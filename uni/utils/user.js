/**
 * 用户相关服务
 */
const util = require("./util.js");

const api = require("../config/api.js");
/**
 * Promise封装wx.checkSession
 */


function checkSession() {
  return new Promise(function (resolve, reject) {
    uni.checkSession({
      success: function () {
        resolve(true);
      },
      fail: function () {
        reject(false);
      }
    });
  });
}
/**
 * Promise封装wx.login
 */


function login() {
  return new Promise(function (resolve, reject) {
    uni.login({
      success: function (res) {
        if (res.code) {
          resolve(res);
        } else {
          reject(res);
        }
      },
      fail: function (err) {
        reject(err);
      }
    });
  });
}
/**
 * 调用微信登录
 */


function loginByWeixin(userInfo) {
  let shareUserId = uni.getStorageSync('shareUserId');

  if (!shareUserId || shareUserId == 'undefined') {
    shareUserId = 1;
  }

  return new Promise(function (resolve, reject) {
    return login().then(res => {
      //登录远程服务器
      util.request(api.AuthLoginByWeixin, {
        code: res.code,
        userInfo: userInfo,
        shareUserId: shareUserId
      }, 'POST').then(res => {
        if (res.errno === 0) {
          //存储用户信息
          uni.setStorageSync('userInfo', res.data.userInfo);
          uni.setStorageSync('token', res.data.token);
          resolve(res);
        } else {
          reject(res);
        }
      }).catch(err => {
        reject(err);
      });
    }).catch(err => {
      reject(err);
    });
  });
}
/**
 * 判断用户是否登录
 */


function checkLogin() {
  return new Promise(function (resolve, reject) {
    if (uni.getStorageSync('userInfo') && uni.getStorageSync('token')) {
      resolve(true);
    } else {
      reject(false);
    }
  });
}

module.exports = {
  loginByWeixin,
  checkLogin
};