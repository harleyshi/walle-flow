package com.flow.engine.component.builder;


import com.flow.engine.model.FlowCtx;

public final class Builders {

    public static <T, C extends FlowCtx> ChooseBuilder<T, C> newChoose(String name) {
        return new ChooseBuilder<>(name);
    }

    public static <T, C extends FlowCtx> IfBuilder<T, C> newIf(String name) {
        return new IfBuilder<>(name);
    }


//    public static <C extends FlowCtx> PipelineBuilder<C> pipeline() {
//        return new PipelineBuilder<>();
//    }

    public static <T, C extends FlowCtx> ScriptBuilder<T, C> script() {
        return new ScriptBuilder<>();
    }

//    public static <C extends FlowCtx> EngineBuilder<C> engine() {
//        return new EngineBuilder<>();
//    }
}
