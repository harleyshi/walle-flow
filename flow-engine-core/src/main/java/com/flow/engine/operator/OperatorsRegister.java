package com.flow.engine.operator;


import com.flow.engine.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 算子定义注册器
 * @author harley.shi
 * @date 2024/6/29
 */
public class OperatorsRegister {

    private static final OperatorsRegister INSTANCE = new OperatorsRegister();

    private final Map<String, OperatorDefinition> operatorDefineMap = new HashMap<>();

    private OperatorsRegister() {

    }

    public static OperatorsRegister getInstance() {
        return INSTANCE;
    }

    public void register(String operator, OperatorDefinition invoker) {
        this.operatorDefineMap.put(operator, invoker);
    }

    public OperatorDefinition getOperatorDefine(String operator) {
        OperatorDefinition operatorDefine = this.operatorDefineMap.get(operator);
        AssertUtil.notNull(operatorDefine, String.format("[%s] 算子不存在或未定义", operator));
        return operatorDefine;
    }

    public boolean containsKey(String operator) {
        return this.operatorDefineMap.containsKey(operator);
    }
}
