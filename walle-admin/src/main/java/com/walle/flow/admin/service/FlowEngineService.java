package com.walle.flow.admin.service;


import com.walle.flow.admin.domain.vo.base.PageResp;
import com.walle.flow.admin.domain.vo.req.EditFlowEngineReq;
import com.walle.flow.admin.domain.vo.req.EditFlowEngineStatusReq;
import com.walle.flow.admin.domain.vo.req.QueryFlowEngineReq;
import com.walle.flow.admin.domain.vo.resp.QueryFlowEngineResp;

/**
 *
 * @author harley.shi
 * @date 2024-09-26 13:44:47
 */
public interface FlowEngineService{

    PageResp<QueryFlowEngineResp> page(QueryFlowEngineReq req);

    QueryFlowEngineResp detail(Long id);

    void insertOrUpdate(EditFlowEngineReq req);

    void changeStatus(EditFlowEngineStatusReq req);
}