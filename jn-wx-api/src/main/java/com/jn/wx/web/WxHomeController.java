package com.jn.wx.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import com.jn.db.domain.JnBooks;
import com.jn.db.domain.JnCompus;
import com.jn.db.domain.JnUser;
import com.jn.db.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jn.core.system.SystemConfig;
import com.jn.core.util.ResponseUtil;
import com.jn.db.domain.JnCategory;
import com.jn.wx.annotation.LoginUser;
import com.jn.wx.service.HomeCacheManager;

/**
 * 首页服务
 */
@RestController
@RequestMapping("/wx/home")
@Validated
public class WxHomeController {
    private static final Logger logger = LoggerFactory.getLogger(WxHomeController.class);


    @Autowired
    private JnAdService adService;

    @Autowired
    private JnBooksService booksService;


    @Autowired
    private JnTopicService topicService;

    @Autowired
    private JnCategoryService categoryService;

    @Autowired
    private JnUserService userService;


    @Autowired
    private JnArticleService articleService;

    @Autowired
    private JnCompusService compusService;

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    @SuppressWarnings("unused")
    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(9, 9, 1000, TimeUnit.MILLISECONDS,
            WORK_QUEUE, HANDLER);

    @GetMapping("/cache")
    public Object cache(@NotNull String key) {
        logger.info("请求开始=>缓存已清除,请求参数,key:{}", key);

        if (!key.equals("Jn_cache")) {
            logger.error("缓存已清除出错:非本平台标识！！！");
            return ResponseUtil.fail();
        }

        // 清除缓存
        HomeCacheManager.clearAll();

        logger.info("请求结束=>缓存已清除成功!");
        return ResponseUtil.ok("缓存已清除");
    }

    /**
     * 首页数据
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @SuppressWarnings("rawtypes")
    @GetMapping("/index")
    public Object index(@LoginUser Integer userId) {
        logger.info("请求开始=>访问首页,请求参数,userId:{}", userId);

        Map<String, Object> data = new HashMap<String, Object>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {

            // 优先从缓存中读取
            if (HomeCacheManager.hasData(HomeCacheManager.INDEX)) {
                data = HomeCacheManager.getCacheData(HomeCacheManager.INDEX);
                if (data != null) {// 加上这个判断，排除判断后到获取数据之间时间段清理的情况
                    LocalDateTime expire = (LocalDateTime) data.get("expireTime");
                    logger.info("访问首页,存在缓存数据，除用户优惠券信息外，加载缓存数据,有效期时间点：" + expire.toString());
                    return ResponseUtil.ok(data);
                }
            }

            Callable<List> bannerListCallable = () -> adService.queryIndex();

            Callable<List> articleListCallable = () -> articleService.queryList(0, 5, "add_time", "desc");

            Callable<List> channelListCallable = () -> categoryService.queryChannel();

            Callable<List> newBooksListCallable = () -> booksService.queryByNew(0, SystemConfig.getNewLimit());

            Callable<List> hotBooksListCallable = () -> booksService.queryByHot(0, SystemConfig.getHotLimit());

            Callable<List> topicListCallable = () -> topicService.queryList(0, SystemConfig.getTopicLimit());


            Callable<List> floorBooksListCallable = this::getCategoryList;

            FutureTask<List> bannerTask = new FutureTask<>(bannerListCallable);
            FutureTask<List> articleTask = new FutureTask<>(articleListCallable);
            FutureTask<List> channelTask = new FutureTask<>(channelListCallable);
            FutureTask<List> newBooksListTask = new FutureTask<>(newBooksListCallable);
            FutureTask<List> hotBooksListTask = new FutureTask<>(hotBooksListCallable);
            FutureTask<List> topicListTask = new FutureTask<>(topicListCallable);
            FutureTask<List> floorBooksListTask = new FutureTask<>(floorBooksListCallable);

            executorService.submit(bannerTask);
            executorService.submit(articleTask);
            executorService.submit(channelTask);
            executorService.submit(newBooksListTask);
            executorService.submit(hotBooksListTask);
            executorService.submit(topicListTask);
            executorService.submit(floorBooksListTask);

            data.put("banner", bannerTask.get());
            data.put("articles", articleTask.get());
            data.put("channel", channelTask.get());
            data.put("newbooksList", newBooksListTask.get());
            data.put("hotbooksList", hotBooksListTask.get());
            data.put("topicList", topicListTask.get());
            data.put("floorbooksList", floorBooksListTask.get());

            // 缓存数据首页缓存数据
            HomeCacheManager.loadData(HomeCacheManager.INDEX, data);
            executorService.shutdown();

        } catch (Exception e) {
            logger.error("首页信息获取失败：{}", e.getMessage());
            e.printStackTrace();
        }

        logger.info("请求结束=>访问首页成功!");//暂不打印首页信息
        return ResponseUtil.ok(data);
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/school")
    public Object school(@LoginUser Integer userId) {
        logger.info("请求开始=>访问首页,请求参数,userId:{}", userId);

        Map<String, Object> data = new HashMap<String, Object>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try {

            List<JnBooks> hotBooksListCallable;
            if(userId!=null){
                JnUser jnUser=userService.findById(userId);
                JnCompus jnCompus=compusService.findByCompusname(jnUser.getCompusname());
                hotBooksListCallable = booksService.queryByHotSchool(0, SystemConfig.getHotLimit(),jnCompus.getId());
            }
            else{
                hotBooksListCallable = booksService.queryByHot(0, SystemConfig.getHotLimit());
            }
            data.put("hotbooksList", hotBooksListCallable);


        } catch (Exception e) {
            logger.error("首页信息获取失败：{}", e.getMessage());
            e.printStackTrace();
        }

        logger.info("请求结束=>访问首页成功!");//暂不打印首页信息
        return ResponseUtil.ok(data);
    }

    @SuppressWarnings("rawtypes")
    private List<Map> getCategoryList() {
        List<Map> categoryList = new ArrayList<>();
        List<JnCategory> catL1List = categoryService.queryL1WithoutRecommend(0, SystemConfig.getCatlogListLimit());
        for (JnCategory catL1 : catL1List) {
            List<JnCategory> catL2List = categoryService.queryByPid(catL1.getId());
            List<Integer> l2List = new ArrayList<>();
            for (JnCategory catL2 : catL2List) {
                l2List.add(catL2.getId());
            }

            List<JnBooks> categoryBooks;
            if (l2List.size() == 0) {
                categoryBooks = new ArrayList<>();
            } else {
                categoryBooks = booksService.queryByCategory(l2List, 0, SystemConfig.getCatlogMoreLimit());
            }

            Map<String, Object> catBooks = new HashMap<>();
            catBooks.put("id", catL1.getId());
            catBooks.put("name", catL1.getName());
            catBooks.put("booksList", categoryBooks);
            categoryList.add(catBooks);
        }
        return categoryList;
    }
}
