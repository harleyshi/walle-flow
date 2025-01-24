package com.flow.engine.script;

import com.flow.engine.common.enums.ScriptLang;
import com.flow.engine.model.FlowCtx;
import com.flow.engine.component.IComponent;
import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/29
 */
@Data
public abstract class ScriptExecutor<T, C extends FlowCtx> implements IComponent<T, C>, Describable {
    /**
     * 脚本名称
     */
    protected String name;

    /**
     * 执行脚本
     */
    protected String script;

    /**
     * 获取脚本语言
     *
     * @return type string
     */
    public abstract ScriptLang getScriptLang();

    /**
     * Execute script.
     * @param ctx ctx.
     * @return result.
     */
    public abstract T execute(C ctx);

    @Override
    public String describe() {
        return name + "@" + getScriptLang().getCode() + "-script";
    }
}
