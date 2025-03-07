package com.walle.engine.core.parser;

import com.walle.engine.core.builder.AsyncEngineBuilder;
import com.walle.engine.core.builder.Builders;
import com.walle.engine.core.builder.SimpleEngineBuilder;
import com.walle.engine.common.enums.ExecutionMode;
import com.walle.engine.common.exception.FlowException;
import com.walle.engine.domain.model.FlowDSL;
import com.walle.engine.domain.model.NodeInfo;
import com.walle.engine.core.Engine;
import com.walle.operator.FlowCtx;
import com.walle.operator.OperatorsRegister;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.component.IComponent;
import com.walle.operator.component.StandardComponent;
import com.walle.operator.node.Operator;
import com.walle.operator.utils.DAG;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2025/1/6
 */
public class DSLParser {

    public Engine<FlowCtx> parse(FlowDSL flowDSL) {
        FlowDSL.Graph graph = flowDSL.getContent();
        ExecutionMode executionMode = ExecutionMode.getByCode(flowDSL.getExecutionMode());
        // 解析算子
        Map<String, IComponent<FlowCtx, ?>> components = parseComponents(graph);
        // 构建DAG
        DAG<String> dag = builderDAG(graph);

        switch (Objects.requireNonNull(executionMode)){
            case SYNC -> {
                SimpleEngineBuilder engineBuilder = Builders.newSimpleEngine(flowDSL.getName(), flowDSL.getVersion());
                engineBuilder.originalDag(dag);
                engineBuilder.components(components);
                return engineBuilder.buildEngine();
            }
            case ASYNC -> {
                AsyncEngineBuilder engineBuilder = Builders.newAsyncEngine(flowDSL.getName(), flowDSL.getVersion());
                engineBuilder.originalDag(dag);
                engineBuilder.components(components);
                return engineBuilder.buildEngine();
            }
            default -> throw new FlowException("执行模式不存在");
        }
    }

    /**
     * 构建DAG
     */
    private DAG<String> builderDAG(FlowDSL.Graph graph){
        DAG<String> dag = new DAG<>();
        // 添加节点和边
        graph.getEdges().forEach(edge -> {
            if(edge.getData() != null){
                dag.addEdge(edge.getSource(), edge.getTarget(), edge.getLabel());
            }else{
                dag.addEdge(edge.getSource(), edge.getTarget());
            }
        });
        return dag;
    }

    /**
     * 解析算子
     */
    private Map<String, IComponent<FlowCtx, ?>> parseComponents(FlowDSL.Graph graph){
        Map<String, IComponent<FlowCtx, ?>> components = new ConcurrentHashMap<>();
        for (NodeInfo node : graph.getNodes()) {
            NodeType nodeType = NodeType.getByCode(node.getType());
            if(nodeType == null){
                throw new FlowException("节点类型不存在");
            }
            if(NodeType.START == nodeType || NodeType.END == nodeType){
                continue;
            }
            String operatorName = node.getLabel();
            String operatorVersion = node.getVersion();
            StandardComponent<FlowCtx, ?> component = new StandardComponent<>(operatorName);
            NodeInfo.NodeConfig config = node.getConfig();
            component.setTimeout(config.getTimeout());
            component.setIgnoreException(config.getIgnoreException());
            component.setOperator(builderOperator(operatorName, operatorVersion, config.getParams()));
            components.put(node.getId(), component);
        }
        return components;
    }

    protected <C extends FlowCtx, O> Operator<C, O> builderOperator(String name, String version, String params) {
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        return operatorsRegister.getOperator(name, version);
    }
}
