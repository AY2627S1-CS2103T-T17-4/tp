package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FxTestHelperTest {

    @Test
    public void callOnFxThread_success_returnsResult() {
        assertEquals("result", FxTestHelper.callOnFxThread(() -> "result"));
    }

    @Test
    public void runOnFxThread_runtimeException_rethrowsOriginalException() {
        RuntimeException failure = new IllegalStateException("Test failure");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> FxTestHelper.runOnFxThread(() -> {
            throw failure;
        }));

        assertSame(failure, thrown);
    }

    @Test
    public void runOnFxThread_assertionError_rethrowsOriginalError() {
        AssertionError failure = new AssertionError("Test assertion failure");

        AssertionError thrown = assertThrows(AssertionError.class, () -> FxTestHelper.runOnFxThread(() -> {
            throw failure;
        }));

        assertSame(failure, thrown);
    }
}
