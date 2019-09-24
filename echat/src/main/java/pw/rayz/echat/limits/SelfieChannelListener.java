package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;

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

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();

        if (!eChat.getBot().isGuildChannel(channel))
            return;

        if (member != null && selfieChannels.contains(channel.getId())) {
            logger.info(member.getUser().getName() + " Talking in selfie channel");

            Punishment punishment = new IllegalChannelChatInfraction(channel, member);

            punishment.send();
            punishment.sendAudit();

            event.getMessage().delete().queue();
        }
    }
}
