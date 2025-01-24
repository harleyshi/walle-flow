package com.walle.test.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walle.test.springboot.domain.TestBDO;
import org.apache.ibatis.annotations.Mapper;


/**
 *
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Mapper
public interface TestBMapper extends BaseMapper<TestBDO> {
}