package com.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.admin.entity.BpmForm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 表单Mapper
 */
public interface BpmFormMapper extends BaseMapper<BpmForm> {

    /**
     * 根据表单标识查询表单
     */
    @Select("SELECT * FROM bpm_form WHERE form_key = #{formKey} AND deleted = 0")
    BpmForm selectByFormKey(@Param("formKey") String formKey);

    /**
     * 根据分类查询表单列表
     */
    @Select("SELECT * FROM bpm_form WHERE category = #{category} AND deleted = 0 AND status = 1")
    List<BpmForm> selectByCategory(@Param("category") String category);

}
