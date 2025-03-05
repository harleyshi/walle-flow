package com.walle.engine.parser.definition;


import com.walle.engine.parser.DefinitionVisitor;
import com.walle.operator.common.enums.ProcessType;

/**
 * @author harley.shi
 * @date 2024/10/28
 */
public class ChooseNodeDefinition extends ConditionNodeDefinition {

    public ChooseNodeDefinition(String identify, String name) {
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
