package com.walle.operator;

import com.walle.operator.common.enums.NodeType;
import java.lang.annotation.*;

/**
 * 算子节点注解
 * @author harley.shi
 * @date 2024/7/1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentFn {

    /**
     * 节点名称
     */
    String name() default "";

    /**
     * 节点类型
     */
    NodeType type() default NodeType.STANDARD;

    /**
     * 节点版本号
     */
    String version() default "1.0.0";
}
