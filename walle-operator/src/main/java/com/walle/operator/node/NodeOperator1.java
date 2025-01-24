//package com.walle.operator.node;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.node.input.Input1;
//
///**
// * @author harley.shi
// * @date 2025/1/13
// */
//public abstract class NodeOperator1<C extends FlowCtx, I, O> extends AbstractOperator<C, Input1<I>, O> {
//
//    @Override
//    public O executeNode(C ctx, Input1<I> input) {
//        if(input == null){
//            return doExecute(ctx, null);
//        }
//        return doExecute(ctx, input.getInput());
//    }
//
//    @Override
//    public O handleFailure(C ctx, Input1<I> input){
//        if(input == null){
//            return doFallback(ctx, null);
//        }
//        return doFallback(ctx, input.getInput());
//    }
//
//    public abstract O doExecute(C ctx, I input);
//
//    public abstract O doFallback(C ctx, I input);
//}
