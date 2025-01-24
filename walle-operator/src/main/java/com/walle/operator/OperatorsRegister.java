package com.walle.operator;


import com.walle.operator.node.Operator;
import com.walle.operator.utils.AssertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 算子定义注册器
 * @author harley.shi
 * @date 2024/6/29
 */
public class OperatorsRegister {

    private static final OperatorsRegister INSTANCE = new OperatorsRegister();

    private final Map<String, OperatorHolder<?, ?>> operatorHolderMap = new HashMap<>();

    private OperatorsRegister() {

    }

    public static OperatorsRegister getInstance() {
        return INSTANCE;
    }

    public <C extends FlowCtx, O> void register(String name, OperatorHolder<C, O> operatorHolder) {
        this.operatorHolderMap.put(name, operatorHolder);
    }

    @SuppressWarnings("unchecked")
    public <C extends FlowCtx, O> Operator<C, O> getOperator(String name) {
        OperatorHolder<C, O> operatorHolder = (OperatorHolder<C, O>) this.operatorHolderMap.get(name);
        AssertUtil.notNull(operatorHolder, String.format("[%s] 算子不存在或未定义", name));
        return operatorHolder.getOperator();
    }

    public List<OperatorHolder<?, ?>> operatorData() {
        return new ArrayList<>(operatorHolderMap.values());
    }

    public boolean containsKey(String name) {
        return this.operatorHolderMap.containsKey(name);
    }
}
