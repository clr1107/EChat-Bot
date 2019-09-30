package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.afk.AFKInstance;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.implementations.IllegalChannelChatInfraction;
import pw.rayz.echat.punishment.implementations.IllegalCharacterCountInfraction;
import pw.rayz.echat.punishment.implementations.IllegalWordInfraction;
import pw.rayz.echat.punishment.implementations.SpamInfraction;

import javax.annotation.Nonnull;
import java.util.List;

public class ChatListener extends ListenerAdapter {
    private final JDABot bot;

    public ChatListener(JDABot bot) {
        this.bot = bot;
    }

    private void performAFKChecks(Message message) {
        JDABot bot = EChat.eChat().getBot();
        TextChannel channel = message.getTextChannel();
        Member member = message.getMember();

        if (member == null)
            return;

        if (bot.getAfkHandler().isAFK(member)) {
            bot.getAfkHandler().disableAFK(member);
            channel.sendMessage("I have removed your AFK status, " + member.getEffectiveName()).queue();
        }
    }

    private void replyTaggedAFKMembers(Message message) {
        List<Member> mentioned = message.getMentionedMembers();
        StringBuilder builder = new StringBuilder();

        for (Member mentionedMember : mentioned) {
            AFKInstance instance = bot.getAfkHandler().getAFKInstance(mentionedMember);

            if (mentionedMember != null && instance != null) {
                String msg = "%s is afk: %s (time: %s)";
                msg = String.format(
                        msg, mentionedMember.getEffectiveName(), instance.getReason(), instance.timeSinceAFK()
                );

                builder.append(msg).append(". ");
            }
        }

        String msg = builder.toString();
        if (!msg.isBlank())
            message.getChannel().sendMessage(msg).queue();
    }

    private boolean applyMessageFilterPunishments(Member member, Message message, TextChannel channel) {
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

    private void onMsg(Message message) {
        Member member = message.getMember();
        TextChannel channel = message.getTextChannel();

        if (!bot.isGuildChannel(message.getTextChannel()))
            return;

        if (member == null || member.getUser().isBot())
            return;

        if (applyMessageFilterPunishments(member, message, channel)) {
            performAFKChecks(message);
            replyTaggedAFKMembers(message);
            bot.getMessageFilter().testForHooks(message);

            bot.getMessageFilter().registerSentMessage(member);
        } else
            message.delete().queue();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        onMsg(event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        onMsg(event.getMessage());
    }
}
