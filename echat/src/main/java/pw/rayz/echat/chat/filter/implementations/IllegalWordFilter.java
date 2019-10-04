package pw.rayz.echat.chat.filter.implementations;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.chat.MessageAuthority;
import pw.rayz.echat.chat.filter.MessageFilter;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalWordInfraction;

import java.util.ArrayList;
import java.util.List;

public class IllegalWordFilter implements MessageFilter {
    private final MessageAuthority authority;
    private List<String> words;

    public IllegalWordFilter(MessageAuthority authority) {
        this.authority = authority;
        authority.getBot().getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = authority.getBot().getEChat().getConfig();

        words = (ArrayList<String>) config.getField("banned_words", ArrayList.class, false);
    }

    /**
     * Return whether this message should be filtered. The conditions are:
     * - they are not immune (this method is never called).
     * - the message isn't null & the member isn't null.
     * - one or more of their words (delimited by a space) is in the prohibited words list.
     *
     * @param message {@link Message} to check.
     * @return {@link Punishment} or {@code null}.
     */
    @Override
    public Punishment checkMessage(@NotNull Message message) {
        if (message == null || message.getMember() == null)
            return null;

        String raw = message.getContentRaw();
        String[] parts = raw.split(" ");

        for (String word : parts) {
            if (words.contains(word.toLowerCase())) {
                message.delete().queue();

                return new IllegalWordInfraction(
                        message.getTextChannel(), word, message.getMember()
                );
            }
        }

        return null;
    }
}
