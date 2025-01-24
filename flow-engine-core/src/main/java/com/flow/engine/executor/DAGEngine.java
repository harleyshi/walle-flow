package com.flow.engine.executor;

import com.flow.engine.model.FlowCtx;
import com.flow.engine.component.IComponent;
import com.flow.engine.utils.DirectedGraph;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class DAGEngine<C extends FlowCtx> extends DirectedGraph<String> {
    /*
     * name
     */
    @Getter
    private final String name;

    /*
     * nodeId -> component
     */
    private final Map<String, IComponent<?, C>> components = new ConcurrentHashMap<>();

    /**
     * constructor of DAGEngine
     */
    public DAGEngine(String name) {
        this.name = name;
    }

    /**
     * add component to engine
     */
    public void addComponent(String nodeId, IComponent<?, C> component) {
        components.put(nodeId, component);
    }

    /**
     * get component by node id
     */
    public IComponent<?, C> getComponent(String nodeId){
        return components.get(nodeId);
    }

    /**
     * get all components
     */
    public Map<String, IComponent<?, C>> getComponents() {
        return components;
    }

    /**
     * build executor
     */
    public IExecutor<C> buildExecutor(Integer exeType){
        if(exeType == 1){
            return new AsyncReactiveExecutor<>(this);
        } else{
            return new DefaultExecutor<>(this);
        }
    }
}
