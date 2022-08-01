package com.jn.db.service;

import com.github.pagehelper.PageHelper;
import com.jn.db.dao.JnBooksMapper;
import com.jn.db.domain.JnBooks;
import com.jn.db.domain.JnBooksExample;
import com.jn.db.domain.JnBooks.Column;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JnBooksService {
    Column[] columns = new Column[]{Column.id, Column.name, Column.brief, Column.picUrl, Column.isHot, Column.isNew,
            Column.retailPrice};
    @Resource
    private JnBooksMapper booksMapper;

    /**
     * 获取热卖书籍
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByHot(int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("browse desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }


    /**
     * 获取热卖书籍
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByHotSchool(int offset, int limit, Integer compusId) {
        JnBooksExample example = new JnBooksExample();
        example.or().andCompuIdEqualTo(compusId).andIsHotEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("browse desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取新品上市
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByNew(int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andIsNewEqualTo(true).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    public JnBooks queryById(Integer id) {
        JnBooksExample example = new JnBooksExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");

        return booksMapper.selectOneByExample(example);
    }

    /**
     * 获取我发布的图书
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByMe(Integer userId, int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andUserIdEqualTo(userId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的书籍
     *
     * @param catList
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByCategory(List<Integer> catList, int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andCategoryIdIn(catList).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("sort_order  asc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    /**
     * 获取分类下的书籍
     *
     * @param catId
     * @param offset
     * @param limit
     * @return
     */
    public List<JnBooks> queryByCategory(Integer catId, int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andCategoryIdEqualTo(catId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    public List<JnBooks> querySelective(Integer catId,  String keywords, Boolean isHot, Boolean isNew,
                                        Integer offset, Integer limit, String sort, String order) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria1 = example.or();
        JnBooksExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(catId) && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId);
            criteria2.andCategoryIdEqualTo(catId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

    public List<JnBooks> querySelectiveSchool(Integer catId, String keywords, Boolean isHot, Boolean isNew,
                                              Integer offset, Integer limit, String sort, String order) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria1 = example.or();
        JnBooksExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(catId) && catId != 0) {
            criteria1.andCategoryIdEqualTo(catId);
            criteria2.andCategoryIdEqualTo(catId);
        }
        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExample(example);
    }

    public List<JnBooks> querySelective(String booksSn, String name, Integer page, Integer size, String sort,
                                        String order) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(booksSn)) {
            criteria.andBooksSnEqualTo(booksSn);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return booksMapper.selectByExampleWithBLOBs(example);
    }

    public List<JnBooks> querySelectiveU(String booksSn, String name, Integer page, Integer size, String sort,
                                        String order,Integer userId) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(booksSn)) {
            criteria.andBooksSnEqualTo(booksSn);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andUserIdEqualTo(userId);
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return booksMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 获取某个书籍信息,包含完整信息
     *
     * @param id
     * @return
     */
    public JnBooks findById(Integer id) {
        JnBooksExample example = new JnBooksExample();
        example.or().andIdEqualTo(id).andDeletedEqualTo(false);
        return booksMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 根据序列码找到书籍
     *
     * @param booksSn
     * @return
     */
    public JnBooks findByBooksSn(String booksSn) {
        JnBooksExample example = new JnBooksExample();
        example.or().andBooksSnEqualTo(booksSn).andDeletedEqualTo(false);
        return booksMapper.selectOneByExampleWithBLOBs(example);
    }

    /**
     * 获取某个书籍信息，仅展示相关内容
     *
     * @param id
     * @return
     */
    public JnBooks findByIdVO(Integer id) {
        JnBooksExample example = new JnBooksExample();
        example.or().andIdEqualTo(id).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return booksMapper.selectOneByExampleSelective(example, columns);
    }

    public JnBooks findBySnVO(String sn) {
        JnBooksExample example = new JnBooksExample();
        example.or().andBooksSnEqualTo(sn).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return booksMapper.selectOneByExampleSelective(example, columns);
    }

    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    public Integer queryOnSale() {
        JnBooksExample example = new JnBooksExample();
        example.or().andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int) booksMapper.countByExample(example);
    }

    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    public Integer queryOnSaleSchool(Integer compusId) {
        JnBooksExample example = new JnBooksExample();
        example.or().andCompuIdEqualTo(compusId).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return (int) booksMapper.countByExample(example);
    }

    public int updateById(JnBooks books) {
        books.setUpdateTime(LocalDateTime.now());
        return booksMapper.updateByPrimaryKeySelective(books);
    }

    public void deleteById(Integer id) {
        booksMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(JnBooks books) {
        books.setAddTime(LocalDateTime.now());
        books.setUpdateTime(LocalDateTime.now());
        booksMapper.insertSelective(books);
    }

    /**
     * 获取所有物品总数，包括在售的和下架的，但是不包括已删除的书籍
     *
     * @return
     */
    public int count() {
        JnBooksExample example = new JnBooksExample();
        example.or().andDeletedEqualTo(false);
        return (int) booksMapper.countByExample(example);
    }

    public List<Integer> getCatIds( String keywords, Boolean isHot, Boolean isNew) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria1 = example.or();
        JnBooksExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<JnBooks> booksList = booksMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();
        for (JnBooks books : booksList) {
            cats.add(books.getCategoryId());
        }
        return cats;
    }

    public List<Integer> getCatIdsSchool( String keywords, Boolean isHot, Boolean isNew,Integer compusId) {
        JnBooksExample example = new JnBooksExample();
        JnBooksExample.Criteria criteria1 = example.or();
        JnBooksExample.Criteria criteria2 = example.or();

        if (!StringUtils.isEmpty(isNew)) {
            criteria1.andIsNewEqualTo(isNew);
            criteria2.andIsNewEqualTo(isNew);
        }
        if (!StringUtils.isEmpty(isHot)) {
            criteria1.andIsHotEqualTo(isHot);
            criteria2.andIsHotEqualTo(isHot);
        }
        if (!StringUtils.isEmpty(keywords)) {
            criteria1.andKeywordsLike("%" + keywords + "%");
            criteria2.andNameLike("%" + keywords + "%");
        }
        criteria1.andIsOnSaleEqualTo(true);
        criteria2.andIsOnSaleEqualTo(true);
        criteria1.andDeletedEqualTo(false);
        criteria2.andDeletedEqualTo(false);

        List<JnBooks> booksList = booksMapper.selectByExampleSelective(example, Column.categoryId);
        List<Integer> cats = new ArrayList<Integer>();

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
        for (JnBooks books : booksList) {
            cats.add(books.getCategoryId());
        }
        return cats;
    }

    public boolean checkExistByName(String name) {
        JnBooksExample example = new JnBooksExample();
        example.or().andNameEqualTo(name).andIsOnSaleEqualTo(true).andDeletedEqualTo(false);
        return booksMapper.countByExample(example) != 0;
    }


    /**
     * 同类书籍，且不同店铺
     *
     * @param cid
     * @param i
     * @param limitCid
     * @return
     */
    public List<JnBooks> queryByCategoryAndNotSameBrandId(int cid, int offset, int limit) {
        JnBooksExample example = new JnBooksExample();
        example.or().andCategoryIdEqualTo(cid).andIsOnSaleEqualTo(true)
                .andDeletedEqualTo(false);
        example.setOrderByClause("browse desc");
        PageHelper.startPage(offset, limit);

        return booksMapper.selectByExampleSelective(example, columns);
    }

}
