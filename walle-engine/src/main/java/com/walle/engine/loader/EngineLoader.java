package com.walle.engine.loader;

import com.walle.engine.domain.model.FlowDSL;

import java.util.List;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
public interface EngineLoader {
    /**
     * load engine list
     */
    List<FlowDSL> loadPublishedEngines();

    /**
     * load all engine list
     */
    List<FlowDSL> loadAllEngineList();

    /**
     * load engine by name
     * @param engineName engine name
     */
    FlowDSL getEngineByName(String engineName);
}
