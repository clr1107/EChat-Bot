package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalCharacterCountInfraction;
import pw.rayz.echat.punishment.implementations.IllegalWordInfraction;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import javax.annotation.Nonnull;

public class ChatListener extends ListenerAdapter {
    private final MessageFilter filter;

    public ChatListener(MessageFilter filter) {
        this.filter = filter;
    }

    private boolean testMessage(Member member, Message message, TextChannel channel) {
        Punishment punishment = null;

        if (filter.checkForSpam(member))
            punishment = new SpamInfraction(channel, message.getContentRaw(), member);

        if (punishment == null && filter.checkForLength(message))
            punishment = new IllegalCharacterCountInfraction(channel, member);

        if (punishment == null && filter.checkForCaps(message))
            punishment = new SpamInfraction(channel, message.getContentRaw(), member);

        if (punishment == null) {
            String bannedWord = filter.checkForBannedWords(message);

            if (bannedWord != null)
                punishment = new IllegalWordInfraction(channel, bannedWord, member);
        }

        if (punishment != null) {
            punishment.sendAudit();
            punishment.send();

            return false;
        } else return true;
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if (member == null || member.getUser().isBot())
            return;

        if (!testMessage(event.getMember(), message, event.getChannel()))
            message.delete().queue();
        else filter.registerSentMessage(member);
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if (member == null || member.getUser().isBot())
            return;

        if (!testMessage(event.getMember(), message, event.getChannel()))
            message.delete().queue();
        else filter.registerSentMessage(member);
    }
}
