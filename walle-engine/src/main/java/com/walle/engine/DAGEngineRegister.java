package com.walle.engine;

import com.walle.engine.core.Engine;
import com.walle.operator.FlowCtx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/10/30
 */
public class DAGEngineRegister {
    private static final DAGEngineRegister REGISTER = new DAGEngineRegister();

    private final Map<String, Engine<FlowCtx>> engineMap = new ConcurrentHashMap<>();

    private DAGEngineRegister(){}

    public static DAGEngineRegister getInstance() {
        return REGISTER;
    }

    public void register(Engine<FlowCtx> dagEngine) {
        engineMap.put(dagEngine.name(), dagEngine);
    }

    public Engine<FlowCtx> getEngine(String name) {
        return engineMap.get(name);
    }

}
