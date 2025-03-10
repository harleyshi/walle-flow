package com.walle.flow.admin.service;


import com.walle.flow.admin.domain.vo.base.PageResp;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineReq;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineStatusReq;
import com.walle.flow.admin.domain.vo.req.QueryWalleEngineReq;
import com.walle.flow.admin.domain.vo.resp.QueryWalleEngineResp;

/**
 *
 * @author harley.shi
 * @date 2024-09-26 13:44:47
 */
public interface WalleEngineService {

    PageResp<QueryWalleEngineResp> page(QueryWalleEngineReq req);

    QueryWalleEngineResp detail(Long id);

    void insertOrUpdate(EditWalleEngineReq req);

    void changeStatus(EditWalleEngineStatusReq req);
}