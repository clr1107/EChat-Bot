package pw.rayz.echat.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.MessageAuthority;

import javax.annotation.Nonnull;

public class ChatListener extends ListenerAdapter {
    private final MessageAuthority authority;

    public ChatListener(MessageAuthority authority) {
        this.authority = authority;
    }

    private void msgReceived(Message message) {
        Member member = message.getMember();

        if (member == null || member.getUser().isBot())
            return;

        authority.registerUserSentMessage(message); // do all filters, hooks, logging etc.
    }

    private void msgDeleted(long msgId) {
        authority.registerUserDeletedMessage(msgId);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        JDABot bot = EChat.eChat().getBot();
        Guild guild = event.getGuild();

        if (!bot.getGuildId().equals(guild.getId()))
            return;

        msgReceived(event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        JDABot bot = EChat.eChat().getBot();
        Guild guild = event.getGuild();

        if (!bot.getGuildId().equals(guild.getId()))
            return;

        msgReceived(event.getMessage());
    }

    @Override
    public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event) {
        JDABot bot = EChat.eChat().getBot();
        Guild guild = event.getGuild();

        if (!bot.getGuildId().equals(guild.getId()))
            return;

        msgDeleted(event.getMessageIdLong());
    }
}
