package com.jn.db.dao;

import com.jn.db.domain.JnIssue;
import com.jn.db.domain.JnIssueExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnIssueMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    long countByExample(JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int deleteByExample(JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int insert(JnIssue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int insertSelective(JnIssue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnIssue selectOneByExample(JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnIssue selectOneByExampleSelective(@Param("example") JnIssueExample example, @Param("selective") JnIssue.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnIssue> selectByExampleSelective(@Param("example") JnIssueExample example, @Param("selective") JnIssue.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    List<JnIssue> selectByExample(JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnIssue selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnIssue.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    JnIssue selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnIssue selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnIssue record, @Param("example") JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnIssue record, @Param("example") JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnIssue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnIssue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnIssueExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_issue
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}