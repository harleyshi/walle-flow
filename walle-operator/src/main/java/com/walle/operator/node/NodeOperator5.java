//package com.walle.operator.node;
//
//import com.walle.operator.FlowCtx;
//import com.walle.operator.node.input.Input5;
//
///**
// * @author harley.shi
// * @date 2025/1/13
// */
//public abstract class NodeOperator5<C extends FlowCtx, I1, I2, I3, I4, I5, O> extends AbstractOperator<C, Input5<I1, I2, I3, I4, I5>, O> {
//
//    @Override
//    public O executeNode(C ctx, Input5<I1, I2, I3, I4, I5> input) {
//        if(input == null){
//            return doExecute(ctx, null, null, null, null, null);
//        }
//        return doExecute(
//                ctx,
//                input.getInput1(),
//                input.getInput2(),
//                input.getInput3(),
//                input.getInput4(),
//                input.getInput5()
//        );
//    }
//
//
//    @Override
//    public O handleFailure(C ctx, Input5<I1, I2, I3, I4, I5> input) {
//        if(input == null){
//            return doFallback(ctx, null, null, null, null, null);
//        }
//        return doFallback(
//                ctx,
//                input.getInput1(),
//                input.getInput2(),
//                input.getInput3(),
//                input.getInput4(),
//                input.getInput5()
//        );
//    }
//
//    public abstract O doExecute(C ctx, I1 input1, I2 input2, I3 input3, I4 input4, I5 input5);
//
//    public abstract O doFallback(C ctx, I1 input1, I2 input2, I3 input3, I4 input4, I5 input5);
//}
