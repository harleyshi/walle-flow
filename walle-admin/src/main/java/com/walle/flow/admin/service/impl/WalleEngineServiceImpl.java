package com.walle.flow.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walle.flow.admin.common.StatusEnum;
import com.walle.flow.admin.domain.entity.WalleEngineDO;
import com.walle.flow.admin.domain.vo.base.PageResp;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineReq;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineStatusReq;
import com.walle.flow.admin.domain.vo.req.QueryWalleEngineReq;
import com.walle.flow.admin.domain.vo.resp.QueryWalleEngineResp;
import com.walle.flow.admin.mapper.WalleEngineMapper;
import com.walle.flow.admin.service.WalleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


/**
 *
 * @author harley.shi
 * @date 2024-09-26 11:27:36
 */
@Slf4j
@Service
public class WalleEngineServiceImpl extends ServiceImpl<WalleEngineMapper, WalleEngineDO> implements WalleEngineService {

    @Override
    public PageResp<QueryWalleEngineResp> page(QueryWalleEngineReq req) {
        Page<WalleEngineDO> pageRet = baseMapper.selectPage(req.buildPage(), buildQueryWrapper(req));
        return new PageResp<>(BeanUtil.copyToList(pageRet.getRecords(), QueryWalleEngineResp.class), pageRet.getTotal());
    }

    private LambdaQueryWrapper<WalleEngineDO> buildQueryWrapper(QueryWalleEngineReq req) {
        LambdaQueryWrapper<WalleEngineDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotBlank(req.getName()), WalleEngineDO::getName, req.getName());
        queryWrapper.orderByDesc(WalleEngineDO::getCreateTime);
        return queryWrapper;
    }

    @Override
    public QueryWalleEngineResp detail(Long id) {
        WalleEngineDO engineDO = baseMapper.selectById(id);
        if(engineDO == null){
            return null;
        }
        return BeanUtil.copyProperties(engineDO, QueryWalleEngineResp.class);
    }

    @Override
    public void insertOrUpdate(EditWalleEngineReq req) {
        // 新增
        if(req.getId() == null){
            WalleEngineDO engineDO = BeanUtil.copyProperties(req, WalleEngineDO.class);
            engineDO.setStatus(StatusEnum.UNAVAILABLE.getCode());
            baseMapper.insert(engineDO);
        }else{ // 更新
            WalleEngineDO engineDO = baseMapper.selectById(req.getId());
            if(engineDO == null){
                throw new IllegalArgumentException("flow engine not exist");
            }
            WalleEngineDO updateEngineDO = BeanUtil.copyProperties(req, WalleEngineDO.class);
            baseMapper.updateEngineById(updateEngineDO);
        }
    }

    @Override
    public void changeStatus(EditWalleEngineStatusReq req) {
        baseMapper.changeStatus(req.getId(), req.getStatus());
    }
}