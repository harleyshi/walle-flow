package com.flow.engine.parser;

import com.flow.engine.executor.DAGEngine;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.component.IComponent;
import com.flow.engine.component.StandardComponent;
import com.flow.engine.component.builder.Builders;
import com.flow.engine.component.builder.ChooseBuilder;
import com.flow.engine.component.builder.IfBuilder;
import com.flow.engine.component.builder.ScriptBuilder;
import com.flow.engine.operator.Condition;
import com.flow.engine.operator.Operator;
import com.flow.engine.operator.OperatorsRegister;
import com.flow.engine.parser.definition.ChooseNodeDefinition;
import com.flow.engine.parser.definition.IfNodeDefinition;
import com.flow.engine.parser.definition.ScriptNodeDefinition;
import com.flow.engine.parser.definition.StandardNodeDefinition;
import com.flow.engine.utils.AssertUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/11/20
 */
public class BuilderDefinitionVisitor implements DefinitionVisitor{
    private final DAGEngine<FlowCtx> dagEngine;

    private final Map<String, IComponent<?, FlowCtx>> components = new ConcurrentHashMap<>();

    public BuilderDefinitionVisitor(DAGEngine<FlowCtx> dagEngine) {
        this.dagEngine = dagEngine;
    }

    @Override
    public void visit(ChooseNodeDefinition ed) {
        ChooseBuilder<?, ?> chooseBuilder = Builders.newChoose(ed.getName());
        chooseBuilder.condition(builderCondition(ed.getName()));
    }

    @Override
    public void visit(IfNodeDefinition ed) {
        IfBuilder<?, FlowCtx> ifBuilder = Builders.newIf(ed.getName());
        ifBuilder.condition(builderCondition(ed.getName()));
        components.put(ed.identify(), ifBuilder.build());
    }

    @Override
    public void visit(ScriptNodeDefinition ed) {
        ScriptBuilder<?, FlowCtx> scriptBuilder = Builders.script()
                .name(ed.getName())
                .type(ed.getScriptLang())
                .script(ed.getScript());
        components.put(ed.identify(), scriptBuilder.build());
    }

    @Override
    public void visit(StandardNodeDefinition ed) {
        StandardComponent<FlowCtx> standardComponent = new StandardComponent<>(ed.getName());
        standardComponent.setTimeout(ed.getTimeout());
        standardComponent.setIgnoreException(ed.getIgnoreException());
        standardComponent.setOperator(builderOperator(ed.getName(), ed.getParams()));
        components.put(ed.identify(), standardComponent);
    }

    /**
     * 获取dagEngine
     */
    public DAGEngine<FlowCtx> getEngine() {
        AssertUtil.notEmpty(this.components, "components must not be empty!");
        this.components.forEach((key, value) -> dagEngine.addComponent(key, value));
        return dagEngine;
    }

    @SuppressWarnings("unchecked")
    protected <C extends FlowCtx> Operator<C> builderOperator(String operator, String params) {
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        return (Operator<C>) operatorsRegister.getOperatorDefine(operator).builder(params);
    }

    @SuppressWarnings("unchecked")
    protected <T, C extends FlowCtx> Condition<T, C> builderCondition(String operator) {
        OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();
        return (Condition<T, C>) operatorsRegister.getOperatorDefine(operator).builder(null);
    }
}
