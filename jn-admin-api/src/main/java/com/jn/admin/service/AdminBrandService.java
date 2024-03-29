package com.jn.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jn.admin.util.CatVo;
import com.jn.core.util.ResponseUtil;
import com.jn.db.domain.JnAdmin;
import com.jn.db.domain.JnCategory;
import com.jn.db.service.JnAdminService;
import com.jn.db.service.JnCategoryService;


/*
* 品牌管理服务
* */

@Service
public class AdminBrandService {
	private static final Logger logger = LoggerFactory.getLogger(AdminBrandService.class);

	@Autowired
	private JnCategoryService categoryService;
	
	@Autowired
	private JnAdminService adminService;

	/**
	 * 获取分类和管理用户
	 * @return
	 */
	public Object catAndAdmin() {
		List<JnCategory> l1CatList = categoryService.queryL1();
		List<CatVo> categoryList = new ArrayList<CatVo>(l1CatList == null ? 0 : l1CatList.size());

		for (JnCategory l1 : l1CatList) {
			CatVo l1CatVo = new CatVo();
			l1CatVo.setValue(l1.getId());
			l1CatVo.setLabel(l1.getName());

			List<JnCategory> l2CatList = categoryService.queryByPid(l1.getId());
			List<CatVo> children = new ArrayList<CatVo>(l2CatList == null ? 0 : l2CatList.size());
			for (JnCategory l2 : l2CatList) {
				CatVo l2CatVo = new CatVo();
				l2CatVo.setValue(l2.getId());
				l2CatVo.setLabel(l2.getName());
				children.add(l2CatVo);
			}
			l1CatVo.setChildren(children);

			categoryList.add(l1CatVo);
		}
		
		//系统用户
		List<JnAdmin> list = adminService.allAdmin();
		List<Map<String, Object>> adminList = new ArrayList<Map<String, Object>>(list == null ? 0 : list.size());
		for (JnAdmin admin : list) {
			Map<String, Object> b = new HashMap<>(2);
			b.put("value", admin.getId());
			b.put("label", admin.getUsername());
			adminList.add(b);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("categoryList", categoryList);
		data.put("adminList", adminList);
		logger.info("品牌入驻获取的总一级目录数：{},总会员数：{}",categoryList.size(),adminList.size());
		return ResponseUtil.ok(data);
	}

}
