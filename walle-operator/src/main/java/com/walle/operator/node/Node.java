package com.walle.operator.node;

import com.walle.operator.common.constants.Constants;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.*;

/**
 * @author harley.shi
 * @date 2025/3/5
 */
public class Node {

    private final List<String> nodeIds = new ArrayList<>();


    enum Type {
        NORMAL,
        START,
        END
    }

    /**
     * 构造器：传入单个节点
     */
    public Node(String nodeId) {
//        if(nodeId instanceof String){
//            if(nodeId.equals(Constants.START_NODE_ID)){
//                type = Type.START;
//            }
//        }
        this.nodeIds.add(nodeId);
    }

    /**
     * 构造器：传入多个节点
     * @param nodeIds 节点列表
     */
    public Node(List<String> nodeIds) {
        // 为防止外部修改，创建一个新列表
        this.nodeIds.addAll(nodeIds);
    }

    /**
     * 判断节点是否只有一个节点
     */
    public boolean isSingle() {
        return nodeIds.size() == 1;
    }

    /**
     * 获取所有节点
     */
    public List<String> getNodeIds() {
        return Collections.unmodifiableList(this.nodeIds);
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
        return Arrays.toString(nodeIds.toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return new EqualsBuilder().append(nodeIds, node.nodeIds).isEquals();
    }
}
