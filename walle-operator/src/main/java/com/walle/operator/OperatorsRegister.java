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

    private final Map<String, OperatorDef<?, ?>> operatorHolderMap = new HashMap<>();

    private OperatorsRegister() {

    }

    public static OperatorsRegister getInstance() {
        return INSTANCE;
    }

    public <C extends FlowCtx, O> void register(OperatorDef<C, O> operatorDef) {
        String key = getKey(operatorDef.getName(), operatorDef.getVersion());
        this.operatorHolderMap.put(key, operatorDef);
    }

    @SuppressWarnings("unchecked")
    public <C extends FlowCtx, O> Operator<C, O> getOperator(String name, String version) {
        String key = getKey(name, version);
        OperatorDef<C, O> operatorDef = (OperatorDef<C, O>) this.operatorHolderMap.get(key);
        AssertUtil.notNull(operatorDef, String.format("[%s] 算子不存在或未定义", key));
        return operatorDef.getOperator();
    }

    public List<OperatorDef<?, ?>> operatorData() {
        return new ArrayList<>(operatorHolderMap.values());
    }

    public boolean containsKey(String name) {
        return this.operatorHolderMap.containsKey(name);
    }

    private String getKey(String name, String version){
        return name + "-" + version;
    }
}
