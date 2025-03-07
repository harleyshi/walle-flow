package com.walle.engine.core.builder;


public final class Builders {

    public static SimpleEngineBuilder newSimpleEngine(String name, String version) {
        return new SimpleEngineBuilder(name, version);
    }

    public static AsyncEngineBuilder newAsyncEngine(String name, String version) {
        return new AsyncEngineBuilder(name, version);
    }
}
