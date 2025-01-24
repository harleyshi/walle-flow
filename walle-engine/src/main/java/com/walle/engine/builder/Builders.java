package com.walle.engine.builder;


import com.walle.operator.FlowCtx;

public final class Builders {

    public static <C extends FlowCtx, I, O> ChooseBuilder<C, O> newChoose(String name) {
        return new ChooseBuilder<>(name);
    }

    public static <C extends FlowCtx, I, O> IfBuilder<C, O> newIf(String name) {
        return new IfBuilder<>(name);
    }

    public static <C extends FlowCtx, I, O> ScriptBuilder<C, I, O> script() {
        return new ScriptBuilder<>();
    }
}
