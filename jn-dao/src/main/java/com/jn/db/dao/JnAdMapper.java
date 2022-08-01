package com.jn.db.dao;

import com.jn.db.domain.JnAd;
import com.jn.db.domain.JnAdExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnAdMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    long countByExample(JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int deleteByExample(JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int insert(JnAd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int insertSelective(JnAd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAd selectOneByExample(JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAd selectOneByExampleSelective(@Param("example") JnAdExample example, @Param("selective") JnAd.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnAd> selectByExampleSelective(@Param("example") JnAdExample example, @Param("selective") JnAd.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    List<JnAd> selectByExample(JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAd selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnAd.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    JnAd selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAd selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnAd record, @Param("example") JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnAd record, @Param("example") JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnAd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnAd record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnAdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_ad
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}