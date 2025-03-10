package com.walle.engine.loader.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walle.engine.domain.entity.WalleEngineDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Mapper
public interface WalleEngineMapper extends BaseMapper<WalleEngineDO> {

    List<WalleEngineDO> listAll();

    List<WalleEngineDO> getPublishedEngines();

    WalleEngineDO getEngineByName(@Param("name") String name);
}