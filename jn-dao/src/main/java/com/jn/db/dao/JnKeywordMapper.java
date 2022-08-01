package com.jn.db.dao;

import com.jn.db.domain.JnKeyword;
import com.jn.db.domain.JnKeywordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnKeywordMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    long countByExample(JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int deleteByExample(JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int insert(JnKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int insertSelective(JnKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnKeyword selectOneByExample(JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnKeyword selectOneByExampleSelective(@Param("example") JnKeywordExample example, @Param("selective") JnKeyword.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnKeyword> selectByExampleSelective(@Param("example") JnKeywordExample example, @Param("selective") JnKeyword.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    List<JnKeyword> selectByExample(JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnKeyword selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnKeyword.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    JnKeyword selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnKeyword selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnKeyword record, @Param("example") JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnKeyword record, @Param("example") JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnKeyword record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnKeywordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_keyword
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}