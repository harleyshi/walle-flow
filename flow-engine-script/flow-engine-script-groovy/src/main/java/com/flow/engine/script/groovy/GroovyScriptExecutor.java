package com.flow.engine.script.groovy;

import com.flow.engine.common.enums.ScriptLang;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.script.ScriptExecutor;
import com.flow.engine.exception.FlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * groovy脚本执行器
 * @author harley.shi
 * @date 2024/10/29
 */
public class GroovyScriptExecutor<T, C extends FlowCtx> extends ScriptExecutor<T, C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptExecutor.class);

    private final ScriptEngine engine;

    public GroovyScriptExecutor(){
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(getScriptLang().getCode());
    }

    @Override
    public ScriptLang getScriptLang() {
        return ScriptLang.GROOVY;
    }

    @Override
    public T execute(C ctx) {
        try {
            Bindings data = engine.createBindings();
            Object params = ctx.getScriptParams();
            data.put("ctx", params);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Script parameters: {}", params);
            }
            Object value = engine.eval(script, data);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[GroovyScriptExecutor] evaluate script [{}] finished", script);
                LOGGER.debug("[GroovyScriptExecutor] evaluate value {}", value);
            }
            return (T) value;
        } catch (Exception e) {
            throw new FlowException(String.format("[%s] %s evaluate failed, script : %s", getScriptLang(), name, script), e);
        }
    }
}
