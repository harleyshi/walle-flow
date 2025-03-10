package com.walle.flow.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.walle.flow.admin.common.R;
import com.walle.flow.admin.domain.vo.base.PageResp;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineReq;
import com.walle.flow.admin.domain.vo.req.EditWalleEngineStatusReq;
import com.walle.flow.admin.domain.vo.req.QueryWalleEngineReq;
import com.walle.flow.admin.domain.vo.resp.QueryWalleEngineResp;
import com.walle.flow.admin.service.WalleEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author harley.shi
 * @date 2024/11/4
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/engine")
public class WalleEngineController {

    @Autowired
    private WalleEngineService flowEngineService;

    @GetMapping("/list")
    public R<PageResp<QueryWalleEngineResp>> list(QueryWalleEngineReq req) {
        log.info("WalleEngineController list req: {}", JSON.toJSONString(req));
        return R.ok(flowEngineService.page(req));
    }

    @GetMapping("/detail")
    public R<QueryWalleEngineResp> detail(Long id) {
        log.info("WalleEngineController detail req: {}", id);
        QueryWalleEngineResp resp = flowEngineService.detail(id);
        return R.ok(resp);
    }

    @GetMapping("/changeStatus")
    public R<Void> changeStatus(EditWalleEngineStatusReq req) {
        log.info("WalleEngineController changeStatus req: {}", JSON.toJSONString(req));
        flowEngineService.changeStatus(req);
        return R.ok();
    }

    @PostMapping("/edit")
    public R<Void> edit(@RequestBody EditWalleEngineReq req) {
        log.info("WalleEngineController edit req: {}", JSON.toJSONString(req));
        flowEngineService.insertOrUpdate(req);
        return R.ok();
    }
}
