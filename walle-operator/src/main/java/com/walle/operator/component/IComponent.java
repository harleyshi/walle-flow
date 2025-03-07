package com.walle.operator.component;

import com.walle.operator.FlowCtx;

public interface IComponent<C extends FlowCtx, O>{

    String name();

    O execute(C ctx);
}
