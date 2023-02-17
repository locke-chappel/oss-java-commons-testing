package io.github.lc.oss.commons.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractTestThreadingTest extends AbstractTest {
    private static class ThreadHelper implements Runnable {
        private Thread parent;

        public ThreadHelper(Thread parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Test was interrupted unexpectedly", ex);
            }
            this.parent.interrupt();
        }
    }

    @Test
    public void test_interrupt() {
        Thread t = new Thread(new ThreadHelper(Thread.currentThread()));
        t.start();

        try {
            this.waitUntil(() -> false, TestUtil.DEFAULT_MAX_WAIT, 1000);
            Assertions.fail("Expected exception");
        } catch (AssertionError ex) {
            Assertions.assertEquals("Interrupted while waiting for condition.", ex.getMessage());
        }
    }
}
