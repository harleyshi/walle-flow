package com.flow.engine.operator;


import com.flow.engine.operator.factory.OperatorFactory;
import com.flow.engine.operator.factory.builder.OperatorGenerate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 算子函数，用于标记在方法上
 * @author harley.shi
 * @date 2024/7/1
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface ComponentFn {

    String name() default "";

    Class<? extends OperatorFactory> builder() default OperatorGenerate.class;

}
