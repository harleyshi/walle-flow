package com.walle.operator;

import com.walle.operator.node.Operator;

/**
 * 算子元数据
 * @author harley.shi
 * @date 2025/1/23
 */
public class OperatorDef<C extends FlowCtx, O> {

    private final String name;

    private final String type;

    private final String version;

    private final Operator<C, O> operator;

    public OperatorDef(String name, String type, String version, Operator<C, O> operator) {
        this.name = name;
        this.type = type;
        this.version = version;
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public Operator<C, O> getOperator() {
        return operator;
    }
}
