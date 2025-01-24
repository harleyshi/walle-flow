//package com.walle.operator.node;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.node.input.Input3;
//
///**
// * @author harley.shi
// * @date 2025/1/13
// */
//public abstract class NodeOperator3<C extends FlowCtx, I1, I2, I3, O> extends AbstractOperator<C, Input3<I1, I2, I3>, O> {
//
//    @Override
//    public O executeNode(C ctx, Input3<I1, I2, I3> input) {
//        if(input == null){
//            return doExecute(ctx, null, null, null);
//        }
//        return doExecute(
//                ctx,
//                input.getInput1(),
//                input.getInput2(),
//                input.getInput3()
//        );
//    }
//
//    @Override
//    public O handleFailure(C ctx, Input3<I1, I2, I3> input) {
//        if(input == null){
//            return doFallback(ctx, null, null, null);
//        }
//        return doFallback(
//                ctx,
//                input.getInput1(),
//                input.getInput2(),
//                input.getInput3()
//        );
//    }
//
//    public abstract O doExecute(C ctx, I1 input1, I2 input2, I3 input3);
//
//    public abstract O doFallback(C ctx, I1 input1, I2 input2, I3 input3);
//}
