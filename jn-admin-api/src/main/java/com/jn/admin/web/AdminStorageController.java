package com.jn.admin.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jn.admin.annotation.RequiresPermissionsDesc;
import com.jn.core.storage.StorageService;
import com.jn.core.util.ResponseUtil;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;
import com.jn.db.domain.JnStorage;
import com.jn.db.service.JnStorageService;

/*
* 对象存储管理
* */
@RestController
@RequestMapping("/admin/storage")
@Validated
public class AdminStorageController {
	private static final Logger logger = LoggerFactory.getLogger(AdminStorageController.class);

	@Autowired
	private StorageService storageService;
	@Autowired
	private JnStorageService JnStorageService;

	@RequiresPermissions("admin:storage:list")
	@RequiresPermissionsDesc(menu = { "系统管理", "对象存储" }, button = "查询")
	@GetMapping("/list")
	public Object list(String key, String name, @RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "10") Integer limit,
			@Sort @RequestParam(defaultValue = "add_time") String sort,
			@Order @RequestParam(defaultValue = "desc") String order) {
		logger.info("请求开始=>系统管理->对象存储->查询,请求参数,name:{},key:{},page:{}", name, key, page);

		List<JnStorage> storageList = JnStorageService.querySelective(key, name, page, limit, sort, order);
		long total = PageInfo.of(storageList).getTotal();
		Map<String, Object> data = new HashMap<>();
		data.put("total", total);
		data.put("items", storageList);

		logger.info("请求结束=>系统管理->对象存储->查询:响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	@RequiresPermissions("admin:storage:create")
	@RequiresPermissionsDesc(menu = { "系统管理", "对象存储" }, button = "上传")
	@PostMapping("/create")
	public Object create(@RequestParam("file") MultipartFile file) throws IOException {
		logger.info("请求开始=>系统管理->对象存储->上传,请求参数,file:{}", file.getOriginalFilename());

		String originalFilename = file.getOriginalFilename();
		String url = storageService.store(file.getInputStream(), file.getSize(), file.getContentType(),
				originalFilename);
		Map<String, Object> data = new HashMap<>();
		data.put("url", url);

		logger.info("请求结束=>系统管理->对象存储->查询:响应结果:{}", JSONObject.toJSONString(data));
		return ResponseUtil.ok(data);
	}

	@RequiresPermissions("admin:storage:read")
	@RequiresPermissionsDesc(menu = { "系统管理", "对象存储" }, button = "详情")
	@PostMapping("/read")
	public Object read(@NotNull Integer id) {
		logger.info("请求开始=>系统管理->对象存储->详情,请求参数,id:{}", id);

		JnStorage storageInfo = JnStorageService.findById(id);
		if (storageInfo == null) {
			return ResponseUtil.badArgumentValue();
		}

		logger.info("请求结束=>系统管理->对象存储->详情:响应结果:{}", JSONObject.toJSONString(storageInfo));
		return ResponseUtil.ok(storageInfo);
	}

	@RequiresPermissions("admin:storage:update")
	@RequiresPermissionsDesc(menu = { "系统管理", "对象存储" }, button = "编辑")
	@PostMapping("/update")
	public Object update(@RequestBody JnStorage jnStorage) {
		logger.info("请求开始=>系统管理->对象存储->编辑,请求参数:{}", JSONObject.toJSONString(jnStorage));

		if (JnStorageService.update(jnStorage) == 0) {
			logger.error("系统管理->对象存储->编辑 错误:{}", "更新数据失败!");
			return ResponseUtil.updatedDataFailed();
		}

		logger.info("请求结束=>系统管理->对象存储->编辑:响应结果:{}", JSONObject.toJSONString(jnStorage));
		return ResponseUtil.ok(jnStorage);
	}

	@RequiresPermissions("admin:storage:delete")
	@RequiresPermissionsDesc(menu = { "系统管理", "对象存储" }, button = "删除")
	@PostMapping("/delete")
	public Object delete(@RequestBody JnStorage JnStorage) {
		logger.info("请求开始=>系统管理->对象存储->删除,请求参数:{}", JSONObject.toJSONString(JnStorage));

		String key = JnStorage.getKey();
		if (StringUtils.isEmpty(key)) {
			return ResponseUtil.badArgument();
		}
		JnStorageService.deleteByKey(key);
		storageService.delete(key);

		logger.info("请求结束=>系统管理->对象存储->删除:响应结果:{}", "成功!");
		return ResponseUtil.ok();
	}
}
