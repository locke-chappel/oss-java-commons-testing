package io.github.lc.oss.commons.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

public abstract class AbstractMockTest extends AbstractTest {
    private MockitoSession mockito;

    @BeforeEach
    public void before() {
        this.mockito = Mockito.mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking();
    }

    @AfterEach
    public void after() {
        this.mockito.finishMocking();
    }
}
