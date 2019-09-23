package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.implementations.IllegalWordInfraction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BannedWordListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Configuration config = eChat.getConfig();
    private List<String> bannedWords = new ArrayList<>();

    public BannedWordListener() {
        config.addLoadTask(this::loadBannedWords, true);
    }

    private void loadBannedWords() {
        List<String> list = config.getField("banned_words", ArrayList.class, false);

        if (list != null)
            bannedWords = list;
    }

    private String matches(String message) {
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        String[] words = message.split(" ");

        for (String word : words) {
            double perc = word.length() >= 6 ? 0.85 : 0.7;
            boolean matches = bannedWords.parallelStream()
                    .anyMatch(test -> similarity.apply(word, test.toLowerCase()) >= perc);

            if (matches)
                return word;
        }

        return null;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getMessage().getChannel();
        Member member = event.getMember();
        String message = event.getMessage().getContentRaw();
        String matchingWord;

        if (member != null && (matchingWord = matches(message)) != null) {
            logger.info(member.getUser().getName() + " Said banned word: " + matchingWord);

            new IllegalWordInfraction(channel, matchingWord).send(member);
            event.getMessage().delete().queue();
        }
    }
}
