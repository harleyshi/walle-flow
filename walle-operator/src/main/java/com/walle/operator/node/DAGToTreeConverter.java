package com.walle.operator.node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author harley.shi
 * @date 2025/3/4
 */
public class DAGToTreeConverter<T> {

    private final Graph<T> dag;

    public DAGToTreeConverter(Graph<T> dag) {
        this.dag = dag;
    }
    /**
     * 转换 DAG 为树结构，返回根节点
     * 假设 DAG 中只有唯一入度为 0 的节点
     */
    public TreeNode<T> convertToTree() {
        // 获取所有入度为 0 的节点，如果有多个则取第一个或抛出异常，根据实际需求处理
        List<T> zeroInDegreeNodes = dag.getNodesWithZeroInDegree();
        if (zeroInDegreeNodes.isEmpty()) {
            throw new IllegalStateException("DAG中没有入度为0的节点，无法确定根节点");
        }
        // 如果严格要求只有一个根节点，可以在这里做检查
        if (zeroInDegreeNodes.size() > 1) {
            // 根据实际需求处理：例如抛异常或选择其中一个作为根
            throw new IllegalStateException("DAG中存在多个入度为0的节点，不满足单棵树的要求");
        }
        T root = zeroInDegreeNodes.iterator().next();
        return buildTree(root, new HashSet<>());
    }

    /**
     * 递归构造树
     * @param current 当前处理的节点
     * @param visited 当前递归路径上已访问的节点集合
     * @return 当前节点对应的 TreeNode
     */
    private TreeNode<T> buildTree(T current, Set<T> visited) {
        TreeNode<T> treeNode = new TreeNode<>(current);
        visited.add(current);
        // 遍历当前节点的所有出边
        for (Graph.Edge<T> edge : dag.getOutgoingEdges(current)) {
            T child = edge.getTarget();
            // 如果当前路径中已经访问过 child，则直接复制一个新节点
            if (visited.contains(child)) {
                treeNode.addChild(new TreeNode<>(child));
            } else {
                treeNode.addChild(buildTree(child, visited));
            }
        }
        // 回溯：递归完成后移除当前节点，保证其他递归路径能正确使用 visited 集合
        visited.remove(current);
        return treeNode;
    }
}
