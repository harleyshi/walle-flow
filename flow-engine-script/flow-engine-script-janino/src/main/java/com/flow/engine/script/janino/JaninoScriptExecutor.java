package com.flow.engine.script.janino;

import com.flow.engine.common.enums.ScriptLang;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.script.ScriptExecutor;
import com.flow.engine.exception.FlowException;
import org.codehaus.janino.ScriptEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author harley.shi
 * @date 2024/10/29
 */
public class JaninoScriptExecutor<T, C extends FlowCtx> extends ScriptExecutor<T, C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaninoScriptExecutor.class);

    @Override
    public ScriptLang getScriptLang() {
        return ScriptLang.JANINO;
    }

    @Override
    public T execute(C ctx) {
        try {
            ScriptEvaluator evaluator = new ScriptEvaluator();
            evaluator.setParameters(new String[] { "arg1", "arg2" }, new Class[] {Integer.class, Integer.class});
            evaluator.cook(script);

            Object value = evaluator.evaluate(new Object[]{11, 22});
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[JaninoScriptExecutor] evaluate script [{}] finished", script);
                LOGGER.debug("[JaninoScriptExecutor] evaluate value {}", value);
            }
            // 结果转换为 T 类型返回
            return (T) value;
        } catch (Exception e) {
            throw new FlowException(String.format("[%s] %s evaluate failed, script : %s", getScriptLang(), name, script), e);
        }
    }
}
