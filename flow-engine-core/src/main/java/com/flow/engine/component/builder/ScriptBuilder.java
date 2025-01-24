package com.flow.engine.component.builder;


import com.flow.engine.model.FlowCtx;
import com.flow.engine.operator.factory.creator.DefaultObjectCreator;
import com.flow.engine.operator.factory.creator.ObjectCreator;
import com.flow.engine.script.ScriptDetector;
import com.flow.engine.script.ScriptExecutor;
import com.flow.engine.utils.AssertUtil;
import com.flow.engine.utils.AuxiliaryUtils;
import com.flow.engine.exception.FlowException;

/**
 * script构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class ScriptBuilder<T, C extends FlowCtx>{

    /**
     * Script's name, required.
     */
    private String name;

    /**
     * Script's type, required.
     */
    private String type;

    /**
     * Script content, required.
     */
    private String script;

    private final ObjectCreator objectCreator = DefaultObjectCreator.getInstance();

    public ScriptBuilder<T, C> name(String name) {
        AssertUtil.notBlank(name, "name must not be blank");
        this.name = name;
        return this;
    }

    public ScriptBuilder<T, C> type(String type) {
        AssertUtil.notBlank(type, "type must not be blank");
        this.type = type;
        return this;
    }

    public ScriptBuilder<T, C> script(String script) {
        AssertUtil.notBlank(script, "script must not be blank");
        this.script = script;
        return this;
    }

    public ScriptExecutor<T, C> build() {
        AssertUtil.notBlank(script, "script must not be blank");
        AssertUtil.notBlank(type, "type must not be blank");
        AssertUtil.notBlank(name, "name must not be blank");

        ScriptExecutor<T, C> executor = createExecutor(type);
        executor.setName(name);
        executor.setScript(script);
        return executor;
    }

    @SuppressWarnings("unchecked")
    private ScriptExecutor<T, C> createExecutor(String type) {
        Class<?> full = AuxiliaryUtils.asClass(type);
        if (full == null) {
            full = ScriptDetector.get().getJavaType(type);
        }
        String typename = full != null ? full.getName() : type;
        try {
            return objectCreator.create(typename, ScriptExecutor.class, false);
        }catch (Exception e){
            throw new FlowException(String.format("Failed to create script executor for typename %s", typename), e);
        }
    }
}
