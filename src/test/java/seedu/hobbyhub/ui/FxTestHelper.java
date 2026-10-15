package seedu.hobbyhub.ui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javafx.application.Platform;

/** Runs JavaFX assertions on the JavaFX Application Thread. */
public final class FxTestHelper {

    static {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException ignored) {
            // The toolkit was started by another UI test.
        }
        Platform.setImplicitExit(false);
    }

    private FxTestHelper() {}

    /**
     * Runs the supplied action on the JavaFX Application Thread and returns its result.
     */
    public static <T> T callOnFxThread(Supplier<T> action) {
        if (Platform.isFxApplicationThread()) {
            return action.get();
        }

        CountDownLatch completion = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<RuntimeException> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                result.set(action.get());
            } catch (RuntimeException e) {
                failure.set(e);
            } finally {
                completion.countDown();
            }
        });

        try {
            completion.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }

        if (failure.get() != null) {
            throw failure.get();
        }
        return result.get();
    }

    /**
     * Runs the supplied action on the JavaFX Application Thread.
     */
    public static void runOnFxThread(Runnable action) {
        callOnFxThread(() -> {
            action.run();
            return null;
        });
    }
}
