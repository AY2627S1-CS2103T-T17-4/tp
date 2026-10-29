package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class InstanceLockTest {

    private static final String LOCK_FILE_SUFFIX = ".lock";

    @TempDir
    public Path testFolder;

    @Test
    public void tryAcquire_nullDataFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> InstanceLock.tryAcquire(null));
    }

    @Test
    public void tryAcquire_freshPath_acquiresLockAndCreatesLockFile() throws Exception {
        Path dataFile = testFolder.resolve("hobbyhub.json");

        try (InstanceLock lock = InstanceLock.tryAcquire(dataFile).get()) {
            assertTrue(Files.exists(lockFileOf(dataFile)));
        }
    }

    @Test
    public void tryAcquire_lockAlreadyHeld_returnsEmpty() throws Exception {
        Path dataFile = testFolder.resolve("hobbyhub.json");

        try (InstanceLock firstLock = InstanceLock.tryAcquire(dataFile).get()) {
            Optional<InstanceLock> secondLock = InstanceLock.tryAcquire(dataFile);
            assertFalse(secondLock.isPresent());
        }
    }

    @Test
    public void tryAcquire_afterClose_acquiresLockAgain() throws Exception {
        Path dataFile = testFolder.resolve("hobbyhub.json");

        InstanceLock.tryAcquire(dataFile).get().close();

        try (InstanceLock secondLock = InstanceLock.tryAcquire(dataFile).get()) {
            assertTrue(Files.exists(lockFileOf(dataFile)));
        }
    }

    @Test
    public void tryAcquire_differentDataFiles_locksAreIndependent() throws Exception {
        Path firstDataFile = testFolder.resolve("first.json");
        Path secondDataFile = testFolder.resolve("second.json");

        try (InstanceLock firstLock = InstanceLock.tryAcquire(firstDataFile).get();
                InstanceLock secondLock = InstanceLock.tryAcquire(secondDataFile).get()) {
            assertTrue(Files.exists(lockFileOf(firstDataFile)));
            assertTrue(Files.exists(lockFileOf(secondDataFile)));
        }
    }

    @Test
    public void tryAcquire_missingParentDirectory_createsDirectoryAndAcquiresLock() throws Exception {
        Path dataFile = testFolder.resolve("nested").resolve("data").resolve("hobbyhub.json");

        try (InstanceLock lock = InstanceLock.tryAcquire(dataFile).get()) {
            assertTrue(Files.exists(lockFileOf(dataFile)));
        }
    }

    @Test
    public void close_leavesLockFileInPlace() throws Exception {
        Path dataFile = testFolder.resolve("hobbyhub.json");

        InstanceLock.tryAcquire(dataFile).get().close();

        assertTrue(Files.exists(lockFileOf(dataFile)));
    }

    /**
     * Returns the path of the lock file guarding {@code dataFile}.
     */
    private Path lockFileOf(Path dataFile) {
        return dataFile.resolveSibling(dataFile.getFileName() + LOCK_FILE_SUFFIX);
    }

}
