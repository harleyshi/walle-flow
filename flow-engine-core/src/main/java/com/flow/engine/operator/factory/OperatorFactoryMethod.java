package com.flow.engine.operator.factory;

import com.flow.engine.exception.FlowException;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 算子工厂方法类
 * @author harley.shi
 * @date 2024/7/1
 */
@Getter
public class OperatorFactoryMethod {

    private final Object target;

    private final Method method;

    private final Function<Object[], Object> targetOperator;

    public OperatorFactoryMethod(Object target, Method method) {
        this.target = target;
        this.method = method;
        this.targetOperator = null;
    }

    public OperatorFactoryMethod(Object target, Method method, Function<Object[], Object> targetOperator) {
        this.target = target;
        this.method = method;
        this.targetOperator = targetOperator;
    }

    public Object invoke(Object... args){
        try {
            return method.invoke(this.target, args);
        } catch (Exception e) {
            throw new FlowException(String.format("[%s#%s] 方法初始化失败", target.getClass(), method.getName()), e);
        }
    }
}
