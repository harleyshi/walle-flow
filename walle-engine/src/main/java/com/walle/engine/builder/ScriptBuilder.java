package com.walle.engine.builder;


import com.walle.engine.creator.DefaultObjectCreator;
import com.walle.engine.creator.ObjectCreator;
import com.walle.engine.common.exception.FlowException;
import com.walle.engine.script.ScriptDetector;
import com.walle.engine.script.ScriptExecutor;
import com.walle.operator.FlowCtx;
import com.walle.operator.utils.AssertUtil;
import com.walle.operator.utils.AuxiliaryUtils;

/**
 * script构造器
 * @author harley.shi
 * @date 2024/7/1
 */
public class ScriptBuilder<C extends FlowCtx, I, O>{

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

    public ScriptBuilder<C, I, O> name(String name) {
        AssertUtil.notBlank(name, "name must not be blank");
        this.name = name;
        return this;
    }

    public ScriptBuilder<C, I, O> type(String type) {
        AssertUtil.notBlank(type, "type must not be blank");
        this.type = type;
        return this;
    }

    public ScriptBuilder<C, I, O> script(String script) {
        AssertUtil.notBlank(script, "script must not be blank");
        this.script = script;
        return this;
    }

    public ScriptExecutor<C, I, O> build() {
        AssertUtil.notBlank(script, "script must not be blank");
        AssertUtil.notBlank(type, "type must not be blank");
        AssertUtil.notBlank(name, "name must not be blank");

        ScriptExecutor<C, I, O> executor = createExecutor(type);
        executor.setName(name);
        executor.setScript(script);
        return executor;
    }

    @SuppressWarnings("unchecked")
    private ScriptExecutor<C, I, O> createExecutor(String type) {
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
