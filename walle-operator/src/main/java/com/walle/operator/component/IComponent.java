package com.walle.operator.component;

import com.walle.operator.FlowCtx;

public interface IComponent<C extends FlowCtx, O>{

    O execute(C ctx);
}
