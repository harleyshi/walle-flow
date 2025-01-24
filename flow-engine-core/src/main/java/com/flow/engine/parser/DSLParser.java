package com.flow.engine.parser;

import com.flow.engine.DAGEngineRegister;
import com.flow.engine.common.enums.NodeTypeEnums;
import com.flow.engine.exception.FlowException;
import com.flow.engine.executor.DAGEngine;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.model.FlowDSL;
import com.flow.engine.model.NodeInfo;
import com.flow.engine.parser.definition.*;
import com.flow.engine.utils.DirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author harley.shi
 * @date 2025/1/6
 */
public class DSLParser {

    public void parse(FlowDSL flowDSL) {
        FlowDSL.Graph graph = flowDSL.getContent();
        // 创建DAG引擎
        DAGEngine<FlowCtx> dagEngine = new DAGEngine<>(flowDSL.getName());
        // 添加节点和边
        graph.getEdges().forEach(edge -> {
            if(edge.getData() != null){
                dagEngine.addEdge(edge.getSource(), edge.getTarget(), edge.getData());
            }else{
                dagEngine.addEdge(edge.getSource(), edge.getTarget());
            }
        });

        // 解析节点算子
        List<NodeDefinition> nodeDefList = new ArrayList<>();
        graph.getNodes().forEach(node -> nodeDefList.add(parserNode(dagEngine, node)));
        BuilderDefinitionVisitor visitor = new BuilderDefinitionVisitor(dagEngine);
        nodeDefList.forEach(visitor::visit);

        // 注册DAG引擎
        DAGEngineRegister.getInstance().register(visitor.getEngine());
    }

    private NodeDefinition parserNode(DAGEngine<FlowCtx> dagEngine, NodeInfo node){
        NodeTypeEnums nodeType = NodeTypeEnums.getByCode(node.getType());
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
        }
        throw new FlowException(String.format("节点类型%s不存在", nodeType));
    }
}
