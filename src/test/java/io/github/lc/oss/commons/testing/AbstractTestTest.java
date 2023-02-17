package io.github.lc.oss.commons.testing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class AbstractTestTest extends AbstractTest {
    private static class TestClass {
        private static Object staticMember;

        private Object member;

        public Object obj;

        Object getMember() {
            return this.member;
        }

        static Object getStaticMember() {
            return TestClass.staticMember;
        }
    }

    private static class TestDerived extends TestClass {
        @SuppressWarnings("unused")
        private Object derivedOnly;
    }

    private static class TestTest extends AbstractTest {

    }

    @Test
    public void test_waitUntil_defaultTimeout() {
        AbstractTest test = new TestTest();

        final long start = System.currentTimeMillis();

        test.waitUntil(() -> System.currentTimeMillis() - 10 > start);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            test.waitUntil(() -> false, -1);
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
        AbstractTest test = new TestTest();

        final long start = System.currentTimeMillis();

        test.waitUntil(() -> System.currentTimeMillis() - 10 > start, 50);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            test.waitUntil(() -> false, 50);
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
        AbstractTest test = new TestTest();

        final long start = System.currentTimeMillis();

        test.waitUntil(() -> System.currentTimeMillis() - 10 > start, 50, 5);

        long before = -1;
        long after = -1;
        try {
            before = System.currentTimeMillis();
            test.waitUntil(() -> false, 50);
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
        Field fieldC = this.findField("member", TestClass.class);
        Assertions.assertNotNull(fieldC);

        Field fieldD = this.findField("member", TestDerived.class);
        Assertions.assertNotNull(fieldD);
        Assertions.assertEquals(fieldC, fieldD);
    }

    @Test
    public void test_findField_notFound() {
        Field field = this.findField("junk", Object.class);
        Assertions.assertNull(field);
    }

    @Test
    public void test_findMethod() {
        Method methodC = this.findMethod("getMember", TestClass.class);
        Assertions.assertNotNull(methodC);

        Method methodD = this.findMethod("getMember", TestDerived.class);
        Assertions.assertNotNull(methodD);
        Assertions.assertEquals(methodC, methodD);
    }

    @Test
    public void test_setAndGetField_byName() {
        TestClass c = new TestClass();
        TestDerived d = new TestDerived();

        Assertions.assertNull(c.getMember());
        Assertions.assertNull(c.obj);
        Assertions.assertNull(d.getMember());
        Assertions.assertNull(TestClass.getStaticMember());

        this.setField("obj", Integer.MAX_VALUE, c);
        this.setField("member", Boolean.TRUE, c);
        this.setField("member", Boolean.FALSE, d);
        this.setField("staticMember", Double.MAX_VALUE, TestClass.class);

        Assertions.assertEquals(Integer.MAX_VALUE, c.obj);
        Assertions.assertEquals(Boolean.TRUE, c.getMember());
        Assertions.assertEquals(Boolean.FALSE, d.getMember());
        Assertions.assertEquals(Double.MAX_VALUE, TestClass.getStaticMember());

        Assertions.assertEquals(Integer.MAX_VALUE, (Integer) this.getField("obj", c));
        Assertions.assertEquals(Boolean.TRUE, this.getField("member", c));
        Assertions.assertEquals(Boolean.FALSE, this.getField("member", d));
        Assertions.assertEquals(Double.MAX_VALUE, this.getField("staticMember", TestClass.class));

    }

    @Test
    public void test_setAndGetField_byField() {
        TestClass c = new TestClass();
        TestDerived d = new TestDerived();

        Assertions.assertNull(c.getMember());
        Assertions.assertNull(c.obj);
        Assertions.assertNull(d.getMember());

        Field objField = this.findField("obj", c.getClass());
        Field memberFieldC = this.findField("member", c.getClass());
        Field memberFieldD = this.findField("member", d.getClass());

        this.setField(objField, Integer.MAX_VALUE, c);
        this.setField(memberFieldC, Boolean.TRUE, c);
        this.setField(memberFieldD, Boolean.FALSE, d);

        Assertions.assertEquals(Integer.MAX_VALUE, c.obj);
        Assertions.assertEquals(Boolean.TRUE, c.getMember());
        Assertions.assertEquals(Boolean.FALSE, d.getMember());

        Assertions.assertEquals(Integer.MAX_VALUE, (Integer) this.getField(objField, c));
        Assertions.assertEquals(Boolean.TRUE, this.getField(memberFieldC, c));
        Assertions.assertEquals(Boolean.FALSE, this.getField(memberFieldD, d));
    }

    @Test
    public void test_setField_error() {
        TestClass c = new TestClass();
        Field filed = this.findField("derivedOnly", TestDerived.class);

        try {
            this.setField(filed, Integer.MAX_VALUE, c);
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assertions.assertEquals("object is not an instance of io.github.lc.oss.commons.testing.AbstractTestTest$TestDerived", ex.getCause().getMessage());
        }
    }

    @Test
    public void test_getField_error() {
        TestClass c = new TestClass();
        Field filed = this.findField("derivedOnly", TestDerived.class);

        try {
            this.getField(filed, c);
            Assertions.fail("Expected exception");
        } catch (RuntimeException ex) {
            Assertions.assertTrue(ex.getCause() instanceof IllegalArgumentException);
            Assertions.assertEquals("object is not an instance of io.github.lc.oss.commons.testing.AbstractTestTest$TestDerived", ex.getCause().getMessage());
        }
    }

    @Test
    public void test_waitFor_millis() {
        AbstractTest test = new TestTest();

        final long start = System.currentTimeMillis();

        test.waitUntil(() -> System.currentTimeMillis() - 10 > start);

        long before = -1;
        long after = -1;
        before = System.currentTimeMillis();
        test.waitFor(1000);
        after = System.currentTimeMillis();
        long total = after - before;
        Assertions.assertTrue(total >= 1000, "Test aborted too sonn, total wait time: " + total + "ms");
        Assertions.assertTrue(total < 1200, "Test waited too long before aborting, total wait time: " + total + "ms");
    }
}
