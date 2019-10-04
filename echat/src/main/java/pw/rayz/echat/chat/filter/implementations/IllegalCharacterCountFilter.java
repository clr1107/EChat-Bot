package pw.rayz.echat.chat.filter.implementations;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.chat.MessageAuthority;
import pw.rayz.echat.chat.filter.MessageFilter;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalCharacterCountInfraction;

public class IllegalCharacterCountFilter implements MessageFilter {
    private final MessageAuthority authority;
    private int charLimit;

    public IllegalCharacterCountFilter(MessageAuthority authority) {
        this.authority = authority;
        authority.getBot().getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = authority.getBot().getEChat().getConfig();

        charLimit = config.getInt("limits.characters", 800, false);
    }

    /**
     * Return whether this message should be filtered. The conditions are:
     * - they are not immune (this method is never called).
     * - the message isn't null & the member isn't null.
     * - their message's length is greater than or equal to the character limit.
     *
     * @param message {@link Message} to check.
     * @return {@link Punishment} or {@code null}.
     */
    @Override
    public Punishment checkMessage(@NotNull Message message) {
        if (message == null || message.getMember() == null)
            return null;

        String raw = message.getContentRaw();

        if (raw.length() >= charLimit) {
            message.delete().queue();
            return new IllegalCharacterCountInfraction(message.getTextChannel(), message.getMember());
        }

        return null;
    }
}
