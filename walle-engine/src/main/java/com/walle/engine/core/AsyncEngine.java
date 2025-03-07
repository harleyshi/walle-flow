package com.walle.engine.core;

import com.walle.engine.core.executor.AsyncExecutor;
import com.walle.engine.core.executor.GraphExecutor;
import com.walle.operator.FlowCtx;
import com.walle.operator.node.Node;
import com.walle.operator.utils.DAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author harley.shi
 * @date 2025/1/8
 */
public class AsyncEngine<C extends FlowCtx> extends DAG<Node> implements Engine<C> {
    /*
     * name
     */
    private final String name;

    /*
     * version
     */
    private final String version;

    /**
     * constructor of AsyncEngine
     */
    public AsyncEngine(String name, String version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String version() {
        return this.version;
    }

    /**
     * build executor
     */
    public GraphExecutor<C> buildExecutor(){
        return new AsyncExecutor<>(this);
    }

    /**
     * 打印图的结构，效果类似如下格式
     */
    public void printGraph() {
        List<Node> roots = getNodesWithZeroInDegree();
        System.out.println();
        System.out.println(name + " (" + version+")");
        // 对于每个入度为 0 的节点，递归打印其子树
        for (int i = 0; i < roots.size(); i++) {
            printGraphRecursive(roots.get(i), "", i == roots.size() - 1);
        }
        System.out.println("-------------------------------------------------------------------------------------------");
    }

    /**
     * 递归打印每个节点及其出边，构造漂亮的树形结构
     * @param prefix 当前行的前缀（包含空格及连线）
     * @param isTail 当前节点是否为同级的最后一个
     */
    private void printGraphRecursive(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.toString());
        // 获取当前节点的所有出边
        Set<Edge<Node>> children = getOutgoingEdges(node);
        List<Edge<Node>> childrenList = new ArrayList<>(children);
        // 对于每个子节点，递归调用打印方法
        for (int i = 0; i < childrenList.size(); i++) {
            Node child = childrenList.get(i).getTarget();
            // 是否为最后一个子节点
            boolean last = i == childrenList.size() - 1;
            // 更新前缀，最后一个节点时使用空格，否则使用竖线标记
            printGraphRecursive(child, prefix + (isTail ? "    " : "│   "), last);
        }
    }
}
