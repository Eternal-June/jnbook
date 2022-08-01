package com.jn.db.dao;

import com.jn.db.domain.JnCompus;
import com.jn.db.domain.JnCompusExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnCompusMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    long countByExample(JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int deleteByExample(JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int insert(JnCompus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int insertSelective(JnCompus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnCompus selectOneByExample(JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnCompus selectOneByExampleSelective(@Param("example") JnCompusExample example, @Param("selective") JnCompus.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnCompus> selectByExampleSelective(@Param("example") JnCompusExample example, @Param("selective") JnCompus.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    List<JnCompus> selectByExample(JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnCompus selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnCompus.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    JnCompus selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnCompus selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnCompus record, @Param("example") JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnCompus record, @Param("example") JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnCompus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnCompus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnCompusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_compus
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}