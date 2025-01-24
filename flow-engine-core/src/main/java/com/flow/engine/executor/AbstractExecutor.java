package com.flow.engine.executor;

import com.flow.engine.model.FlowCtx;
import com.flow.engine.operator.Operator;
import com.flow.engine.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * abstract executor
 * @author harley.shi
 * @date 2024/7/3
 */
@Slf4j
public abstract class AbstractExecutor<C extends FlowCtx> implements IExecutor<C>{

    abstract void doExecute(C context);

    @Override
    public void execute(C context) {
        AssertUtil.notNull(context, "context must not be null!");
        try{
            // 执行正常流程
            doExecute(context);
        }catch (Exception e){
            // 有异常：执行回滚逻辑
            if(context.hasException()){
//                executeRollback(context);
            }
            throw e;
        }
    }


//    @SuppressWarnings("unchecked")
//    public void executeRollback(C context) {
//        Stack<Operator<?>> rollbackStack = context.rollbackStacks();
//        while (!rollbackStack.isEmpty()){
//            Operator<C> invoker = (Operator<C>) rollbackStack.pop();
//            if(invoker == null){
//                break;
//            }
//            try{
//                invoker.exec(context);
//            }catch (Exception e){
//                // TODO 回滚失败，做特殊处理
//                log.error("[{}] executeRollback error", invoker.getClass(), e);
//            }
//        }
//    }
}
