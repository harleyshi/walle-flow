package com.flow.engine.test;

import com.alibaba.fastjson2.JSONObject;
import com.flow.engine.DAGEngineRegister;
import com.flow.engine.common.enums.NodeTypeEnums;
import com.flow.engine.common.enums.StatusEnum;
import com.flow.engine.exception.FlowException;
import com.flow.engine.executor.DAGEngine;
import com.flow.engine.executor.IExecutor;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.model.FlowDSL;
import com.flow.engine.model.NodeInfo;
import com.flow.engine.operator.Operator;
import com.flow.engine.parser.BuilderDefinitionVisitor;
import com.flow.engine.parser.DSLParser;
import com.flow.engine.parser.definition.*;
import com.flow.engine.utils.DirectedGraph;

import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @author harley.shi
 * @date 2025/1/4
 */
public class EngineModelTest {

    public static void main(String[] args) {
//        FlowDSL flowDSL = new FlowDSL();
//        flowDSL.setId(1L);
//        flowDSL.setName("flow-test");
//        flowDSL.setDescription("测试的一个流程引擎");
//        flowDSL.setStatus(StatusEnum.PUBLISHED);
//        FlowDSL.Graph graph = parseFlowGraph();
//        flowDSL.setContent(graph);
//
//
////        ParserCtx parserCtx = new ParserCtx();
////        // 解析flowDSL
////        parserCtx.parserFlowDSL(flowDSL);
////
////        // 打印流程图
//////        printGraph(parserCtx.getDagEngine());
////
////
////
////        // 解析出所有节点
////        graph.getNodes().forEach(node -> {
////            parserCtx.register(parserNodeDefinition(parserCtx, node));
////        });
////
////        BuilderDefinitionVisitor visitor = new BuilderDefinitionVisitor(dagEngine);
////        List<NodeDefinition> nodeDefinitionList = parserCtx.getRegistered();
////        nodeDefinitionList.forEach(visitor::visit);
////
////        visitor.getEngine();
////
//////        visitor.getEngine();
////
//////        DAGEngine engine =  parserCtx.buildEngine();
////
////        DAGEngineRegister.getInstance().register(engine);
////        IExecutor executor = engine.buildExecutor();
//
//        FlowCtx ctx = new FlowCtx() {
//            @Override
//            public Object getScriptParams() {
//                return null;
//            }
//
//            @Override
//            public boolean hasException() {
//                return false;
//            }
//
//            @Override
//            public void setHasException(boolean hasException) {
//
//            }
//
//            @Override
//            public Stack<Operator<?>> rollbackStacks() {
//                return null;
//            }
//
//            @Override
//            public <C extends FlowCtx> void addRollback(Operator<C> rollback) {
//
//            }
//        };
//        DSLParser parser = new DSLParser();
//        parser.parse(flowDSL);
//        DAGEngine<FlowCtx> dagEngine = DAGEngineRegister.getInstance().getEngine(flowDSL.getName());
//        IExecutor<FlowCtx> executor = dagEngine.buildExecutor(1);
//        executor.execute(ctx);
//        System.out.println(1);


    }

    private static void printGraph(DirectedGraph<String> graph){
        graph.printGraph();

        // 获取某节点的出边数量和入边数量
//        System.out.println("获取节点2的入度数量: " + graph.getOutDegree("2"));
//        System.out.println("获取节点3的入度数量: " + graph.getInDegree("3"));
//        System.out.println("获取节点2的出边: " + graph.getNeighbors("2"));
//
//        // 获取入度为0的节点列表
//        List<String> nodesWithZeroInDegree = graph.getNodesWithZeroInDegree();
//        System.out.println("入度为数量为0的节点: " + nodesWithZeroInDegree);
//        System.out.println("获取图的所有节点: " + graph.getAllNodes());





        // 获取某节点的出边数量和入边数量
        System.out.println("获取节点6的入度数量: " + graph.getInDegree("6"));
        System.out.println("获取节点0的出边: " + graph.getNeighbors("0"));

        // 获取入度为0的节点列表
        List<String> nodesWithZeroInDegree = graph.getNodesWithZeroInDegree();
        System.out.println("入度为数量为0的节点: " + nodesWithZeroInDegree);
        System.out.println("获取图的所有节点: " + graph.getAllNodes());
    }

    private static FlowDSL.Graph parseFlowGraph() {
       return JSONObject.parseObject(parallelContent, FlowDSL.Graph.class);
    }

    // 并行的流程
    static String parallelContent  = """
    
    
   {"nodes":[{"id":"1","type":"standard","initialized":false,"position":{"x":335.6795959334896,"y":69.79568463536484},"data":{},"label":"节点-31","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":387,"ignoreException":false,"async":false}},{"id":"2","type":"standard","initialized":false,"position":{"x":463.79600511815124,"y":89.38998403442721},"data":{},"label":"节点-01","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":970,"ignoreException":false,"async":false}},{"id":"3","type":"standard","initialized":false,"position":{"x":612.3745879507032,"y":123.68859879812476},"data":{},"label":"节点-91","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":539,"ignoreException":false,"async":false}},{"id":"4","type":"standard","initialized":false,"position":{"x":330.9438474358336,"y":150.27064472143286},"data":{},"label":"节点-81","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":341,"ignoreException":false,"async":false}},{"id":"5","type":"standard","initialized":false,"position":{"x":328.4949931885442,"y":219.45285030046875},"data":{},"label":"节点-61","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":741,"ignoreException":false,"async":false}},{"id":"6","type":"standard","initialized":false,"position":{"x":460.9257337034138,"y":303.0436866087751},"data":{},"label":"节点-01","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":925,"ignoreException":false,"async":false}},{"id":"7","type":"standard","initialized":false,"position":{"x":467.1004801385143,"y":184.05700600937615},"data":{},"label":"节点-31","isScript":false,"script":null,"config":{"params":null,"rollbackParams":null,"timeout":644,"ignoreException":false,"async":false}}],"edges":[{"id":"vueflow__edge-1bottom-1-4top-4","type":"default","source":"1","target":"4","sourceHandle":"bottom-1","targetHandle":"top-4","data":{},"label":"","markerEnd":"arrowclosed","sourceX":386.6795959334896,"sourceY":105.29568463536484,"targetX":381.9438474358336,"targetY":146.77064472143286},{"id":"vueflow__edge-4bottom-4-5top-5","type":"default","source":"4","target":"5","sourceHandle":"bottom-4","targetHandle":"top-5","data":{},"label":"","markerEnd":"arrowclosed","sourceX":381.9438474358336,"sourceY":185.77064472143286,"targetX":379.4949931885442,"targetY":215.95285030046875},{"id":"vueflow__edge-5bottom-5-6top-6","type":"default","source":"5","target":"6","sourceHandle":"bottom-5","targetHandle":"top-6","data":{},"label":"","markerEnd":"arrowclosed","sourceX":379.4949931885442,"sourceY":254.95285030046875,"targetX":511.9257337034138,"targetY":299.5436866087751},{"id":"vueflow__edge-7bottom-7-6top-6","type":"default","source":"7","target":"6","sourceHandle":"bottom-7","targetHandle":"top-6","data":{},"label":"","markerEnd":"arrowclosed","sourceX":518.1004801385143,"sourceY":219.55700600937615,"targetX":511.9257337034138,"targetY":299.5436866087751},{"id":"vueflow__edge-3bottom-3-6top-6","type":"default","source":"3","target":"6","sourceHandle":"bottom-3","targetHandle":"top-6","data":{},"label":"","markerEnd":"arrowclosed","sourceX":663.3745879507032,"sourceY":159.18859879812476,"targetX":511.9257337034138,"targetY":299.5436866087751},{"id":"vueflow__edge-2bottom-2-7top-7","type":"default","source":"2","target":"7","sourceHandle":"bottom-2","targetHandle":"top-7","data":{},"label":"","markerEnd":"arrowclosed","sourceX":514.7960051181512,"sourceY":124.88998403442721,"targetX":518.1004801385143,"targetY":180.55700600937615}],"position":[-322.36958113924743,-54.839371244139954],"zoom":2,"viewport":{"x":-322.36958113924743,"y":-54.839371244139954,"zoom":2}}
    
    
    """;


    static String content = """
            {
                "nodes": [
                    {
                        "id": "1",
                        "type": "standard",
                        "initialized": false,
                        "position": {
                            "x": 275.4289091846616,
                            "y": -12.242141716744811
                        },
                        "data": {
            
                        },
                        "label": "standard：节点-01",
                        "isScript": false,
                        "script": null,
                        "config": {
                            "params": null,
                            "rollbackParams": null,
                            "timeout": 267,
                            "ignoreException": false,
                            "async": false
                        }
                    },
                    {
                        "id": "2",
                        "type": "condition",
                        "initialized": false,
                        "position": {
                            "x": 306.09233996665864,
                            "y": 52.47649362464077
                        },
                        "data": {
            
                        },
                        "label": "condition：节点-01",
                        "isScript": false,
                        "script": null,
                        "config": {
                            "params": null,
                            "rollbackParams": null,
                            "timeout": 192,
                            "ignoreException": false,
                            "async": false
                        }
                    },
                    {
                        "id": "3",
                        "type": "standard",
                        "initialized": false,
                        "position": {
                            "x": 346.404418669792,
                            "y": 145.0471496995312
                        },
                        "data": {
            
                        },
                        "label": "standard：节点-91",
                        "isScript": false,
                        "script": null,
                        "config": {
                            "params": null,
                            "rollbackParams": null,
                            "timeout": 644,
                            "ignoreException": false,
                            "async": false
                        }
                    },
                    {
                        "id": "4",
                        "type": "standard",
                        "initialized": false,
                        "position": {
                            "x": 215.43238871205676,
                            "y": 144.24214171674475
                        },
                        "data": {
            
                        },
                        "label": "standard：节点-91",
                        "isScript": false,
                        "script": null,
                        "config": {
                            "params": null,
                            "rollbackParams": null,
                            "timeout": 644,
                            "ignoreException": false,
                            "async": false
                        }
                    },
                    {
                        "id": "5",
                        "type": "standard",
                        "initialized": false,
                        "position": {
                            "x": 292.74103337737995,
                            "y": 252.29555757510377
                        },
                        "data": {
            
                        },
                        "label": "standard：节点-11",
                        "isScript": false,
                        "script": null,
                        "config": {
                            "params": null,
                            "rollbackParams": null,
                            "timeout": 591,
                            "ignoreException": false,
                            "async": false
                        }
                    }
                ],
                "edges": [
                    {
                        "id": "vueflow__edge-1bottom-1-2top-2",
                        "type": "default",
                        "source": "1",
                        "target": "2",
                        "sourceHandle": "bottom-1",
                        "targetHandle": "top-2",
                        "data": null,
                        "label": "",
                        "markerEnd": "arrowclosed",
                        "sourceX": 326.4289091846616,
                        "sourceY": 23.25785828325519,
                        "targetX": 322.0360045174399,
                        "targetY": 43.50891855139858
                    },
                    {
                        "id": "vueflow__edge-2bottom-2-4top-4",
                        "type": "default",
                        "source": "2",
                        "target": "4",
                        "sourceHandle": "bottom-2",
                        "targetHandle": "top-4",
                        "data": "true",
                        "label": "",
                        "markerEnd": "arrowclosed",
                        "sourceX": 322.0076842049399,
                        "sourceY": 93.10503366370327,
                        "targetX": 266.43238871205676,
                        "targetY": 140.74214171674475
                    },
                    {
                        "id": "vueflow__edge-2bottom-2-3top-3",
                        "type": "default",
                        "source": "2",
                        "target": "3",
                        "sourceHandle": "bottom-2",
                        "targetHandle": "top-3",
                        "data": "false",
                        "label": "",
                        "markerEnd": "arrowclosed",
                        "sourceX": 322.0076842049399,
                        "sourceY": 93.10503366370327,
                        "targetX": 397.404418669792,
                        "targetY": 141.5471496995312
                    },
                    {
                        "id": "vueflow__edge-4bottom-4-5top-5",
                        "type": "default",
                        "source": "4",
                        "target": "5",
                        "sourceHandle": "bottom-4",
                        "targetHandle": "top-5",
                        "data": null,
                        "label": "",
                        "markerEnd": "arrowclosed",
                        "sourceX": 266.43238871205676,
                        "sourceY": 179.74214171674475,
                        "targetX": 343.74103337737995,
                        "targetY": 248.79555757510377
                    },
                    {
                        "id": "vueflow__edge-3bottom-3-5top-5",
                        "type": "default",
                        "source": "3",
                        "target": "5",
                        "sourceHandle": "bottom-3",
                        "targetHandle": "top-5",
                        "data": null,
                        "label": "",
                        "markerEnd": "arrowclosed",
                        "sourceX": 397.404418669792,
                        "sourceY": 180.5471496995312,
                        "targetX": 343.74103337737995,
                        "targetY": 248.79555757510377
                    }
                ]
            }


            """;
}
