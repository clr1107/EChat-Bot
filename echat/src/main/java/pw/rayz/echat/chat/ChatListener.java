package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;
import pw.rayz.echat.punishment.implementations.IllegalCharacterCountInfraction;
import pw.rayz.echat.punishment.implementations.IllegalWordInfraction;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ChatListener extends ListenerAdapter {
    private final JDABot bot;

    public ChatListener(JDABot bot) {
        this.bot = bot;
    }

    private void testAFK(Member member, Message message) {
        JDABot bot = EChat.eChat().getBot();

        if (bot.getAFKMap().containsKey(member.getIdLong())) {
            bot.getAFKMap().remove(member.getIdLong());
            message.getChannel().sendMessage("Removed your afk, " + member.getEffectiveName()).queue();

            return;
        }

        Map<Long, String> afk = bot.getAFKMap();
        List<Member> mentionedMembers = message.getMentionedMembers();
        StringBuilder msg = new StringBuilder();

        for (Member mem : mentionedMembers) {
            long id = mem.getIdLong();
            if (!afk.containsKey(id))
                continue;

            msg.append(" ");
            msg.append(mem.getEffectiveName()).append(" is afk: ").append(afk.get(id)).append(".");
        }

        String send = msg.toString();
        if (!send.isBlank())
            message.getChannel().sendMessage(msg.toString()).queue();
    }

    private boolean testMessage(Member member, Message message, TextChannel channel) {
        Punishment punishment = null;
        MessageFilter filter = bot.getMessageFilter();

        if (filter.checkForSpam(member))
            punishment = new SpamInfraction(channel, message.getContentRaw(), member);

        if (punishment == null && filter.isIllegalChannel(message))
            punishment = new IllegalChannelChatInfraction(channel, member);

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

        if (!bot.isGuildChannel(message.getTextChannel()))
            return;

        if (member == null || member.getUser().isBot())
            return;

        if (!testMessage(event.getMember(), message, event.getChannel()))
            message.delete().queue();
        else {
            testAFK(member, message);
            bot.getMessageFilter().registerSentMessage(member);
        }
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        Message message = event.getMessage();
        Member member = event.getMember();

        if (!bot.isGuildChannel(message.getTextChannel()))
            return;

        if (member == null || member.getUser().isBot())
            return;

        if (!testMessage(event.getMember(), message, event.getChannel()))
            message.delete().queue();
        else bot.getMessageFilter().registerSentMessage(member);
    }
}
