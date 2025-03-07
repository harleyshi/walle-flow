package com.walle.operator.common.constants;

import com.walle.operator.node.Node;

/**
 * @author harley.shi
 * @date 2025/3/5
 */
public interface Constants {
    String START_NODE = "0";

    String END_NODE = "99999999";

    Node START_NODE_DEF = new Node(START_NODE, null);

    Node END_NODE_DEF = new Node(END_NODE, null);
}
