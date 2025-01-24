package com.walle.operator.component;

import com.walle.operator.FlowCtx;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 起始组件，用于启动流程
 * @author harley.shi
 * @date 2024/7/1
 */
@Slf4j
@Getter
@Setter
public class StartComponent<C extends FlowCtx, O> implements IComponent<C, O> {

    @Override
    public O execute(C ctx) {
        return null;
    }
}
