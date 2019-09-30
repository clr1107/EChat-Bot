package pw.rayz.echat.chat.afk;

import java.time.Instant;
import java.time.LocalTime;

public class AFKInstance {
    private final long id;
    private final Instant instant;
    private final String previousNickname;
    private final String reason;

    public AFKInstance(long id, Instant instant, String previousNickname, String reason) {
        this.id = id;
        this.instant = instant;
        this.previousNickname = previousNickname;
        this.reason = reason;
    }

    public String timeSinceAFK() {
        long seconds = (System.currentTimeMillis() - instant.toEpochMilli()) / 1000;
        return LocalTime.MIN.plusSeconds(seconds).toString();
    }

    public long getId() {
        return id;
    }

    public Instant getInstant() {
        return instant;
    }

    public String getPreviousNickname() {
        return previousNickname;
    }

    public String getReason() {
        return reason;
    }
}
