package com.walle.engine.core.builder;

import com.walle.engine.core.SimpleEngine;
import com.walle.operator.FlowCtx;
import com.walle.operator.common.constants.Constants;
import com.walle.operator.component.IComponent;
import com.walle.operator.component.PipelineComponent;
import com.walle.operator.node.Node;
import com.walle.operator.utils.AssertUtil;
import com.walle.operator.utils.DAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 同步执行引擎构造器
 * @author harley.shi
 * @date 2025/1/5
 */
public class SimpleEngineBuilder {
    private final String name;

    private final String version;

    private DAG<String> originalDag;

    private Map<String, IComponent<FlowCtx, ?>> components;

    public SimpleEngineBuilder(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void originalDag(DAG<String> originalDag) {
        AssertUtil.notNull(originalDag, "originalDag must not be null!");
        this.originalDag = originalDag;
    }

    public void components(Map<String, IComponent<FlowCtx, ?>> components) {
        AssertUtil.notEmpty(components, "components must not be empty!");
        this.components = components;
    }

    public SimpleEngine<FlowCtx> buildEngine() {
        List<String> sortedNodes = originalDag.topologicalSortKahn();

        List<String> pipelineNames = new ArrayList<>();
        List<IComponent<FlowCtx, ?>> componentList = new ArrayList<>();
        sortedNodes.forEach(nodeId -> {
            // 开始结束节点不处理
            if(Constants.END_NODE.equals(nodeId) || Constants.START_NODE.equals(nodeId)){
                return;
            }
            IComponent<FlowCtx, ?> component = components.get(nodeId);
            pipelineNames.add(component.name());
            componentList.add(component);
        });
        String pipelineName = Arrays.toString(pipelineNames.toArray());
        PipelineComponent<FlowCtx, ?> pipelineComponent = new PipelineComponent<>(pipelineName, componentList);
        return new SimpleEngine<>(name, version, pipelineComponent);
    }
}
