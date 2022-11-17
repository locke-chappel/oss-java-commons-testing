package com.github.lc.oss.commons.testing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public abstract class AbstractFactory {
    protected <T> T getField(String fieldName, Object instance) {
        return TestUtil.getField(fieldName, instance);
    }

    protected <T> T getField(Field field, Object instance) {
        return TestUtil.getField(field, instance);
    }

    protected void setField(String fieldName, Object value, Object instance) {
        TestUtil.setField(fieldName, value, instance);
    }

    protected void setField(Field field, Object value, Object instance) {
        TestUtil.setField(field, value, instance);
    }

    protected Field findField(String fieldName, Class<?> clazz) {
        return TestUtil.findField(fieldName, clazz);
    }

    protected Method findMethod(String methodName, Class<?> clazz) {
        return TestUtil.findMethod(methodName, clazz);
    }

    protected void waitFor(int millis) {
        TestUtil.waitFor(millis);
    }

    protected void waitUntil(Supplier<Boolean> condition) {
        TestUtil.waitUntil(condition);
    }

    protected void waitUntil(Supplier<Boolean> condition, long maxWait) {
        TestUtil.waitUntil(condition, maxWait);
    }

    protected void waitUntil(Supplier<Boolean> condition, long maxWait, long interval) {
        TestUtil.waitUntil(condition, maxWait, interval);
    }
}
