package com.walle.test.springboot.controller;


import com.walle.engine.EngineManager;
import com.walle.engine.executor.GraphExecutor;
import com.walle.operator.FlowCtx;
import com.walle.test.springboot.context.OrderContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author harley.shi
 * @date 2024/11/4
 */
@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private EngineManager engineManager;

    @GetMapping("/test")
    public List<String> test(String engineName) {
        OrderContext ctx = new OrderContext();
        GraphExecutor<FlowCtx> executor = engineManager.getEngineExecutor(engineName);
        executor.execute(ctx);

        return ctx.getItems();
    }
}
