package com.walle.engine;


import com.walle.engine.executor.DAGEngine;
import com.walle.operator.FlowCtx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
public class DAGEngineRegister {
    private static final DAGEngineRegister REGISTER = new DAGEngineRegister();

    private final Map<String, DAGEngine<FlowCtx>> engineMap = new ConcurrentHashMap<>();

    private DAGEngineRegister(){}

    public static DAGEngineRegister getInstance() {
        return REGISTER;
    }

    public void register(DAGEngine<FlowCtx> dagEngine) {
        engineMap.put(dagEngine.getName(), dagEngine);
    }

    public DAGEngine<FlowCtx> getEngine(String name) {
        return engineMap.get(name);
    }

}
