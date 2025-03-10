package com.walle.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walle.flow.admin.domain.entity.WalleEngineDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Mapper
public interface WalleEngineMapper extends BaseMapper<WalleEngineDO> {

    WalleEngineDO getEngineByName(@Param("name") String name);

    int updateEngineById(WalleEngineDO entity);

    int changeStatus(@Param("id") Long id, @Param("status") String status);
}