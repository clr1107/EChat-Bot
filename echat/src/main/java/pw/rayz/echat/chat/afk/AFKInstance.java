package pw.rayz.echat.chat.afk;

import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public class AFKInstance {
    private final long id;
    private final Instant instant;
    private final String previousNickname;
    private final String reason;

    public AFKInstance(long id, @Nonnull Instant instant, @Nullable String previousNickname, @Nonnull String reason) {
        this.id = id;
        this.instant = instant;
        this.previousNickname = previousNickname;
        this.reason = reason;
    }

    /**
     * Format a string in HH:mm:ss of how long this AFKInstance has been 'active'.
     *
     * @return String formatted in HH:mm:ss, e.g. 01:45:13 representing 1 hour, 45 mins, 13 seconds.
     */
    public String timeSinceAFK() {
        long milliseconds = (System.currentTimeMillis() - instant.toEpochMilli());
        return DurationFormatUtils.formatDuration(milliseconds, "HH:mm:ss");
    }

    public long getId() {
        return id;
    }

    public Instant getInstant() {
        return instant;
    }

    /**
     * Member's previous nickname before it was changed.
     *
     * @return {@link String} previous nickname, or {@code null} if they did not have one.
     */
    @Nullable
    public String getPreviousNickname() {
        return previousNickname;
    }

    public String getReason() {
        return reason;
    }
}
