package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SelfieChannelListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private List<String> selfieChannels = new ArrayList<>();

    public SelfieChannelListener() {
        loadSelfieChannels(true);
    }

    private void loadSelfieChannels(boolean first) {
        Configuration config = eChat.getConfig();

        if (first)
            config.addLoadTask(() -> this.loadSelfieChannels(false));

        List<String> list = config.getField("channels.selfies", ArrayList.class, false);
        if (list != null)
            selfieChannels = list;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getMessage().getChannel();
        Member member = event.getMember();

        if (selfieChannels.contains(channel.getId()) && member != null) {
            logger.info(member.getUser().getName() + " Talking in selfie channel");

            new IllegalChannelChatInfraction(channel).send(member);
            event.getMessage().delete().queue();
        }
    }
}
