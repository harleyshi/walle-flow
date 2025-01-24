package com.flow.engine.operator.factory.builder;

import com.alibaba.fastjson2.JSONObject;
import com.flow.engine.operator.factory.OperatorFactory;
import com.flow.engine.operator.factory.OperatorFactoryMethod;
import com.flow.engine.exception.FlowException;
import lombok.extern.slf4j.Slf4j;

/**
 * 算子生成器
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
public class OperatorGenerate implements OperatorFactory {

    @Override
    public Object create(OperatorFactoryMethod factoryMethod, String param) {
        Class<?>[] parameterTypes = factoryMethod.getMethod().getParameterTypes();
        if (parameterTypes.length == 0) {
            return factoryMethod.invoke();
        }
        if (parameterTypes.length == 1) {
            try{
                return factoryMethod.invoke(JSONObject.parseObject(param, parameterTypes[0]));
            }catch (Exception e){
                throw new FlowException(String.format("[%s(%s)] 参数初始化失败：%s", factoryMethod.getMethod().getName(), parameterTypes[0], param));
            }
        }
        throw new FlowException(String.format("[%s] 方法参数不能超过1个", factoryMethod.getMethod().getName()));
    }
}
