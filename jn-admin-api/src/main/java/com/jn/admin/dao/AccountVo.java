package com.jn.admin.dao;

import java.io.Serializable;
import java.math.BigDecimal;

/*
* value object值对象
* */
/*
* 对应user_account表
* */
public class AccountVo implements Serializable {

	private static final long serialVersionUID = 1567048369574496965L;

	private Integer userId;

	private BigDecimal remainAmount;

	private BigDecimal totalAmount;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(BigDecimal remainAmount) {
		this.remainAmount = remainAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

}
