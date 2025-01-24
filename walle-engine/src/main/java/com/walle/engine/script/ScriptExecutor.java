package com.walle.engine.script;

import com.walle.operator.FlowCtx;
import com.walle.operator.common.enums.ScriptLang;
import com.walle.operator.component.IComponent;
import lombok.Data;

/**
 * @author harley.shi
 * @date 2024/10/29
 */
@Data
public abstract class ScriptExecutor<C extends FlowCtx, I, O> implements IComponent<C, O>, Describable {
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
    public abstract O execute(C ctx, I input);

    @Override
    public String describe() {
        return name + "@" + getScriptLang().getCode() + "-script";
    }
}
