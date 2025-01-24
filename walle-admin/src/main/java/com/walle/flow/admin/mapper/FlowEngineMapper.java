package com.walle.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walle.flow.admin.domain.entity.FlowEngineDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Mapper
public interface FlowEngineMapper extends BaseMapper<FlowEngineDO> {

    FlowEngineDO getEngineByName(@Param("name") String name);

    int updateEngineById(FlowEngineDO entity);

    int changeStatus(@Param("id") Long id, @Param("status") String status);
}