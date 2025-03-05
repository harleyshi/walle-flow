package com.walle.operator.node;

/**
 * @author harley.shi
 * @date 2025/3/4
 */
import cn.hutool.core.collection.CollectionUtil;

import java.util.*;

public class DagScheduler {

    private Map<String, Set<String>> adjacencyList = new HashMap<>();
    private Map<String, Set<String>> reverseList = new HashMap<>();
    private Map<String, Integer> inDegree = new HashMap<>();
    private Set<String> disabledNodes = new HashSet<>();

    public void addEdge(String from, String to) {
        adjacencyList.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        reverseList.computeIfAbsent(to, k -> new HashSet<>()).add(from);
        inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        inDegree.putIfAbsent(from, 0);
    }



    public void execute(String startNode, boolean condition) {
        if (condition) {
            disableBranch("3");
        } else {
            disableBranch("2");
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> currentInDegree = new HashMap<>(inDegree);

        if (!disabledNodes.contains(startNode)) {
            queue.offer(startNode);
        }

        if(CollectionUtil.isNotEmpty(disabledNodes)){
            // 将禁用节点的下一个节点入度减少
            for (String disabledNode : disabledNodes) {
                for (String edgeNode : adjacencyList.get(disabledNode)) {
                    currentInDegree.put(edgeNode, currentInDegree.get(edgeNode) - 1);
                }
            }
        }

        while (!queue.isEmpty()) {
            String node = queue.poll();

            System.out.println("执行节点: " + node);

            for (String child : adjacencyList.getOrDefault(node, Collections.emptySet())) {
                // 如果边在禁用列表中，则跳过
                if (disabledNodes.contains(child)) {
                    continue;
                }
                currentInDegree.put(child, currentInDegree.get(child) - 1);
                if (currentInDegree.get(child) == 0) {
                    queue.offer(child);
                }
            }
        }
    }

    /**
     * 剪掉某个分支起点，并递归剪掉只能从这条分支到达的节点
     */
    public void disableBranch(String node) {
        Queue<String> queue = new LinkedList<>();
        disabledNodes.add(node);
        queue.offer(node);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String child : adjacencyList.getOrDefault(current, Collections.emptySet())) {
                if (disabledNodes.contains(child)) continue;

                // 判断 child 是否还有其他活跃父节点
                boolean hasActiveParent = false;
                for (String parent : reverseList.getOrDefault(child, Collections.emptySet())) {
                    if (!disabledNodes.contains(parent)) {
                        hasActiveParent = true;
                        break;
                    }
                }

                if (!hasActiveParent) {
                    disabledNodes.add(child);
                    queue.offer(child);
                }
            }
        }
    }

    public static void main(String[] args) {
        DagScheduler scheduler = new DagScheduler();

        // 构建 DAG
        scheduler.addEdge("1", "2");
        scheduler.addEdge("1", "3");
        scheduler.addEdge("2", "4");
        scheduler.addEdge("4", "6");
        scheduler.addEdge("3", "8");
        scheduler.addEdge("3", "55");
        scheduler.addEdge("8", "5");
        scheduler.addEdge("55", "5");
        scheduler.addEdge("5", "7");
        scheduler.addEdge("6", "9");
        scheduler.addEdge("7", "9");
        scheduler.addEdge("9", "10");
        scheduler.addEdge("10", "11");
        scheduler.addEdge("11", "12");


        System.out.println("=== 执行分支1 (剪掉3分支)===");
        scheduler.execute("1", false);

    }
}

