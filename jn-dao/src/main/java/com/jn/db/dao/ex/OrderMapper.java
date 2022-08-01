package com.jn.db.dao.ex;

import org.apache.ibatis.annotations.Param;

import com.jn.db.domain.JnOrder;

import java.time.LocalDateTime;

public interface OrderMapper {
	int updateWithOptimisticLocker(@Param("lastUpdateTime") LocalDateTime lastUpdateTime,
			@Param("order") JnOrder order);
}