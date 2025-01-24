package com.walle.engine.parser.definition;

import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.NodeType;
import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Data
public abstract class NodeDefinition implements Validator {
    protected volatile boolean visitCalled = false;
    /**
     * 节点编码
     */
    protected final String identify;

    /**
     * 节点名称
     */
    protected final String name;

    public NodeDefinition(String identify, String name) {
        this.identify = identify;
        this.name = name;
    }

    public void visit(DefinitionVisitor visitor) {
        if (this.visitCalled) {
            return;
        }

        this.visitCalled = true;
        this.doVisit(visitor);
    }

    public abstract void doVisit(DefinitionVisitor visitor);

    /**
     * 节点类型
     */
    public abstract NodeType nodeType();

    public String identify() {
        return this.identify;
    }

    public String name() {
        return this.name;
    }

    @Override
    public void validate() {}
}
