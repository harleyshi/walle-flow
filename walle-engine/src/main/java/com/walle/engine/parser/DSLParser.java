package com.walle.engine.parser;


import com.walle.engine.common.enums.ExecutionMode;
import com.walle.engine.common.exception.FlowException;
import com.walle.engine.executor.DAGEngine;
import com.walle.engine.domain.model.FlowDSL;
import com.walle.engine.domain.model.NodeInfo;
import com.walle.engine.parser.definition.*;
import com.walle.operator.FlowCtx;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.utils.DirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author harley.shi
 * @date 2025/1/6
 */
public class DSLParser {

    public DAGEngine<FlowCtx> parse(FlowDSL flowDSL) {
        FlowDSL.Graph graph = flowDSL.getContent();
        ExecutionMode executionMode = ExecutionMode.getByCode(flowDSL.getExecutionMode());
        // 创建DAG引擎
        DAGEngine<FlowCtx> dagEngine = new DAGEngine<>(flowDSL.getName(), executionMode, flowDSL.getVersion());
        // 添加节点和边
        graph.getEdges().forEach(edge -> {
            if(edge.getData() != null){
                dagEngine.addEdge(edge.getSource(), edge.getTarget(), edge.getLabel());
            }else{
                dagEngine.addEdge(edge.getSource(), edge.getTarget());
            }
        });

        // 解析节点算子
        List<NodeDefinition> nodeDefList = new ArrayList<>();
        graph.getNodes().forEach(node -> nodeDefList.add(parserNode(dagEngine, node)));
        BuilderDefinitionVisitor visitor = new BuilderDefinitionVisitor(dagEngine);
        nodeDefList.forEach(visitor::visit);
        return visitor.getEngine();
    }

    private NodeDefinition parserNode(DAGEngine<FlowCtx> dagEngine, NodeInfo node){
        NodeType nodeType = NodeType.getByCode(node.getType());
        if(nodeType == null){
            throw new FlowException("节点类型不存在");
        }
        switch (nodeType){
            case STANDARD:
                StandardNodeDefinition standardNode = new StandardNodeDefinition(node.getId(), node.getLabel());
                standardNode.setVersion(node.getVersion());
                if (node.getConfig() != null) {
                    standardNode.setAsync(node.getConfig().getAsync());
                    standardNode.setTimeout(node.getConfig().getTimeout());
                    standardNode.setIgnoreException(node.getConfig().getIgnoreException());
                    standardNode.setParams(node.getConfig().getParams());
                }
                return standardNode;
            case SCRIPT:
                ScriptNodeDefinition scriptNode = new ScriptNodeDefinition(node.getId(), node.getLabel());
                scriptNode.setScriptLang(node.getScriptInfo().getScriptLang());
                scriptNode.setScript(node.getScriptInfo().getContent());
                return scriptNode;
            case CONDITION:
                Set<DirectedGraph.Edge<String>> neighbors = dagEngine.getNeighbors(node.getId());
                // choose条件分支
                if (neighbors.size() > 2) {
                    return new ChooseNodeDefinition(node.getId(), node.getLabel());
                } else { // if else 条件分支
                    return new IfNodeDefinition(node.getId(), node.getLabel());
                }
            case START:
                return new StartNodeDefinition(node.getId(), node.getLabel());
            case END:
                return new EndNodeDefinition(node.getId(), node.getLabel());
        }
        throw new FlowException(String.format("节点类型%s不存在", nodeType));
    }
}
