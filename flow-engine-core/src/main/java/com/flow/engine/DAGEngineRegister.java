package com.flow.engine;

import com.flow.engine.executor.DAGEngine;
import com.flow.engine.model.FlowCtx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
public class DAGEngineRegister {
    private static final DAGEngineRegister REGISTER = new DAGEngineRegister();

    private final Map<String, DAGEngine<FlowCtx>> flowExecutors = new ConcurrentHashMap<>();

    private DAGEngineRegister(){}

    public static DAGEngineRegister getInstance() {
        return REGISTER;
    }

    public void register(DAGEngine<FlowCtx> dagEngine) {
        flowExecutors.put(dagEngine.getName(), dagEngine);
    }

    public DAGEngine<FlowCtx> getEngine(String name) {
        return flowExecutors.get(name);
    }

}
