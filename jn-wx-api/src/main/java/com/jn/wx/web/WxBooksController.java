package com.jn.wx.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import javax.validation.constraints.NotNull;

import com.jn.core.system.SystemConfig;
import com.jn.db.domain.*;
import com.jn.db.service.*;
import com.jn.wx.service.HomeCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;
import com.jn.core.util.ResponseUtil;
import com.jn.core.validator.Order;
import com.jn.core.validator.Sort;
import com.jn.wx.annotation.LoginUser;

/**
 * 书籍服务
 */
@RestController
@RequestMapping("/wx/books")
@Validated
public class WxBooksController {
    private static final Logger logger = LoggerFactory.getLogger(WxBooksController.class);

    @Autowired
    private JnBooksService booksService;

    @Autowired
    private JnBooksProductService productService;

    @Autowired
    private JnIssueService booksIssueService;

    @Autowired
    private JnBooksAttributeService booksAttributeService;


    @Autowired
    private JnCommentService commentService;

    @Autowired
    private JnUserService userService;

    @Autowired
    private JnCollectService collectService;

    @Autowired
    private JnFootprintService footprintService;

    @Autowired
    private JnCategoryService categoryService;

    @Autowired
    private JnCompusService compusService;

    @Autowired
    private JnSearchHistoryService searchHistoryService;

    @Autowired
    private JnBooksSpecificationService booksSpecificationService;


    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS,
            WORK_QUEUE, HANDLER);

    /**
     * 书籍详情
     * <p>
     * 用户可以不登录。 如果用户登录，则记录用户足迹以及返回用户收藏信息。
     *
     * @param userId 用户ID
     * @param id     书籍ID
     * @return 书籍详情
     */
    @SuppressWarnings("rawtypes")
    @GetMapping("detail")
    public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
        logger.info("请求开始=>书籍详情,请求参数,userId:{},id:{}", userId, id);

        // 书籍信息
        JnBooks info = booksService.findById(id);

        info.setEmail(userService.findById(info.getUserId()).getEmail());

        // 书籍属性
        Callable<List> booksAttributeListCallable = () -> booksAttributeService.queryByGid(id);

        // 书籍规格 返回的是定制的BooksSpecificationVo
        Callable<Object> objectCallable = () -> booksSpecificationService.getSpecificationVoList(id);

        // 书籍规格对应的数量和价格
        Callable<List> productListCallable = () -> productService.queryByGid(id);

        // 书籍问题，这里是一些通用问题
        Callable<List> issueCallable = () -> booksIssueService.query();


        // 评论
        Callable<Map> commentsCallable = () -> {
            List<JnComment> comments = commentService.queryBooksByGid(id, 0, 2);
            List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
            long commentCount = PageInfo.of(comments).getTotal();
            for (JnComment comment : comments) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", comment.getId());
                c.put("addTime", comment.getAddTime());
                c.put("content", comment.getContent());
                JnUser user = userService.findById(comment.getUserId());
                c.put("nickname", user.getNickname());
                c.put("avatar", user.getAvatar());
                c.put("picList", comment.getPicUrls());
                commentsVo.add(c);
            }
            Map<String, Object> commentList = new HashMap<>();
            commentList.put("count", commentCount);
            commentList.put("data", commentsVo);
            return commentList;
        };


        // 用户收藏
        int userHasCollect = 0;
        if (userId != null) {
            userHasCollect = collectService.count(userId, id);
        }

        // 记录用户的足迹 异步处理
        if (userId != null) {
            executorService.execute(() -> {
                JnFootprint footprint = new JnFootprint();
                footprint.setUserId(userId);
                footprint.setBooksId(id);

                footprintService.add(footprint);
                short num = 1;
                productService.addBrowse(id, num);// 新增书籍点击量
            });
        }

        FutureTask<List> booksAttributeListTask = new FutureTask<>(booksAttributeListCallable);
        FutureTask<Object> objectCallableTask = new FutureTask<>(objectCallable);
        FutureTask<List> productListCallableTask = new FutureTask<>(productListCallable);
        FutureTask<List> issueCallableTask = new FutureTask<>(issueCallable);
        FutureTask<Map> commentsCallableTsk = new FutureTask<>(commentsCallable);

        executorService.submit(booksAttributeListTask);
        executorService.submit(objectCallableTask);
        executorService.submit(productListCallableTask);
        executorService.submit(issueCallableTask);
        executorService.submit(commentsCallableTsk);

        Map<String, Object> data = new HashMap<>();

        try {
            data.put("info", info);
            data.put("userHasCollect", userHasCollect);
            data.put("issue", issueCallableTask.get());
            data.put("comment", commentsCallableTsk.get());
            data.put("specificationList", objectCallableTask.get());
            data.put("productList", productListCallableTask.get());
            data.put("attribute", booksAttributeListTask.get());
        } catch (Exception e) {
            logger.error("获取书籍详情出错:{}", e.getMessage());
            e.printStackTrace();
        }


        logger.info("请求结束=>获取书籍详情成功!");// 这里不打印返回的信息，因为此接口查询量大，太耗日志空间
        return ResponseUtil.ok(data);
    }

    /**
     * 书籍分类类目
     *
     * @param id 分类类目ID
     * @return 书籍分类类目
     */
    @GetMapping("category")
    public Object category(@NotNull Integer id) {
        logger.info("请求开始=>书籍分类类目,请求参数,id:{}", id);

        JnCategory cur = categoryService.findById(id);
        JnCategory parent = null;
        List<JnCategory> children = null;

        if (cur.getPid() == 0) {
            parent = cur;
            children = categoryService.queryByPid(cur.getId());
            cur = children.size() > 0 ? children.get(0) : cur;
        } else {
            parent = categoryService.findById(cur.getPid());
            children = categoryService.queryByPid(cur.getPid());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", cur);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);

        logger.info("请求结束=>书籍分类类目,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    /**
     * 根据条件搜素书籍
     * <p>
     * 1. 这里的前五个参数都是可选的，甚至都是空 2. 用户是可选登录，如果登录，则记录用户的搜索关键字
     *
     * @param categoryId 分类类目ID，可选
     * @param brandId    品牌商ID，可选
     * @param keyword    关键字，可选
     * @param isNew      是否新品，可选
     * @param isHot      是否热买，可选
     * @param userId     用户ID
     * @param page       分页页数
     * @param size       分页大小
     * @param sort       排序方式，支持"add_time", "retail_price"或"name",浏览量 "browse",销售量："sales"
     * @param order      排序类型，顺序或者降序
     * @return 根据条件搜素的书籍详情
     */
    @GetMapping("list")
    public Object list(Integer categoryId, String keyword, Boolean isNew, Boolean isHot,
                       @LoginUser Integer userId, @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @Sort(accepts = {"sort_order", "add_time", "retail_price", "browse", "name",
                               "sales"}) @RequestParam(defaultValue = "sort_order") String sort,
                       @Order @RequestParam(defaultValue = "asc") String order) {

        logger.info("请求开始=>根据条件搜素书籍,请求参数,categoryId:{},brandId:{},keyword:{}", categoryId, keyword);

        // 添加到搜索历史
        if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
            JnSearchHistory searchHistoryVo = new JnSearchHistory();
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(userId);
            searchHistoryVo.setFrom("wx");
            searchHistoryService.save(searchHistoryVo);
        }

        // 查询列表数据
        List<JnBooks> booksList = booksService.querySelective(categoryId, keyword, isHot, isNew, page, size,
                sort, order);

        // 查询书籍所属类目列表。
        List<Integer> booksCatIds = booksService.getCatIds(keyword, isHot, isNew);
        List<JnCategory> categoryList = null;
        if (booksCatIds.size() != 0) {
            categoryList = categoryService.queryL2ByIds(booksCatIds);
        } else {
            categoryList = new ArrayList<>(0);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("booksList", booksList);
        long count = PageInfo.of(booksList).getTotal();
        int totalPages = (int) Math.ceil((double) count / size);
        data.put("count", PageInfo.of(booksList).getTotal());
        data.put("filterCategoryList", categoryList);
        data.put("totalPages", totalPages);

        logger.info("请求结束=>根据条件搜素书籍,响应结果:查询的书籍数量:{},总数：{},总共 {} 页", booksList.size(), count, totalPages);
        return ResponseUtil.ok(data);
    }


    @GetMapping("listschool")
    public Object listschool(Integer categoryId, String keyword, Boolean isNew, Boolean isHot,
                             @LoginUser Integer userId, @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer size,
                             @Sort(accepts = {"sort_order", "add_time", "retail_price", "browse", "name",
                                     "sales"}) @RequestParam(defaultValue = "sort_order") String sort,
                             @Order @RequestParam(defaultValue = "asc") String order) {

        logger.info("请求开始=>根据条件搜素书籍,请求参数,categoryId:{},keyword:{}", categoryId, keyword);

        // 添加到搜索历史
        if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
            JnSearchHistory searchHistoryVo = new JnSearchHistory();
            searchHistoryVo.setKeyword(keyword);
            searchHistoryVo.setUserId(userId);
            searchHistoryVo.setFrom("wx");
            searchHistoryService.save(searchHistoryVo);
        }

        // 查询列表数据
        List<JnBooks> booksList = booksService.querySelectiveSchool(categoryId, keyword, isHot, isNew, page, size,
                sort, order);
        for (int i = 0; i < booksList.size(); i++) {
            logger.info(booksList.get(i).getCompuId().toString());
        }
        logger.info("dsfdsfdsf");
        if (userId != null) {
            JnUser jnUser = userService.findById(userId);
            JnCompus jnCompus = compusService.findByCompusname(jnUser.getCompusname());
            Integer compusId = jnCompus.getId();
            for (int i = 0; i < booksList.size(); i++) {
                if (booksList.get(i).getCompuId() != null) {
                    if (booksList.get(i).getCompuId() != (compusId)) {
                        booksList.remove(booksList.get(i));
                        i--;
                    }
                } else {
                    booksList.remove(booksList.get(i));
                    i--;
                }
            }
        }
        for (int i = 0; i < booksList.size(); i++) {
            logger.info(booksList.get(i).getCompuId().toString());
        }

        logger.info("joijojoj");
        // 查询书籍所属类目列表。
        List<Integer> booksCatIds;
        if (userId != null) {
            JnUser jnUser = userService.findById(userId);
            JnCompus jnCompus = compusService.findByCompusname(jnUser.getCompusname());
            Integer compusId = jnCompus.getId();
            booksCatIds = booksService.getCatIdsSchool(keyword, isHot, isNew, compusId);
        } else {
            booksCatIds = booksService.getCatIds(keyword, isHot, isNew);
        }

        List<JnCategory> categoryList = null;
        if (booksCatIds.size() != 0) {
            categoryList = categoryService.queryL2ByIds(booksCatIds);
        } else {
            categoryList = new ArrayList<>(0);
        }


        Map<String, Object> data = new HashMap<>();
        data.put("booksList", booksList);
        long count = PageInfo.of(booksList).getTotal();
        int totalPages = (int) Math.ceil((double) count / size);
        data.put("count", PageInfo.of(booksList).getTotal());
        data.put("filterCategoryList", categoryList);
        data.put("totalPages", totalPages);

        logger.info("请求结束=>根据条件搜素书籍,响应结果:查询的书籍数量:{},总数：{},总共 {} 页", booksList.size(), count, totalPages);
        return ResponseUtil.ok(data);
    }

    /**
     * 书籍详情页面“大家都在看”推荐书籍
     *
     * @param id, 书籍ID
     * @return 书籍详情页面推荐书籍
     */
    @GetMapping("related")
    public Object related(@NotNull Integer id) {
        logger.info("请求开始=>书籍详情页面“大家都在看”推荐书籍,请求参数,id:{}", id);

        JnBooks books = booksService.findById(id);
        if (books == null) {
            return ResponseUtil.badArgumentValue();
        }

        // 目前的书籍推荐算法仅仅是推荐同类目的其他书籍
        int cid = books.getCategoryId().intValue();

        // 查找六个相关书籍,同店铺，同类优先
        int limitBid = 10;
        List<JnBooks> booksListBrandId = booksService.queryByCategory(cid, 0, limitBid);
        List<JnBooks> relatedBooks = booksListBrandId == null ? new ArrayList<JnBooks>() : booksListBrandId;
        if (booksListBrandId == null || booksListBrandId.size() < 6) {// 同店铺，同类书籍小于6件，则获取其他店铺同类书籍
            int limitCid = 6;
            List<JnBooks> booksListCategory = booksService.queryByCategory(cid, 0, limitCid);
            relatedBooks.addAll(booksListCategory);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("booksList", relatedBooks);

        logger.info("请求结束=>书籍详情页面“大家都在看”推荐书籍,响应结果:查询的书籍数量:{}", relatedBooks.size());
        return ResponseUtil.ok(data);
    }

    /**
     * 在售的书籍总数
     *
     * @return 在售的书籍总数
     */
    @GetMapping("count")
    public Object count() {
        logger.info("请求开始=>在售的书籍总数...");
        Integer booksCount = booksService.queryOnSale();
        Map<String, Object> data = new HashMap<>();
        data.put("booksCount", booksCount);

        logger.info("请求结束=>在售的书籍总数,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }


    /**
     * 在售的书籍总数
     *
     * @return 在售的书籍总数
     */
    @GetMapping("schoolCount")
    public Object schoolCount(@LoginUser Integer userId) {
        logger.info("请求开始=>在售的书籍总数...{}", userId);
        Integer booksCount;
        if (userId != null) {
            JnUser jnUser = userService.findById(userId);
            JnCompus jnCompus = compusService.findByCompusname(jnUser.getCompusname());
            booksCount = booksService.queryOnSaleSchool(jnCompus.getId());
        } else {
            booksCount = booksService.queryOnSale();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("schoolBooksCount", booksCount);

        logger.info("请求结束=>在售的书籍总数,响应结果:{}", JSONObject.toJSONString(data));
        return ResponseUtil.ok(data);
    }

    /**
     * 卖书
     */
    //TODO
    @PostMapping("contact")
    public Object contact(@RequestBody String body) {
        logger.info("请求开始=>联系卖家, body:{}", body);

        logger.info("请求结束=>联系卖家,响应结果:{}");
        return ResponseUtil.ok();
    }
}
