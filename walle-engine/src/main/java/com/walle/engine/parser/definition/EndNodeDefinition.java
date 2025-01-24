package com.walle.engine.parser.definition;

import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.NodeType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Getter
@Setter
public class EndNodeDefinition extends NodeDefinition {

    public EndNodeDefinition(String identify, String name) {
        super(identify, name);
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType nodeType() {
        return NodeType.END;
    }
}
