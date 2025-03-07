package com.walle.flow.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.walle.flow.admin.common.R;
import com.walle.flow.admin.domain.vo.base.PageResp;
import com.walle.flow.admin.domain.vo.req.QueryOperatorsReq;
import com.walle.flow.admin.domain.vo.resp.OperatorsResp;
import com.walle.operator.OperatorDef;
import com.walle.operator.OperatorsRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author harley.shi
 * @date 2024/11/4
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/operators")
public class OperatorsController {

    @GetMapping("/list")
    public R<PageResp<OperatorsResp>> list(QueryOperatorsReq req) {
        log.info("OperatorsController list req: {}", JSON.toJSONString(req));
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        List<OperatorDef<?, ?>> operatorNames = operatorsRegister.operatorData();
        List<OperatorsResp> list = new ArrayList<>();
        for (OperatorDef<?, ?> operatorHolder : operatorNames) {
            OperatorsResp resp = new OperatorsResp();
            resp.setType(operatorHolder.getType());
            resp.setLabel(operatorHolder.getName());
            resp.setVersion(operatorHolder.getVersion());

            OperatorsResp.AdvancedConfig advancedConfig = new OperatorsResp.AdvancedConfig();
            advancedConfig.setTimeout(new Random().nextInt(1000) + 1);
            advancedConfig.setIgnoreException(true);
            advancedConfig.setParams("测试参数");
            resp.setConfig(advancedConfig);
            list.add(resp);
        }

        PageResp<OperatorsResp> pageResp = new PageResp<>();
        pageResp.setTotal((long) list.size());
        pageResp.setRecords(list);
        return R.ok(pageResp);
    }
}
