package pw.rayz.echat.chat.filter.implementations;

import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.chat.MessageAuthority;
import pw.rayz.echat.chat.filter.MessageFilter;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;

import java.util.ArrayList;
import java.util.List;

public class IllegalChannelFilter implements MessageFilter {
    private final MessageAuthority authority;
    private List<String> selfieChannelIds;

    public IllegalChannelFilter(MessageAuthority authority) {
        this.authority = authority;
        authority.getBot().getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = authority.getBot().getEChat().getConfig();

        selfieChannelIds = (ArrayList<String>) config.getField("channels.selfies", ArrayList.class, false);
    }

    @Override
    public Punishment checkMessage(Message message) {
        if (message == null || message.getMember() == null)
            return null;

        String channelId = message.getTextChannel().getId();

        if (selfieChannelIds.contains(channelId)) {
            message.delete().queue();
            return new IllegalChannelChatInfraction(message.getTextChannel(), message.getMember());
        }

        return null;
    }
}
