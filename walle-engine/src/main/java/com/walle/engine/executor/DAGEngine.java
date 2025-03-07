package com.walle.engine.executor;

import com.walle.engine.common.enums.ExecutionMode;
import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;
import com.walle.operator.node.Node;
import com.walle.operator.utils.DAG;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class DAGEngine<C extends FlowCtx> {
    /*
     * name
     */
    private final String name;

    /*
     * execution mode
     */
    private final ExecutionMode executionMode;

    /*
     * version
     */
    private final String version;

    /*
     * nodeId -> component
     */
    private final Map<String, IComponent<C, ?>> components = new ConcurrentHashMap<>();

    /*
     * DAG graph
     */
    private DAG dagGraph;

    /**
     * constructor of DAGEngine
     */
    public DAGEngine(String name, ExecutionMode executionMode, String version) {
        this.name = name;
        this.executionMode = executionMode;
        this.version = version;
    }

    public Set<DAG.Edge> getOutgoingEdges(Node node) {
        return dagGraph.getOutgoingEdges(node);
    }

    public void setDagGraph(DAG dagGraph) {
        this.dagGraph = dagGraph;
    }

    public DAG getDagGraph() {
        return dagGraph;
    }

    /**
     * add component to engine
     */
    public void addComponent(String nodeId, IComponent<C, ?> component) {
        components.put(nodeId, component);
    }

    /**
     * get component by node id
     */
    public IComponent<C, ?> getComponent(String nodeId){
        return components.get(nodeId);
    }

    /**
     * get all components
     */
    public Map<String, IComponent<C, ?>> getComponents() {
        return components;
    }

    /**
     * build executor
     */
    public GraphExecutor<C> buildExecutor(){
        return switch (executionMode) {
            case SYNC -> new SimpleExecutor<>(this);
            case BATCH -> new SimpleExecutor<>(this);  // TODO: batch executor
            case ASYNC -> new AsyncExecutor<>(this);
        };
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
