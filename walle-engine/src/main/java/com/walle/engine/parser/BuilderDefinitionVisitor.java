package com.walle.engine.parser;

import com.walle.engine.executor.DAGEngine;
import com.walle.engine.parser.definition.*;
import com.walle.operator.FlowCtx;
import com.walle.operator.component.EndComponent;
import com.walle.operator.component.IComponent;
import com.walle.operator.component.StandardComponent;
import com.walle.engine.builder.Builders;
import com.walle.engine.builder.ChooseBuilder;
import com.walle.engine.builder.IfBuilder;
import com.walle.engine.builder.ScriptBuilder;
import com.walle.operator.OperatorsRegister;
import com.walle.operator.component.StartComponent;
import com.walle.operator.node.Operator;
import com.walle.operator.utils.AssertUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
public class BuilderDefinitionVisitor implements DefinitionVisitor{
    private final DAGEngine<FlowCtx> dagEngine;

    private final Map<String, IComponent<FlowCtx, ?>> components = new ConcurrentHashMap<>();

    public BuilderDefinitionVisitor(DAGEngine<FlowCtx> dagEngine) {
        this.dagEngine = dagEngine;
    }

    @Override
    public void visit(ChooseNodeDefinition ed) {
        ChooseBuilder<FlowCtx, ?> chooseBuilder = Builders.newChoose(ed.getName());
        chooseBuilder.condition(builderOperator(ed.getName(), null));
    }

    @Override
    public void visit(IfNodeDefinition ed) {
        IfBuilder<FlowCtx, ?> ifBuilder = Builders.newIf(ed.getName());
        ifBuilder.condition(builderOperator(ed.getName(), null));
        components.put(ed.identify(), ifBuilder.build());
    }

    @Override
    public void visit(ScriptNodeDefinition ed) {
        ScriptBuilder<FlowCtx, ?, ?> scriptBuilder = Builders.script()
                .name(ed.getName())
                .type(ed.getScriptLang())
                .script(ed.getScript());
        components.put(ed.identify(), scriptBuilder.build());
    }

    @Override
    public void visit(StandardNodeDefinition ed) {
        StandardComponent<FlowCtx, ?> standardComponent = new StandardComponent<>(ed.getName());
        standardComponent.setTimeout(ed.getTimeout());
        standardComponent.setIgnoreException(ed.getIgnoreException());
        standardComponent.setOperator(builderOperator(ed.getName(), ed.getParams()));
        components.put(ed.identify(), standardComponent);
    }

    @Override
    public void visit(StartNodeDefinition ed) {
        EndComponent<FlowCtx, ?> endComponent = new EndComponent<>();
        components.put("0", endComponent);
    }

    @Override
    public void visit(EndNodeDefinition ed) {
        StartComponent<FlowCtx, ?> startComponent = new StartComponent<>();
        components.put("999999999", startComponent);
    }

    /**
     * 获取dagEngine
     */
    public DAGEngine<FlowCtx> getEngine() {
        AssertUtil.notEmpty(this.components, "components must not be empty!");
        this.components.forEach(dagEngine::addComponent);
        return dagEngine;
    }

    protected <C extends FlowCtx, O> Operator<C, O> builderOperator(String operator, String params) {
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        return operatorsRegister.getOperator(operator);
    }
}
