package com.walle.operator;

import com.walle.operator.common.enums.NodeType;
import com.walle.operator.node.Operator;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author harley.shi
 * @date 2025/1/21
 */
public class OperatorBeanPostProcessor implements BeanPostProcessor {

    private final OperatorsRegister operatorsRegister = OperatorsRegister.getInstance();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!Operator.class.isAssignableFrom(targetClass)) {
            return bean;
        }
        if (!targetClass.isAnnotationPresent(ComponentFn.class)) {
            return bean;
        }
        ComponentFn componentFn = targetClass.getAnnotation(ComponentFn.class);
        Object targetBean = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean;
        if (!(targetBean instanceof Operator<?, ?>)) {
            throw new RuntimeException("target bean is not an instance of Operator");
        }
        Operator<?, ?> operator = (Operator<?, ?>) targetBean;
        String operatorName = componentFn.name();
        if(operatorsRegister.containsKey(operatorName)){
            throw new RuntimeException("operator name already exists");
        }
        String version = componentFn.version();
        NodeType type = componentFn.type();
        operatorsRegister.register(operatorName, new OperatorHolder<>(operatorName, type.getCode(), version, operator));
        return bean;
    }
}
