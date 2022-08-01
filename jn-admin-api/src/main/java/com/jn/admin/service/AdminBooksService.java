package com.jn.admin.service;

import static com.jn.admin.util.AdminResponseCode.BOOKS_NAME_EXIST;
import static com.jn.admin.util.AdminResponseCode.BOOKS_UPDATE_NOT_ALLOWED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jn.db.domain.*;
import com.jn.db.service.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jn.admin.dao.BooksAllinone;
import com.jn.admin.util.AdminResponseUtil;
import com.jn.admin.util.CatVo;
import com.jn.core.util.ResponseUtil;


/*
 * 书籍管理服务
 * */
@Service
public class AdminBooksService {
    private static final Logger logger = LoggerFactory.getLogger(AdminBooksService.class);

    @Autowired
    private JnBooksService booksService;
    @Autowired
    private JnBooksSpecificationService specificationService;
    @Autowired
    private JnBooksAttributeService attributeService;
    @Autowired
    private JnBooksProductService productService;
    @Autowired
    private JnCategoryService categoryService;
    @Autowired
    private JnCartService cartService;
    @Autowired
    private JnOrderBooksService orderBooksService;
    @Autowired
    private JnUserService userService;
    @Autowired
    private JnCompusService compusService;


    public Object list(String booksSn, String name, Integer page, Integer limit, String sort, String order) {
        List<JnBooks> booksList = booksService.querySelective(booksSn, name, page, limit, sort, order);
        long total = PageInfo.of(booksList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", booksList);

        logger.info("请求结束=>书籍管理->书籍管理->查询,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    public Object listU(String booksSn, String name, Integer page, Integer limit, String sort, String order,Integer userId) {
        List<JnBooks> booksList = booksService.querySelectiveU(booksSn, name, page, limit, sort, order,userId);
        long total = PageInfo.of(booksList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", booksList);

        logger.info("请求结束=>书籍管理->书籍管理->查询,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    private Object validate(BooksAllinone booksAllinone) {
        JnBooks books = booksAllinone.getBooks();
        String name = books.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        String booksSn = books.getBooksSn();
        if (StringUtils.isEmpty(booksSn)) {
            return ResponseUtil.badArgument();
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = books.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.findById(categoryId) == null) {
                return ResponseUtil.badArgumentValue();
            }
        }

        JnBooksAttribute[] attributes = booksAllinone.getAttributes();
        for (JnBooksAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                return ResponseUtil.badArgument();
            }
            String value = attribute.getValue();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        JnBooksSpecification[] specifications = booksAllinone.getSpecifications();
        for (JnBooksSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StringUtils.isEmpty(spec)) {
                return ResponseUtil.badArgument();
            }
            String value = specification.getValue();
            if (StringUtils.isEmpty(value)) {
                return ResponseUtil.badArgument();
            }
        }

        JnBooksProduct[] products = booksAllinone.getProducts();
        for (JnBooksProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                return ResponseUtil.badArgument();
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                return ResponseUtil.badArgument();
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return ResponseUtil.badArgument();
            }
        }

        return null;
    }

    /**
     * 编辑书籍
     * <p>
     * TODO 目前书籍修改的逻辑是 1. 更新Jn_books表 2.
     * 逻辑删除Jn_books_specification、Jn_books_attribute、Jn_books_product 3.
     * 添加Jn_books_specification、Jn_books_attribute、Jn_books_product
     * <p>
     * 这里书籍三个表的数据采用删除再添加的策略是因为 书籍编辑页面，支持管理员添加删除书籍规格、添加删除书籍属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。 但是这里又会引入新的问题，就是存在订单书籍货品ID指向了失效的书籍货品表。
     * 因此这里会拒绝管理员编辑书籍，如果订单或购物车中存在书籍。 所以这里可能需要重新设计。
     */
    @Transactional
    public Object update(BooksAllinone booksAllinone) {
        Object error = validate(booksAllinone);
        if (error != null) {
            return error;
        }
        JnBooks books = booksAllinone.getBooks();
        JnBooksAttribute[] attributes = booksAllinone.getAttributes();
        JnBooksSpecification[] specifications = booksAllinone.getSpecifications();
        JnBooksProduct[] products = booksAllinone.getProducts();

        Integer id = books.getId();
        // 检查是否存在购物车书籍或者订单书籍
        // 如果存在则拒绝修改书籍。
        if (orderBooksService.checkExist(id) || cartService.checkExist(id)) {
            logger.error("书籍管理->书籍管理->编辑错误:{}", BOOKS_UPDATE_NOT_ALLOWED.desc());
            return AdminResponseUtil.fail(BOOKS_UPDATE_NOT_ALLOWED);
        }

        // 书籍基本信息表Jn_books
        if (booksService.updateById(books) == 0) {
            logger.error("书籍管理->书籍管理->编辑错误:{}", "更新数据失败");
            throw new RuntimeException("更新数据失败");
        }

        Integer gid = books.getId();
        specificationService.deleteByGid(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);

        // 书籍规格表Jn_books_specification
        for (JnBooksSpecification specification : specifications) {
            specification.setBooksId(books.getId());
            specificationService.add(specification);
        }

        // 书籍参数表Jn_books_attribute
        for (JnBooksAttribute attribute : attributes) {
            attribute.setBooksId(books.getId());
            attributeService.add(attribute);
        }

        // 书籍货品表Jn_product
        for (JnBooksProduct product : products) {
            product.setBooksId(books.getId());
            productService.add(product);
        }
        //qCodeService.createGoodShareImage(books.getId().toString(), books.getPicUrl(), books.getName());

        logger.info("请求结束=>书籍管理->书籍管理->编辑,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    @Transactional
    public Object delete(JnBooks books) {
        Integer id = books.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        Integer gid = books.getId();
        booksService.deleteById(gid);
        specificationService.deleteByGid(gid);
        attributeService.deleteByGid(gid);
        productService.deleteByGid(gid);

        logger.info("请求结束=>书籍管理->书籍管理->删除,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    @Transactional
    public Object create(BooksAllinone booksAllinone) {
        Object error = validate(booksAllinone);
        if (error != null) {
            return error;
        }

        JnBooks books = booksAllinone.getBooks();
        JnBooksAttribute[] attributes = booksAllinone.getAttributes();
        JnBooksSpecification[] specifications = booksAllinone.getSpecifications();
        JnBooksProduct[] products = booksAllinone.getProducts();

        String name = books.getName();
        if (booksService.checkExistByName(name)) {
            logger.error("书籍管理->书籍管理->上架错误:{}", BOOKS_NAME_EXIST.desc());
            return AdminResponseUtil.fail(BOOKS_NAME_EXIST);
        }

        Subject currentUser = SecurityUtils.getSubject();
        JnAdmin admin = (JnAdmin) currentUser.getPrincipal();
        if(admin.getUserId()!=null){
            JnUser user=userService.findById(admin.getUserId());
            JnCompus compus=compusService.findByCompusname(user.getCompusname());
            books.setUserId(admin.getUserId());
            books.setCompuId(compus.getId());
        }else{
            books.setCompuId(0);
            books.setUserId(0);
        }
        // 书籍基本信息表Jn_books
        booksService.add(books);


        // 书籍规格表Jn_books_specification
        for (JnBooksSpecification specification : specifications) {
            specification.setBooksId(books.getId());
            specificationService.add(specification);
        }

        // 书籍参数表Jn_books_attribute
        for (JnBooksAttribute attribute : attributes) {
            attribute.setBooksId(books.getId());
            attributeService.add(attribute);
        }

        // 书籍货品表Jn_product
        for (JnBooksProduct product : products) {
            product.setBooksId(books.getId());
            productService.add(product);
        }

        logger.info("请求结束=>书籍管理->书籍管理->上架,响应结果:{}", "成功!");
        return ResponseUtil.ok();
    }

    public Object catAndBrand() {
        List<JnCategory> l1CatList = categoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (JnCategory l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<JnCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (JnCategory l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        return ResponseUtil.ok(data);
    }

    public Object detail(Integer id) {
        JnBooks books = booksService.findById(id);
        List<JnBooksProduct> products = productService.queryByGid(id);
        List<JnBooksSpecification> specifications = specificationService.queryByGid(id);
        List<JnBooksAttribute> attributes = attributeService.queryByGid(id);

        //用于展示书籍归属的类目（页面级联下拉控件数据展示）
        Integer categoryId = books.getCategoryId();
        JnCategory category = categoryService.findById(categoryId);
        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("books", books);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        logger.info("请求结束=>书籍管理->书籍管理->详情,响应结果:{}", "成功!");
        return ResponseUtil.ok(data);
    }

}
