package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SelfieChannelListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Configuration config = eChat.getConfig();
    private List<String> selfieChannels = new ArrayList<>();

    public SelfieChannelListener() {
        config.addLoadTask(this::loadSelfieChannels, true);
    }

    private void loadSelfieChannels() {
        List<String> list = config.getField("channels.selfies", ArrayList.class, false);

        if (list != null)
            selfieChannels = list;
    }

    private boolean testAndPunish(TextChannel channel, Member member, Message message) {
        if (member == null || !eChat.getBot().isGuildChannel(channel))
            return false;

        if (member.getUser().isBot() || !message.getAttachments().isEmpty())
            return false;

        if (selfieChannels.contains(channel.getId())) {
            logger.info(member.getUser().getName() + " Talking in selfie channel");

            Punishment punishment = new IllegalChannelChatInfraction(channel, member);

            punishment.send();
            punishment.sendAudit();
            return true;
        }

        return false;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Message message = event.getMessage();

        if (testAndPunish(channel, member, message))
            event.getMessage().delete().queue();
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Message message = event.getMessage();

        if (testAndPunish(channel, member, message))
            event.getMessage().delete().queue();
    }
}
