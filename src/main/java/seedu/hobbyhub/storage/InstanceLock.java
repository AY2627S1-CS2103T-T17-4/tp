package seedu.hobbyhub.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.util.FileUtil;

/**
 * An exclusive operating system lock on the data file of HobbyHub, used to prevent two instances of the
 * application from modifying the same data at the same time.
 * The lock is released by {@link #close()}, or by the operating system if the application terminates abnormally.
 * A reference to the lock must be kept for as long as it should be held.
 */
public class InstanceLock implements AutoCloseable {

    private static final String LOCK_FILE_SUFFIX = ".lock";

    private static final Logger logger = LogsCenter.getLogger(InstanceLock.class);

    private final FileChannel channel;
    private final FileLock lock;

    private InstanceLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /**
     * Tries to acquire the lock guarding {@code dataFile}.
     * The lock is held on a separate file with the same name as {@code dataFile} plus {@code .lock}, which is
     * created if missing.
     *
     * @param dataFile location of the data file to guard. Cannot be null.
     * @return the acquired lock, or {@code Optional.empty()} if another instance already holds it.
     * @throws IOException if the lock file could not be created or locked.
     */
    public static Optional<InstanceLock> tryAcquire(Path dataFile) throws IOException {
        requireNonNull(dataFile);

        Path lockPath = dataFile.resolveSibling(dataFile.getFileName() + LOCK_FILE_SUFFIX);
        FileUtil.createIfMissing(lockPath);

        FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.WRITE);
        FileLock lock = null;
        try {
            lock = channel.tryLock();
        } catch (OverlappingFileLockException ofle) {
            logger.fine("Lock on " + lockPath + " is already held within this process.");
        } finally {
            if (lock == null) {
                channel.close();
            }
        }

        return lock == null ? Optional.empty() : Optional.of(new InstanceLock(channel, lock));
    }

    /**
     * Releases the lock. The lock file itself is deliberately left in place, as deleting it could race with
     * another instance that is starting up.
     *
     * @throws IOException if the lock could not be released.
     */
    @Override
    public void close() throws IOException {
        try {
            lock.release();
        } finally {
            channel.close();
        }
    }

}
