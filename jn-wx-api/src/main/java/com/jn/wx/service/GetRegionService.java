package com.jn.wx.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jn.db.domain.JnRegion;
import com.jn.db.service.JnRegionService;

/**
 * @author qiguliuxing
 * @since 1.0.0
 * @date 2017-04-11 11:07
 **/
@Component
public class GetRegionService {

	@Autowired
	private JnRegionService regionService;

	private static List<JnRegion> JnRegions;

	protected List<JnRegion> getJnRegions() {
		if (JnRegions == null) {
			createRegion();
		}
		return JnRegions;
	}

	private synchronized void createRegion() {
		if (JnRegions == null) {
			JnRegions = regionService.getAll();
		}
	}
}
