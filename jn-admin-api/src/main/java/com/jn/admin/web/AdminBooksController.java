package com.jn.admin.web;

import javax.validation.constraints.NotNull;

import com.jn.db.domain.JnAdmin;
import com.jn.db.domain.JnBooks;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jn.admin.annotation.RequiresPermissionsDesc;
import com.jn.admin.dao.BooksAllinone;
import com.jn.admin.service.AdminBooksService;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;

/*
 *
 * 书籍管理
 * */
@RestController
@RequestMapping("/admin/books")
@Validated
public class AdminBooksController {
    private static final Logger logger = LoggerFactory.getLogger(AdminBooksController.class);

    @Autowired
    private AdminBooksService adminBooksService;

    /**
     * 查询书籍
     *
     * @param booksSn
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:books:list")
    @RequiresPermissionsDesc(menu = {"书籍管理", "书籍管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String booksSn, String name, @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        logger.info("请求开始=>书籍管理->书籍管理->查询,请求参数:booksSn:{},name:{},page:{}", booksSn, name, page);
        Subject currentUser = SecurityUtils.getSubject();
        JnAdmin admin = (JnAdmin) currentUser.getPrincipal();
        if(admin.getUserId()!=null){
            return adminBooksService.listU(booksSn, name, page, limit, sort, order, admin.getUserId());
        }else{
            return adminBooksService.list(booksSn, name, page, limit, sort, order);
        }

    }

    @GetMapping("/catAndBrand")
    public Object catAndBrand() {
        return adminBooksService.catAndBrand();
    }

    /**
     * 编辑书籍
     *
     * @param booksAllinone
     * @return
     */
    @RequiresPermissions("admin:books:update")
    @RequiresPermissionsDesc(menu = {"书籍管理", "书籍管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody BooksAllinone booksAllinone) {
        logger.info("请求开始=>书籍管理->书籍管理->编辑,请求参数:{}", JSONObject.toJSONString(booksAllinone));

        return adminBooksService.update(booksAllinone);
    }

    /**
     * 删除书籍
     *
     * @param books
     * @return
     */
    @RequiresPermissions("admin:books:delete")
    @RequiresPermissionsDesc(menu = {"书籍管理", "书籍管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody JnBooks books) {
        logger.info("请求开始=>书籍管理->书籍管理->删除,请求参数:{}", JSONObject.toJSONString(books));

        return adminBooksService.delete(books);
    }

    /**
     * 添加书籍
     *
     * @param booksAllinone
     * @return
     */
    @RequiresPermissions("admin:books:create")
    @RequiresPermissionsDesc(menu = {"书籍管理", "书籍管理"}, button = "上架")
    @PostMapping("/create")
    public Object create(@RequestBody BooksAllinone booksAllinone) {
        logger.info("请求开始=>书籍管理->书籍管理->上架,请求参数:{}", JSONObject.toJSONString(booksAllinone));

        return adminBooksService.create(booksAllinone);
    }

    /**
     * 书籍详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:books:read")
    @RequiresPermissionsDesc(menu = {"书籍管理", "书籍管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        logger.info("请求开始=>书籍管理->书籍管理->详情,请求参数,id:{}", id);

        return adminBooksService.detail(id);
    }

}
