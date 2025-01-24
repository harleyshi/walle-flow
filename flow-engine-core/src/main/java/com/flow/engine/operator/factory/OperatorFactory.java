package com.flow.engine.operator.factory;


/**
 * 算子构造器工厂接口
 * @author harley.shi
 * @date 2024/7/1
 */
public interface OperatorFactory {

    Object create(OperatorFactoryMethod factoryMethod, String param);
}
