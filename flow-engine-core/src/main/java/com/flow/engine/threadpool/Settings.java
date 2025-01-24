package com.flow.engine.threadpool;

import java.util.*;

/**
 * 配置
 */
public class Settings {

    public static final Settings EMPTY = new Builder().build();

    public static Builder builder() {
        return new Builder();
    }

    private Map<String, String> map;

    private Settings(Map<String, String> settings) {
        this.map = settings;
    }

    /**
     * The settings as a flat {@link Map}.
     * @return an unmodifiable map of settings
     */
    public Map<String, String> getAsMap() {
        return Collections.unmodifiableMap(this.map);
    }

    /**
     * 获取指定属性
     * @param name
     * @return
     */
    public String getString(String name) {
        return getPropOrDefault(name, null);
    }

    /**
     * 获取指定属性
     * @param name
     * @return
     */
    public String get(String name) {
        return getString(name);
    }

    /**
     * 获取指定属性
     * @param name
     * @param defaultValue
     * @return
     */
    public String getPropOrDefault(String name, String defaultValue) {
        return this.map.getOrDefault(name, defaultValue);
    }

    /**
     * A settings that are filtered (and key is removed) with the specified prefix.
     */
    public Settings getByPrefix(String prefix) {
        Builder builder = new Builder();
        for (Map.Entry<String, String> entry : getAsMap().entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getKey().length() < prefix.length()) {
                    // ignore this. one
                    continue;
                }
                builder.put(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * @return  The direct keys of this settings
     */
    public Set<String> names() {
        Set<String> names = new HashSet<>();
        for (String key : map.keySet()) {
            int i = key.indexOf(".");
            if (i < 0) {
                names.add(key);
            } else {
                names.add(key.substring(0, i));
            }
        }
        return names;
    }

    /**
     * settings builder
     */
    public static class Builder {

        private Map<String, String> map = new HashMap<>();

        /**
         * Puts tuples of key value pairs of settings. Simplified version instead of repeating calling
         * put for each one.
         */
        public Builder put(Object... settings) {
            if (settings.length == 1) {
                // support cases where the actual type gets lost down the road...
                if (settings[0] instanceof Map) {
                    //noinspection unchecked
                    return put((Map) settings[0]);
                } else if (settings[0] instanceof Settings) {
                    return put((Settings) settings[0]);
                }
            }
            if ((settings.length % 2) != 0) {
                throw new RuntimeException("array settings of key + value order doesn't hold correct number of arguments (" + settings.length + ")");
            }
            for (int i = 0; i < settings.length; i++) {
                put(settings[i++].toString(), settings[i].toString());
            }
            return this;
        }

        /**
         * Sets a setting with the provided setting key and value.
         *
         * @param key   The setting key
         * @param value The setting value
         * @return The builder
         */
        public Builder put(String key, String value) {
            map.put(key, value);
            return this;
        }

        /**
         * Sets all the provided settings.
         */
        public Builder put(Settings settings) {
            map.putAll(settings.getAsMap());
            return this;
        }

        /**
         * Sets all the provided settings.
         */
        public Builder put(Map<String, String> settings) {
            map.putAll(settings);
            return this;
        }

        public Settings build() {
            return new Settings(Collections.unmodifiableMap(this.map));
        }
    }

}
