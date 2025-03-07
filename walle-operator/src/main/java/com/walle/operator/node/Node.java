package com.walle.operator.node;

import com.walle.operator.FlowCtx;
import com.walle.operator.component.IComponent;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.*;

/**
 * @author harley.shi
 * @date 2025/3/6
 */
public class Node {

    private final List<String> nodeIds;

    private final IComponent<FlowCtx, ?> component;

    /**
     * 构造器：传入单个节点
     */
    public Node(String nodeId, IComponent<FlowCtx, ?> component) {
        this.nodeIds = List.of(nodeId);
        this.component = component;
    }

    /**
     * 构造器：传入多个节点
     * @param nodeIds 节点列表
     */
    public Node(List<String> nodeIds, IComponent<FlowCtx, ?> component) {
        this.nodeIds = new ArrayList<>();
        this.nodeIds.addAll(nodeIds);
        this.component = component;
    }

    /**
     * 获取节点算子
     */
    public IComponent<FlowCtx, ?> getComponent() {
        return component;
    }

    /**
     * 获取单个节点
     */
    public String getNodeId() {
        if (this.nodeIds.isEmpty()) {
            throw new IllegalStateException("节点为空，无法获取元素");
        }
        return this.nodeIds.get(0);
    }

    public int getSize() {
        return this.nodeIds.size();
    }

    @Override
    public String toString() {
        String componentName = null;
        if(component != null){
            componentName = component.name();
        }
        return componentName +"("+ String.join(",", nodeIds)+")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node superNode = (Node) o;
        return new EqualsBuilder().append(nodeIds, superNode.nodeIds).isEquals();
    }
}
