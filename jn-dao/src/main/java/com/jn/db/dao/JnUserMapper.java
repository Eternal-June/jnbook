package com.jn.db.dao;

import com.jn.db.domain.JnUser;
import com.jn.db.domain.JnUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    long countByExample(JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int deleteByExample(JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int insert(JnUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int insertSelective(JnUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnUser selectOneByExample(JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnUser selectOneByExampleSelective(@Param("example") JnUserExample example, @Param("selective") JnUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnUser> selectByExampleSelective(@Param("example") JnUserExample example, @Param("selective") JnUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    List<JnUser> selectByExample(JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnUser selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    JnUser selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnUser selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnUser record, @Param("example") JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnUser record, @Param("example") JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}