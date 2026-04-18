package com.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.admin.entity.BpmForm;
import com.flow.admin.entity.BpmModelForm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 表单与流程模型关联Mapper
 */
public interface BpmModelFormMapper extends BaseMapper<BpmModelForm> {

    /**
     * 根据模型ID查询关联的表单
     */
    @Select("SELECT f.* FROM bpm_form f " +
            "INNER JOIN bpm_model_form mf ON f.id = mf.form_id " +
            "WHERE mf.model_id = #{modelId} AND f.deleted = 0 AND f.status = 1")
    List<BpmForm> selectFormsByModelId(@Param("modelId") String modelId);

    /**
     * 根据模型ID和节点标识查询表单
     */
    @Select("SELECT f.* FROM bpm_form f " +
            "INNER JOIN bpm_model_form mf ON f.id = mf.form_id " +
            "WHERE mf.model_id = #{modelId} AND mf.node_key = #{nodeKey} " +
            "AND f.deleted = 0 AND f.status = 1")
    BpmForm selectFormByModelIdAndNodeKey(@Param("modelId") String modelId,
                                          @Param("nodeKey") String nodeKey);

    /**
     * 删除模型与表单的关联
     */
    @Select("DELETE FROM bpm_model_form WHERE model_id = #{modelId}")
    void deleteByModelId(@Param("modelId") String modelId);

}
