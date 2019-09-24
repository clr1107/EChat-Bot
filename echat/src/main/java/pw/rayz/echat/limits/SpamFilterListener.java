package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import javax.annotation.Nonnull;

public class SpamFilterListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        String message = event.getMessage().getContentRaw();

        if (member == null || !eChat.getBot().isGuildChannel(channel))
            return;

        if (!eChat.getSpamFilter().passesTests(member, message)) {
            Punishment punishment = new SpamInfraction(channel, message, member);

            punishment.send();
            punishment.sendAudit();

            event.getMessage().delete().queue();
        } else eChat.getSpamFilter().sent(member);
    }
}
