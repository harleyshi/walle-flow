package com.walle.engine.loader.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walle.engine.domain.entity.FlowEngineDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 *
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Mapper
public interface FlowEngineMapper extends BaseMapper<FlowEngineDO> {

    List<FlowEngineDO> listAll();

    List<FlowEngineDO> getPublishedEngines();

    FlowEngineDO getEngineByName(@Param("name") String name);
}