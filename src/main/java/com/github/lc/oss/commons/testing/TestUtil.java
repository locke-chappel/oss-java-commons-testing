package com.github.lc.oss.commons.testing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

public class TestUtil {
    public static final long DEFAULT_INTERVAL = 10;
    public static final long DEFAULT_MAX_WAIT = 1000;

    protected static <T> T getField(String fieldName, Object instance) {
        Assertions.assertNotNull(instance, "Instance cannot be null");
        Field field = TestUtil.findField(fieldName, instance.getClass());
        Assertions.assertNotNull(field, String.format("Unable to locate '%s' filed.", fieldName));
        return TestUtil.getField(field, instance);
    }

    protected static <T> T getField(String fieldName, Class<?> clazz) {
        Field field = TestUtil.findField(fieldName, clazz);
        Assertions.assertNotNull(field, String.format("Unable to locate '%s' filed.", fieldName));
        return TestUtil.getField(field, null);
    }

    @SuppressWarnings("unchecked")
    protected static <T> T getField(Field field, Object instance) {
        try {
            boolean isAccessible = field.canAccess(instance);
            if (!isAccessible) {
                field.setAccessible(true);
            }
            Object value = field.get(instance);
            if (!isAccessible) {
                field.setAccessible(false);
            }
            return (T) value;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static void setField(String fieldName, Object value, Object instance) {
        Assertions.assertNotNull(instance, "Instance cannot be null");
        Field field = TestUtil.findField(fieldName, instance.getClass());
        Assertions.assertNotNull(field, String.format("Unable to locate '%s' filed.", fieldName));
        TestUtil.setField(field, value, instance);
    }

    protected static void setField(String fieldName, Object value, Class<?> clazz) {
        Field field = TestUtil.findField(fieldName, clazz);
        Assertions.assertNotNull(field, String.format("Unable to locate '%s' filed.", fieldName));
        TestUtil.setField(field, value, null);
    }

    protected static void setField(Field field, Object value, Object instance) {
        try {
            boolean isAccessible = field.canAccess(instance);
            if (!isAccessible) {
                field.setAccessible(true);
            }
            field.set(instance, value);
            if (!isAccessible) {
                field.setAccessible(false);
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static Field findField(String fieldName, Class<?> clazz) {
        Field found = Arrays.stream(clazz.getDeclaredFields()). //
                filter(f -> f.getName().equals(fieldName)). //
                findAny(). //
                orElse(null);
        if (found == null && clazz.getSuperclass() != null) {
            found = TestUtil.findField(fieldName, clazz.getSuperclass());
        }
        return found;
    }

    protected static Method findMethod(String methodName, Class<?> clazz) {
        Method found = Arrays.stream(clazz.getDeclaredMethods()). //
                filter(m -> m.getName().equals(methodName)). //
                findAny(). //
                orElse(null);
        if (found == null) {
            found = TestUtil.findMethod(methodName, clazz.getSuperclass());
        }
        return found;
    }

    protected static void waitFor(int millis) {
        final long until = System.currentTimeMillis() + millis;
        TestUtil.waitUntil(() -> System.currentTimeMillis() >= until, millis + 50);
    }

    protected static void waitUntil(Supplier<Boolean> condition) {
        TestUtil.waitUntil(condition, TestUtil.DEFAULT_MAX_WAIT);
    }

    protected static void waitUntil(Supplier<Boolean> condition, long maxWait) {
        TestUtil.waitUntil(condition, maxWait, TestUtil.DEFAULT_INTERVAL);
    }

    protected static void waitUntil(Supplier<Boolean> condition, long maxWait, long interval) {
        final long waitAtMost = maxWait < 0 ? TestUtil.DEFAULT_MAX_WAIT : maxWait;
        final long stopAfter = System.currentTimeMillis() + waitAtMost;
        boolean success = false;
        while (!(success = condition.get()) && System.currentTimeMillis() < stopAfter) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                throw new AssertionFailedError("Interrupted while waiting for condition.");
            }
        }
        Assertions.assertTrue(success, "Waited too long, aborting");
    }

    private TestUtil() {
    }
}
