package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
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
//            double perc = word.length() >= 6 ? 0.85 : 0.7;
            double perc = 0.87;
            boolean matches = bannedWords.parallelStream()
                    .anyMatch(test -> similarity.apply(word, test.toLowerCase()) >= perc);

            if (matches)
                return word;
        }

        return null;
    }

    private boolean testAndPunish(TextChannel channel, Member member, String message) {
        String matchingWord;

        if (!eChat.getBot().isGuildChannel(channel))
            return false;

        if (member != null && (matchingWord = matches(message)) != null) {
            logger.info(member.getUser().getName() + " Said banned word: " + matchingWord);

            Punishment punishment = new IllegalWordInfraction(channel, matchingWord, member);

            punishment.send();
            punishment.sendAudit();
            return true;
        }

        return false;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        String message = event.getMessage().getContentRaw();

        if (testAndPunish(channel, member, message))
            event.getMessage().delete().queue();
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        String message = event.getMessage().getContentRaw();

        if (testAndPunish(channel, member, message))
            event.getMessage().delete().queue();
    }
}
