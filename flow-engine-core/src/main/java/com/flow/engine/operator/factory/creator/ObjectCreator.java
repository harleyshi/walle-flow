package com.flow.engine.operator.factory.creator;

/**
 * @author harley.shi
 * @date 2024/7/1
 */
public interface ObjectCreator {

    /**
     * Create an instance with specific type.
     *
     * @since 1.0.5
     * @param type       type
     * @param expectType expect java type.
     * @param useCache   useCache
     * @return           instance.
     */
    <T> T create(String type, Class<T> expectType, boolean useCache);
}
