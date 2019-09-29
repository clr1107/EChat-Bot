package pw.rayz.echat.chat.afk;

import java.time.Instant;

public class AFKInstance {
    private final long id;
    private final Instant instant;
    private final String previousNickname;
    private final String msg;

    public AFKInstance(long id, Instant instant, String previousNickname, String msg) {
        this.id = id;
        this.instant = instant;
        this.previousNickname = previousNickname;
        this.msg = msg;
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

    public String getMsg() {
        return msg;
    }
}
