//package com.walle.operator.node;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.node.input.Input;
//
///**
// * @author harley.shi
// * @date 2025/1/13
// */
//public abstract class NodeOperator0<C extends FlowCtx, O> extends AbstractOperator<C, Input, O> {
//
//    @Override
//    public O executeNode(C ctx, Input input) {
//        return doExecute(ctx);
//    }
//
//    @Override
//    public O handleFailure(C ctx, Input input){
//        return doFallback(ctx);
//    }
//
//    /**
//     * 执行算子节点
//     */
//    public abstract O doExecute(C ctx);
//
//    /**
//     * 执行失败回调
//     */
//    public abstract O doFallback(C ctx);
//}
