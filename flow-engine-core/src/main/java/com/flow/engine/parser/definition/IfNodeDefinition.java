package com.flow.engine.parser.definition;

import com.flow.engine.common.enums.NodeType;
import com.flow.engine.parser.DefinitionVisitor;
import lombok.Getter;
import lombok.Setter;


/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Getter
@Setter
public class IfNodeDefinition extends ConditionNodeDefinition {

    public IfNodeDefinition(String identify, String name) {
        super(identify, name);
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType nodeType() {
        return NodeType.IF;
    }
}
