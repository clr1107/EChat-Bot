package pw.rayz.echat;

import net.dv8tion.jda.api.entities.Member;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpamFilter {
    private final EChat eChat = EChat.eChat();
    private final Map<Long, Instant> previousMessage = new ConcurrentHashMap<>();
    private double capsPerc;

    public SpamFilter() {
        eChat.getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        capsPerc = eChat.getConfig().getDouble("caps_percentage", 100D, false);
    }

    public void sent(Member member) {
        previousMessage.put(member.getIdLong(), Instant.now());
    }

    public boolean canSend(Member member) {
        Instant previous = previousMessage.get(member.getIdLong());
        return previous == null || Instant.now().isAfter(previous.plusMillis(250));
    }

    public boolean testForCaps(String message) {
        long upper = message.chars().mapToObj((c) -> (char) c).filter(Character::isUpperCase).count();
        return (((double) upper / (double) message.length()) * 100) >= capsPerc;
    }

    public boolean passesTests(Member member, String message) {
        return canSend(member) && !testForCaps(message);
    }
}
