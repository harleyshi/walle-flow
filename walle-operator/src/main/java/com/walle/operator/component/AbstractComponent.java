package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 组件抽象类
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractComponent<C extends FlowCtx, O> implements IComponent<C, O>{

    /**
     * 组件名
     */
    private String name;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 是否忽略异常（如果pipeline忽略异常的话pipeline下的算子默认都会忽略异常）
     */
    private boolean ignoreException = false;

    public AbstractComponent(String name) {
        this.name = name;
    }

    @Override
    public O execute(C context){
        // 若出现异常，后续的节点不再执行
//        if(context.hasException()){
//            return null;
//        }
        try {
            return doExecute(context);
        } catch (Throwable ex) {
//            // 忽略异常：直接返回空结果
//            if(isIgnoreException()){
//                // 如果节点设置忽略异常的话，当节点发送异常时直接忽略
//                log.error("[{}] operator execute error, but ignore it. error message: {}", getName(), ex.getMessage());
//                return null;
//            }
//            context.setHasException(true);
            throw ex;
        }
    }

    /**
     * 组件执行方法
     * @param context 上下文
     * @return 是否成功
     * @throws Throwable
     */
    public abstract O doExecute(C context);
}
