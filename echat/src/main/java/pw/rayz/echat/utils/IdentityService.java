package pw.rayz.echat.utils;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IdentityService {
    private static final IdentityService service = new IdentityService();
    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();

    private IdentityService() {
    }

    public static IdentityService getService() {
        return service;
    }

    private void newUUID() {
        UUID uuid = UUID.randomUUID();

        queue.offer(uuid.getLeastSignificantBits());
        queue.offer(uuid.getMostSignificantBits());
    }

    public long nextId() {
        Long id;

        if ((id = queue.poll()) == null) {
            newUUID();
            id = nextId();
        }

        return id;
    }
}
