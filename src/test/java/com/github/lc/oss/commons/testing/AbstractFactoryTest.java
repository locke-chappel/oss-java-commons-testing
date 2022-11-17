package com.github.lc.oss.commons.testing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class AbstractFactoryTest extends AbstractTest {
    private static class TestClass {
        private Object member;

        public Object obj;

        Object getMember() {
            return this.member;
        }
    }

    private static class TestDerived extends TestClass {
        @SuppressWarnings("unused")
        private Object derivedOnly;
    }

    private static class TestFactory extends AbstractFactory {
    }

    @Test
    public void test_waitUntil_defaultTimeout() {
        TestFactory factory = new TestFactory();

        final long start = System.currentTimeMillis();

        factory.waitUntil(() -> System.currentTimeMillis() - 10 > start);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            factory.waitUntil(() -> false, -1);
            Assertions.fail("Expected exception");
        } catch (AssertionFailedError ex) {
            after = System.currentTimeMillis();
            Assertions.assertEquals("Waited too long, aborting ==> expected: <true> but was: <false>", ex.getMessage());
            long total = after - before;
            Assertions.assertTrue(total >= 1000, "Test aborted too sonn, total wait time: " + total + "ms");
            Assertions.assertTrue(total < 1200, "Test waited too long before aborting, total wait time: " + total + "ms");
        }
    }

    @Test
    public void test_waitUntil_customTimeout() {
        TestFactory factory = new TestFactory();

        final long start = System.currentTimeMillis();

        factory.waitUntil(() -> System.currentTimeMillis() - 10 > start, 50);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            factory.waitUntil(() -> false, 50);
            Assertions.fail("Expected exception");
        } catch (AssertionFailedError ex) {
            after = System.currentTimeMillis();
            Assertions.assertEquals("Waited too long, aborting ==> expected: <true> but was: <false>", ex.getMessage());
            long total = after - before;
            Assertions.assertTrue(total >= 50, "Test aborted too sonn, total wait time: " + total + "ms");
            Assertions.assertTrue(total < 200, "Test waited too long before aborting, total wait time: " + total + "ms");
        }
    }

    @Test
    public void test_waitUntil_customTimeout_customInterval() {
        TestFactory factory = new TestFactory();

        final long start = System.currentTimeMillis();

        factory.waitUntil(() -> System.currentTimeMillis() - 10 > start, 50, 5);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            factory.waitUntil(() -> false, 50);
            Assertions.fail("Expected exception");
        } catch (AssertionFailedError ex) {
            after = System.currentTimeMillis();
            Assertions.assertEquals("Waited too long, aborting ==> expected: <true> but was: <false>", ex.getMessage());
            long total = after - before;
            Assertions.assertTrue(total >= 50, "Test aborted too sonn, total wait time: " + total + "ms");
            Assertions.assertTrue(total < 200, "Test waited too long before aborting, total wait time: " + total + "ms");
        }
    }

    @Test
    public void test_findField() {
        TestFactory factory = new TestFactory();

        Field fieldC = factory.findField("member", TestClass.class);
        Assertions.assertNotNull(fieldC);

        Field fieldD = factory.findField("member", TestDerived.class);
        Assertions.assertNotNull(fieldD);
        Assertions.assertEquals(fieldC, fieldD);
    }

    @Test
    public void test_findField_notFound() {
        TestFactory factory = new TestFactory();

        Field field = factory.findField("junk", Object.class);
        Assertions.assertNull(field);
    }

    @Test
    public void test_findMethod() {
        TestFactory factory = new TestFactory();

        Method methodC = factory.findMethod("getMember", TestClass.class);
        Assertions.assertNotNull(methodC);

        Method methodD = factory.findMethod("getMember", TestDerived.class);
        Assertions.assertNotNull(methodD);
        Assertions.assertEquals(methodC, methodD);
    }

    @Test
    public void test_setAndGetField_byName() {
        TestFactory factory = new TestFactory();

        TestClass c = new TestClass();
        TestDerived d = new TestDerived();

        Assertions.assertNull(c.getMember());
        Assertions.assertNull(c.obj);
        Assertions.assertNull(d.getMember());

        factory.setField("obj", Integer.MAX_VALUE, c);
        factory.setField("member", Boolean.TRUE, c);
        factory.setField("member", Boolean.FALSE, d);

        Assertions.assertEquals(Integer.MAX_VALUE, c.obj);
        Assertions.assertEquals(Boolean.TRUE, c.getMember());
        Assertions.assertEquals(Boolean.FALSE, d.getMember());

        Assertions.assertEquals(Integer.MAX_VALUE, (Integer) factory.getField("obj", c));
        Assertions.assertEquals(Boolean.TRUE, factory.getField("member", c));
        Assertions.assertEquals(Boolean.FALSE, factory.getField("member", d));
    }

    @Test
    public void test_setAndGetField_byField() {
        TestFactory factory = new TestFactory();

        TestClass c = new TestClass();
        TestDerived d = new TestDerived();

        Assertions.assertNull(c.getMember());
        Assertions.assertNull(c.obj);
        Assertions.assertNull(d.getMember());

        Field objField = this.findField("obj", c.getClass());
        Field memberFieldC = this.findField("member", c.getClass());
        Field memberFieldD = this.findField("member", d.getClass());

        factory.setField(objField, Integer.MAX_VALUE, c);
        factory.setField(memberFieldC, Boolean.TRUE, c);
        factory.setField(memberFieldD, Boolean.FALSE, d);

        Assertions.assertEquals(Integer.MAX_VALUE, c.obj);
        Assertions.assertEquals(Boolean.TRUE, c.getMember());
        Assertions.assertEquals(Boolean.FALSE, d.getMember());

        Assertions.assertEquals(Integer.MAX_VALUE, (Integer) factory.getField(objField, c));
        Assertions.assertEquals(Boolean.TRUE, factory.getField(memberFieldC, c));
        Assertions.assertEquals(Boolean.FALSE, factory.getField(memberFieldD, d));
    }

    @Test
    public void test_waitFor_millis() {
        TestFactory factory = new TestFactory();

        final long start = System.currentTimeMillis();

        factory.waitUntil(() -> System.currentTimeMillis() - 10 > start);

        long before = -1;
        long after = -1;
        before = System.currentTimeMillis();
        factory.waitFor(1000);
        after = System.currentTimeMillis();
        long total = after - before;
        Assertions.assertTrue(total >= 1000, "Test aborted too sonn, total wait time: " + total + "ms");
        Assertions.assertTrue(total < 1200, "Test waited too long before aborting, total wait time: " + total + "ms");
    }
}
