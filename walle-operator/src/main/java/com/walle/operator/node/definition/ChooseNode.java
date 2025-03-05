//package com.walle.operator.node.definition;
//
//import com.walle.operator.common.enums.NodeType;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author harley.shi
// * @date 2024/10/28
// */
//public class ChooseNode extends ConditionNode {
//
//    /**
//     * 默认分支
//     */
//    private List<Node> defaultDef;
//
//    /**
//     * 条件分支
//     */
//    private Map<String, List<Node>> caseMap;
//
//
//    public ChooseNode(String nodeId) {
//        super(nodeId);
//    }
//
//    @Override
//    public NodeType nodeType() {
//        return NodeType.CHOOSE;
//    }
//}
