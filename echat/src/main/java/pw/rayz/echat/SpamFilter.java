package pw.rayz.echat;

import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpamFilter {
    private Map<Long, Instant> previousMessage = new ConcurrentHashMap<>();

    public SpamFilter() {

    }

    public void sent(Member member) {
        previousMessage.put(member.getIdLong(), Instant.now());
    }

    public boolean canSend(Member member) {
        Instant previous = previousMessage.get(member.getIdLong());
        return previous != null && previous.plusMillis(250).isAfter(Instant.now());
    }
}
