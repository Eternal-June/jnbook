package com.jn.db.dao;

import com.jn.db.domain.JnAccountTrace;
import com.jn.db.domain.JnAccountTraceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnAccountTraceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    long countByExample(JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int deleteByExample(JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int insert(JnAccountTrace record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int insertSelective(JnAccountTrace record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAccountTrace selectOneByExample(JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAccountTrace selectOneByExampleSelective(@Param("example") JnAccountTraceExample example, @Param("selective") JnAccountTrace.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnAccountTrace> selectByExampleSelective(@Param("example") JnAccountTraceExample example, @Param("selective") JnAccountTrace.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    List<JnAccountTrace> selectByExample(JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAccountTrace selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnAccountTrace.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    JnAccountTrace selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnAccountTrace record, @Param("example") JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnAccountTrace record, @Param("example") JnAccountTraceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnAccountTrace record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_account_trace
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnAccountTrace record);
}