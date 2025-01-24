package com.walle.engine;

import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
public class DAGEngineSpringInitializer implements SmartInitializingSingleton{

    private final EngineManager engineManager;

    public DAGEngineSpringInitializer(EngineManager engineManager) {
        this.engineManager = engineManager;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // init dag engine
        engineManager.load();
    }
}
