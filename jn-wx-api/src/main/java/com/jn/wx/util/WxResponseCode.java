package com.jn.wx.util;

public enum WxResponseCode {

	AUTH_INVALID_ACCOUNT(700, "账号密码不对"), AUTH_CAPTCHA_UNSUPPORT(701, "小程序后台验证码服务不支持"), AUTH_CAPTCHA_FREQUENCY(702,
			"验证码未超时1分钟，不能发送"), AUTH_CAPTCHA_UNMATCH(703, "验证码错误"), AUTH_NAME_REGISTERED(704,
					"用户名已注册"),
	AUTH_EMAIL_REGISTERED(705, "邮箱已注册"),
	AUTH_INVALID_EMAIL(753, "邮箱格式不正确"),
	AUTH_MOBILE_UNREGISTERED(706, "手机号未注册"),
	AUTH_INVALID_MOBILE(707, "手机号格式不正确"), AUTH_OPENID_UNACCESS(708,
									"获取腾讯官方 openid失败"), AUTH_OPENID_BINDED(709, "openid已绑定账号"),

	BOOKS_UNSHELVE(710, "书籍已下架"), BOOKS_NO_STOCK(711, "库存不足"), BOOKS_UNKNOWN(712, "未找到对应的书籍"),
	// BOOKS_INVALID(713, ""),

	// 订单当前状态下不支持用户的操作，例如书籍未发货状态用户执行确认收货是不可能的。
	ORDER_UNKNOWN(720, "订单不存在"), ORDER_INVALID(721, "不是当前用户的订单"),
	// ORDER_CHECKOUT_FAIL(722, ""),
	ORDER_REPAY_OPERATION(722, ""), ORDER_NOT_COMMENT(723, "订单不能评价"), ORDER_PAY_FAIL(724,
			"订单支付失败"), ORDER_INVALID_OPERATION(725, "订单不能取消"), ORDER_COMMENTED(726, "订单书籍已评价"), ORDER_COMMENT_EXPIRED(
					727, "当前订单评价时间已过期"), ORDER_CONFIRM_OPERATION(728, "订单不能确认收货"), ORDER_DEL_OPERATION(729, "订单不能删除"),
	// ORDER_CANCEL_FAIL(820,""),


	IS_MY_BOOK(740, "这是自己的书!"),
	// 提现错误
	APPLY_WITHDRAWAL_FAIL(750, "申请提现金额不能大于可提现金额"), APPLY_WITHDRAWAL_EXIST(751, "已存在提现申请"),INVALID_COUPON(752, "无效购物券"), INVALID_USER(753, "无效用户");

	private final Integer code;
	private final String desc;

	WxResponseCode(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static WxResponseCode getInstance(Integer code) {
		if (code != null) {
			for (WxResponseCode tmp : WxResponseCode.values()) {
				if (tmp.code.intValue() == code.intValue()) {
					return tmp;
				}
			}
		}
		return null;
	}

	public Integer code() {
		return code;
	}

	public String desc() {
		return desc;
	}

}
