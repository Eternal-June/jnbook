package com.jn.db.dao;

import com.jn.db.domain.JnAddress;
import com.jn.db.domain.JnAddressExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JnAddressMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    long countByExample(JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int deleteByExample(JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int insert(JnAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int insertSelective(JnAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAddress selectOneByExample(JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAddress selectOneByExampleSelective(@Param("example") JnAddressExample example, @Param("selective") JnAddress.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<JnAddress> selectByExampleSelective(@Param("example") JnAddressExample example, @Param("selective") JnAddress.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    List<JnAddress> selectByExample(JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAddress selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") JnAddress.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    JnAddress selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    JnAddress selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") JnAddress record, @Param("example") JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") JnAddress record, @Param("example") JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(JnAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(JnAddress record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") JnAddressExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table jn_address
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);
}