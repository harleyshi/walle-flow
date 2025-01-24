package com.flow.engine.operator;

import com.flow.engine.operator.factory.OperatorFactory;
import com.flow.engine.operator.factory.OperatorFactoryMethod;
import lombok.Getter;

/**
 * 算子定义
 * @author harley.shi
 * @date 2024/7/1
 */
@Getter
public class OperatorDefinition {

    private final String operatorName;

    private final OperatorFactory operatorFactory;

    private final OperatorFactoryMethod factoryMethod;

    public OperatorDefinition(String operatorName,
                              OperatorFactory operatorFactory,
                              OperatorFactoryMethod factoryMethod) {
        this.operatorName = operatorName;
        this.operatorFactory = operatorFactory;
        this.factoryMethod = factoryMethod;
    }

    public Object builder(String params) {
        return operatorFactory.create(factoryMethod, params);
    }
}
