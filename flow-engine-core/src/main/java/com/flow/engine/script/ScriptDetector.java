package com.flow.engine.script;


import com.flow.engine.utils.AssertUtil;
import com.flow.engine.utils.AuxiliaryUtils;
import com.flow.engine.exception.FlowException;

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harley.shi
 * @date 2024/7/1
 */
public class ScriptDetector {

    /**
     * The phrase for {@link ScriptExecutor} location.
     */
    private static final String LOCATION = "META-INF/flow-engine-script";

    /**
     * The stored phrase for {@link ScriptExecutor}
     */
    private final Map<String, Class<?>> javaTypes = new ConcurrentHashMap<>();

    /**
     * The singleton instance for detector.
     */
    private static final ScriptDetector DETECTOR = new ScriptDetector();

    public static ScriptDetector get() {
        return DETECTOR;
    }

    private ScriptDetector() {
        try {
            Enumeration<URL> resources = this.getClass().getClassLoader().getResources(LOCATION);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                // phrase files must be properties.
                Properties properties = new Properties();
                properties.load(url.openStream());

                // key : phrase, value: full java classname.
                Set<String> keys = properties.stringPropertyNames();
                for (String key : keys) {
                    AssertUtil.notBlank(key, "script key must not be blank");
                    String type = properties.getProperty(key);
                    AssertUtil.notBlank(type, String.format("script %s type must not be blank", key));
                    Class<?> javaType = AuxiliaryUtils.asClass(type);
                    AssertUtil.notNull(javaType, String.format("script %s type must be a javaType", key));
                    AssertUtil.isTrue(ScriptExecutor.class.isAssignableFrom(javaType), String.format("script %s type must be a subclass of ScriptExecutor", key));
                    AssertUtil.isTrue(ScriptExecutor.class != (javaType), String.format("script %s type must be a subclass of ScriptExecutor", key));
                    javaTypes.put(key.toLowerCase(), javaType);
                }
            }
        } catch (Exception e) {
            throw new FlowException(e);
        }

    }

    public Class<?> getJavaType(String type) {
        AssertUtil.notBlank(type, "type must not blank");
        return javaTypes.get(type.trim().toLowerCase());
    }
}
