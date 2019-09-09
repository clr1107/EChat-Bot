package pw.rayz.echat.questionable;

import net.dv8tion.jda.api.entities.*;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;

import java.time.OffsetDateTime;
import java.util.logging.Logger;

public class QuestionableContent {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Member questioner;
    private final Message message;
    private final OffsetDateTime questioned;
    private MessageChannel channel;

    public QuestionableContent(Member questioner, Message message, OffsetDateTime questioned) {
        this.questioner = questioner;
        this.message = message;
        this.questioned = questioned;

        this.loadChannel(true);
    }

    private void loadChannel(boolean first) {
        Configuration configuration = eChat.getConfig();

        if (first)
            configuration.addLoadTask(() -> this.loadChannel(false));

        String channelId = configuration.getString("channels.questionable", false);
        GuildChannel channel = eChat.getBot().getJDA().getGuildChannelById(channelId);

        if (channel instanceof TextChannel) {
            this.channel = (TextChannel) channel;
        }
    }

    public void add() {
        if (channel != null) {
            MessageEmbed embed = new ContentEmbedBuilder(this).build().build();
            channel.sendMessage(embed).queue();

            logger.info("Added new Questionable Content from: " + questioner.getUser().getName());
        }
    }

    public Member getQuestioner() {
        return questioner;
    }

    public Message getMessage() {
        return message;
    }

    public OffsetDateTime getQuestioned() {
        return questioned;
    }
}
