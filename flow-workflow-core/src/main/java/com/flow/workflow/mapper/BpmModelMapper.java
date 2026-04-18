package com.flow.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.workflow.entity.BpmModel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 流程模型扩展Mapper
 */
public interface BpmModelMapper extends BaseMapper<BpmModel> {

    /**
     * 根据Flowable模型ID更新状态
     */
    @Update("UPDATE bpm_model SET status = #{status} WHERE model_id = #{modelId}")
    int updateStatusByModelId(@Param("modelId") String modelId, @Param("status") Integer status);

    /**
     * 根据模型标识查询
     */
    BpmModel selectByModelKey(@Param("modelKey") String modelKey);

}
