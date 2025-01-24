package com.walle.engine.parser.definition;

import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.NodeType;
import com.walle.operator.utils.AssertUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
@Getter
@Setter
public class StartNodeDefinition extends NodeDefinition {

    public StartNodeDefinition(String identify, String name) {
        super(identify, name);
    }

    @Override
    public void doVisit(DefinitionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeType nodeType() {
        return NodeType.START;
    }
}
