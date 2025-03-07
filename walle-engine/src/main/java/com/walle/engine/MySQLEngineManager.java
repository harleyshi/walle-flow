package com.walle.engine;

import com.walle.engine.domain.model.FlowDSL;
import com.walle.engine.core.Engine;
import com.walle.engine.core.executor.GraphExecutor;
import com.walle.engine.core.parser.DSLParser;
import com.walle.engine.loader.EngineLoader;
import com.walle.operator.FlowCtx;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
public class MySQLEngineManager implements EngineManager {
    private final DAGEngineRegister dagEngineRegister = DAGEngineRegister.getInstance();

    private final DSLParser dslParser;

    private final EngineLoader engineLoader;

    public MySQLEngineManager(EngineLoader engineLoader) {
        this.dslParser = new DSLParser();
        this.engineLoader = engineLoader;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkForUpdates, 10, 3, TimeUnit.SECONDS);
    }

    @Override
    public void load() {
        List<FlowDSL> flowList = engineLoader.loadPublishedEngines();
        for (FlowDSL flowDSL : flowList) {
            Engine<FlowCtx> dagEngine = dslParser.parse(flowDSL);
            // 注册DAG引擎
            dagEngineRegister.register(dagEngine);
        }
    }

    @Override
    public GraphExecutor<FlowCtx> getEngineExecutor(String engineName) {
        Engine<FlowCtx> engine = Objects.requireNonNull(dagEngineRegister.getEngine(engineName),
                String.format("engine %s not found", engineName));
        return engine.buildExecutor();
    }

    private void checkForUpdates() {
        List<FlowDSL> flowList = engineLoader.loadAllEngineList();
        for (FlowDSL flowDSL : flowList) {
            Engine<FlowCtx> engine = dagEngineRegister.getEngine(flowDSL.getName());
            // 版本没发生变化，不需要更新
            if(engine != null && flowDSL.getVersion().equals(engine.version())){
                return;
            }
            Engine<FlowCtx> dagEngine = dslParser.parse(flowDSL);
            // 注册DAG引擎
            dagEngineRegister.register(dagEngine);
        }
    }
}