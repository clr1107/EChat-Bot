package pw.rayz.echat.chat.filter.implementations;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.chat.MessageAuthority;
import pw.rayz.echat.chat.filter.MessageFilter;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import java.time.Instant;

public class SpamFilter implements MessageFilter {
    private final MessageAuthority authority;
    private int millis;

    public SpamFilter(MessageAuthority authority) {
        this.authority = authority;
        authority.getBot().getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = authority.getBot().getEChat().getConfig();

        millis = config.getInt("limits.spam_millis", 0, false);
    }

    /**
     * Return whether this message should be filtered. The conditions are:
     * - they are not immune (this method is never called).
     * - the message isn't null & the member isn't null.
     * - their last message was sent less than or equal to 250 milliseconds ago (1/4 of a second).
     *
     * @param message {@link Message} to check.
     * @return {@link Punishment} or {@code null}.
     */
    @Override
    public Punishment checkMessage(@NotNull Message message) {
        if (message == null || message.getMember() == null)
            return null;

        Instant previousInstant = authority.getLogger().lastMessage(message.getMember());

        if (previousInstant != null && previousInstant.plusMillis(millis).isAfter(Instant.now())) {
            message.delete().queue();

            return new SpamInfraction(
                    message.getTextChannel(), message.getContentRaw(), message.getMember()
            );
        }

        return null;
    }
}
