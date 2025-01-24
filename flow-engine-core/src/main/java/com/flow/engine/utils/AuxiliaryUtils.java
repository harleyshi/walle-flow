package com.flow.engine.utils;


/**
 * @author harley.shi
 * @date 2024/7/1
 */
public final class AuxiliaryUtils {

    public static <T> T or(T t, T defaultValue) {
        return t != null ? t : defaultValue;
    }

    public static String or(String t, String defaultValue) {
        return t != null && t.trim().length() > 0 ? t : defaultValue;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static Class<?> asClass(String typename) {
        if (isBlank(typename)) {
            return null;
        }
        try {
            return Class.forName(typename, false, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            return null;
        }
    }
}
