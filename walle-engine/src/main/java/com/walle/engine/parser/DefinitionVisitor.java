package com.walle.engine.parser;

import com.walle.engine.parser.definition.*;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
public interface DefinitionVisitor {

    default void visit(NodeDefinition ed) {
        ed.visit(this);
    }

    void visit(ChooseNodeDefinition ed);

    void visit(IfNodeDefinition ed);

    void visit(ScriptNodeDefinition ed);

    void visit(StandardNodeDefinition ed);

    void visit(StartNodeDefinition ed);

    void visit(EndNodeDefinition ed);
}
