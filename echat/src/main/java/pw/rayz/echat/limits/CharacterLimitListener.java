package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalCharacterCountInfraction;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class CharacterLimitListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Configuration config = eChat.getConfig();
    private int characterLimit;

    public CharacterLimitListener() {
        config.addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        characterLimit = config.getInt("limits.characters", 800, false);
    }

    private boolean passJudgement(Message message) {
        return message.getContentRaw().length() >= characterLimit;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();

        if (!eChat.getBot().isGuildChannel(channel))
            return;

        if (member != null && passJudgement(event.getMessage())) {
            logger.info(member.getUser().getName() + " Broke character limit");

            Punishment punishment = new IllegalCharacterCountInfraction(channel, member);

            punishment.send();
            punishment.sendAudit();
            event.getMessage().delete().queue();
        }
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();

        if (!eChat.getBot().isGuildChannel(channel))
            return;

        if (member != null && passJudgement(event.getMessage())) {
            logger.info(member.getUser().getName() + " Broke character limit");

            Punishment punishment = new IllegalCharacterCountInfraction(channel, member);

            punishment.send();
            punishment.sendAudit();
            event.getMessage().delete().queue();
        }
    }
}
