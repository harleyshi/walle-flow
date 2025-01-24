//package com.walle.operator.node;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.node.input.Input2;
//
///**
// * @author harley.shi
// * @date 2025/1/13
// */
//public abstract class NodeOperator2<C extends FlowCtx, I1, I2, O> extends AbstractOperator<C, Input2<I1, I2>, O> {
//
//    @Override
//    public O executeNode(C ctx, Input2<I1, I2> input) {
//        if(input == null){
//            return doExecute(ctx, null, null);
//        }
//        return doExecute(ctx, input.getInput1(), input.getInput2());
//    }
//
//    @Override
//    public O handleFailure(C ctx, Input2<I1, I2> input){
//        if(input == null){
//            return doFallback(ctx, null, null);
//        }
//        return doFallback(ctx, input.getInput1(), input.getInput2());
//    }
//
//    public abstract O doExecute(C ctx, I1 input1, I2 input2);
//
//    public abstract O doFallback(C ctx, I1 input1, I2 input2);
//}
