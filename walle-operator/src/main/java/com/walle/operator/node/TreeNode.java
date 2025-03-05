package com.walle.operator.node;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author harley.shi
 * @date 2025/3/4
 */
@Data
public class TreeNode<T> {
    private T data;
    private List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T data) {
        this.data = data;
    }

    public void addChild(TreeNode<T> child) {
        children.add(child);
    }

}
