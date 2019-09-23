package pw.rayz.echat.utils;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A simple singleton designed to generate random longs for IDs. Rather
 * than using an entire {@link UUID}, simply split it in half, so it
 * can be stored in two {@code long} types.
 */
public class IdentityService {
    private static final IdentityService service = new IdentityService();
    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();

    private IdentityService() {
    }

    public static IdentityService getService() {
        return service;
    }

    /**
     * Generate a new {@link UUID} and {@link Queue#offer(Object)} both halfs
     * to the {@link Queue}.
     */
    private void newUUID() {
        UUID uuid = UUID.randomUUID();

        queue.offer(uuid.getLeastSignificantBits());
        queue.offer(uuid.getMostSignificantBits());
    }

    /**
     * Take one {@code long} from the Queue if it's available. If not, then
     * call {@link this#newUUID()} to add two more {@code long} types to the queue.
     *
     * @return {@code long} random id.
     */
    public long nextId() {
        Long id;

        if ((id = queue.poll()) == null) {
            newUUID();
            id = nextId();
        }

        return id;
    }

    public String nextHexString() {
        return Long.toHexString(nextId());
    }

    public String nextOctalString() {
        return Long.toOctalString(nextId());
    }
}
