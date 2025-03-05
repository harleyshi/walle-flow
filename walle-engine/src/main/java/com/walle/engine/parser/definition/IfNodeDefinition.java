package com.walle.engine.parser.definition;

import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.ProcessType;
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
    public ProcessType nodeType() {
        return ProcessType.CONDITION;
    }
}
