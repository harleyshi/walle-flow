package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import com.walle.operator.node.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 标准组件构造器
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
public class StandardComponent<C extends FlowCtx, O> implements IComponent<C, O> {
    /**
     * 组件名
     */
    private final String name;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 是否忽略异常
     */
    private boolean ignoreException = false;

    /**
     * 算子
     */
    private Operator<C, O> operator;

    public StandardComponent(String name) {
        this.name = name;
    }

    public void setOperator(Operator<C, O> operator) {
        this.operator = operator;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setIgnoreException(boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public O execute(C ctx) {
        try {
            return operator.execute(ctx);
        } catch (Throwable ex) {
            // 忽略异常：直接返回空结果
            if(ignoreException){
                // 如果节点设置忽略异常的话，当节点发送异常时直接忽略
                log.error("[{}] operator execute error, but ignore it. error message: {}", name, ex.getMessage());
                return null;
            }
            throw ex;
        }
    }
}
