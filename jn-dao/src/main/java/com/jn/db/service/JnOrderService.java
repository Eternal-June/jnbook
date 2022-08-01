package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnOrderMapper;
import com.jn.db.dao.ex.OrderMapper;
import com.jn.db.domain.JnOrder;
import com.jn.db.domain.JnOrderExample;
import com.jn.db.util.OrderUtil;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class JnOrderService {
	@Resource
	private JnOrderMapper jnOrderMapper;
	@Resource
	private OrderMapper orderMapper;

	public int add(JnOrder order) {
		order.setAddTime(LocalDateTime.now());
		order.setUpdateTime(LocalDateTime.now());
		return jnOrderMapper.insertSelective(order);
	}

	public int count(Integer userId) {
		JnOrderExample example = new JnOrderExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
		return (int) jnOrderMapper.countByExample(example);
	}

	public JnOrder findById(Integer orderId) {
		return jnOrderMapper.selectByPrimaryKey(orderId);
	}

	private String getRandomNum(Integer num) {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public int countByOrderSn(Integer userId, String orderSn) {
		JnOrderExample example = new JnOrderExample();
		example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
		return (int) jnOrderMapper.countByExample(example);
	}

	// TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
	public String generateOrderSn(Integer userId) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
		String now = df.format(LocalDate.now());
		String orderSn = now + getRandomNum(6);
		while (countByOrderSn(userId, orderSn) != 0) {
			orderSn = getRandomNum(6);
		}
		return orderSn;
	}

	public List<JnOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer size) {
		JnOrderExample example = new JnOrderExample();
		example.setOrderByClause(JnOrder.Column.addTime.desc());
		JnOrderExample.Criteria criteria = example.or();
		criteria.andUserIdEqualTo(userId);
		if (orderStatus != null) {
			criteria.andOrderStatusIn(orderStatus);
		}
		criteria.andDeletedEqualTo(false);
		PageHelper.startPage(page, size);
		return jnOrderMapper.selectByExample(example);
	}

	public List<JnOrder> querySelective(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page,
			Integer size, String sort, String order) {
		JnOrderExample example = new JnOrderExample();
		JnOrderExample.Criteria criteria = example.createCriteria();

		if (userId != null) {
			criteria.andUserIdEqualTo(userId);
		}
		if (!StringUtils.isEmpty(orderSn)) {
			criteria.andOrderSnEqualTo(orderSn);
		}
		if (orderStatusArray != null && orderStatusArray.size() != 0) {
			criteria.andOrderStatusIn(orderStatusArray);
		}
		criteria.andDeletedEqualTo(false);

		if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
			example.setOrderByClause(sort + " " + order);
		}

		PageHelper.startPage(page, size);
		return jnOrderMapper.selectByExample(example);
	}

	public int updateWithOptimisticLocker(JnOrder order) {
		LocalDateTime preUpdateTime = order.getUpdateTime();
		order.setUpdateTime(LocalDateTime.now());
		return orderMapper.updateWithOptimisticLocker(preUpdateTime, order);
	}

	public void deleteById(Integer id) {
		jnOrderMapper.logicalDeleteByPrimaryKey(id);
	}

	public int count() {
		JnOrderExample example = new JnOrderExample();
		example.or().andDeletedEqualTo(false);
		return (int) jnOrderMapper.countByExample(example);
	}

	public List<JnOrder> queryUnpaid() {
		JnOrderExample example = new JnOrderExample();
		example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false);
		return jnOrderMapper.selectByExample(example);
	}

	public List<JnOrder> queryUnconfirm() {
		JnOrderExample example = new JnOrderExample();
		example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeIsNotNull().andDeletedEqualTo(false);
		return jnOrderMapper.selectByExample(example);
	}

	public JnOrder findBySn(String orderSn) {
		JnOrderExample example = new JnOrderExample();
		example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
		return jnOrderMapper.selectOneByExample(example);
	}

	public Map<Object, Object> orderInfo(Integer userId) {
		JnOrderExample example = new JnOrderExample();
		example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
		List<JnOrder> orders = jnOrderMapper.selectByExampleSelective(example, JnOrder.Column.orderStatus,
				JnOrder.Column.comments);

		int unpaid = 0;
		int unship = 0;
		int unrecv = 0;
		int uncomment = 0;
		for (JnOrder order : orders) {
			if (OrderUtil.isCreateStatus(order)) {
				unpaid++;
			} else if (OrderUtil.isPayStatus(order)) {
				unship++;
			} else if (OrderUtil.isShipStatus(order)) {
				unrecv++;
			} else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
				uncomment += order.getComments();
			} else {
				// do nothing
			}
		}

		Map<Object, Object> orderInfo = new HashMap<Object, Object>();
		orderInfo.put("unpaid", unpaid);
		orderInfo.put("unship", unship);
		orderInfo.put("unrecv", unrecv);
		orderInfo.put("uncomment", uncomment);
		return orderInfo;

	}

	public List<JnOrder> queryComment() {
		JnOrderExample example = new JnOrderExample();
		example.or().andCommentsGreaterThan((short) 0).andDeletedEqualTo(false);
		return jnOrderMapper.selectByExample(example);
	}

}
