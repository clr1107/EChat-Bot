package pw.rayz.echat.limits;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import javax.annotation.Nonnull;

public class SpamFilterListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();

    private boolean testAndPunish(Member member, TextChannel channel, String message) {
        if (member == null || member.getUser().isBot() || !eChat.getBot().isGuildChannel(channel))
            return false;

        if (!eChat.getBot().getSpamFilter().passesTests(member, message)) {
            Punishment punishment = new SpamInfraction(channel, message, member);

            punishment.send();
            punishment.sendAudit(); // won't send audit.

            return true;
        } else {
            eChat.getBot().getSpamFilter().sent(member);
            return false;
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        String message = event.getMessage().getContentRaw();

        if (testAndPunish(member, channel, message))
            event.getMessage().delete().queue();
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        Member member = event.getMember();
        TextChannel channel = event.getChannel();
        String message = event.getMessage().getContentRaw();

        if (testAndPunish(member, channel, message))
            event.getMessage().delete().queue();
    }
}
