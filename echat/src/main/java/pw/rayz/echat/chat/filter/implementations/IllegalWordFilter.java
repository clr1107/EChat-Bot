package pw.rayz.echat.chat.filter.implementations;

import net.dv8tion.jda.api.entities.Message;
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

    @Override
    public Punishment checkMessage(Message message) {
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
