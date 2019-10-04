package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.afk.AFKHandler;
import pw.rayz.echat.chat.filter.MessageFilter;
import pw.rayz.echat.chat.filter.implementations.IllegalChannelFilter;
import pw.rayz.echat.chat.filter.implementations.IllegalCharacterCountFilter;
import pw.rayz.echat.chat.filter.implementations.IllegalWordFilter;
import pw.rayz.echat.chat.filter.implementations.SpamFilter;
import pw.rayz.echat.chat.hooks.ChatHook;
import pw.rayz.echat.chat.hooks.implementations.*;
import pw.rayz.echat.punishment.Punishment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageAuthority {
    private final JDABot bot;
    private final MessageLogger logger;
    private final AFKHandler afkHandler = new AFKHandler();
    private final Set<MessageFilter> filters;
    private final Set<ChatHook> chatHooks = new HashSet<>();
    private List<String> immuneRoles;

    public MessageAuthority(JDABot bot) {
        this.bot = bot;
        this.filters = new HashSet<>();
        this.logger = new MessageLogger();

        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);

        loadFilters();
        loadChatHooks();
    }

    private void loadConfig() {
        Configuration config = bot.getEChat().getConfig();

        immuneRoles = (List<String>) config.getField("roles.message_filter_bypass", new ArrayList<>(), ArrayList.class, false);
    }

    private void loadFilters() {
        addFilter(new SpamFilter(this));
        addFilter(new IllegalChannelFilter(this));
        addFilter(new IllegalWordFilter(this));
        addFilter(new IllegalCharacterCountFilter(this));
    }

    private void loadChatHooks() {
        addChatHook(new BloodHook(bot));
        addChatHook(new DatingHook(bot));
        addChatHook(new MockingHook(bot));
        addChatHook(new AFKHook(bot));
        addChatHook(new AFKMentionHook(bot));
    }

    private boolean applyFilters(Message message) {
        if (isImmune(message.getMember()))
            return false;

        return filters.parallelStream().anyMatch(filter -> {
            Punishment punishment = filter.checkMessage(message);

            if (punishment != null) {
                punishment.send();
                punishment.sendAudit();

                return true;
            }

            return false;
        });
    }

    public void addChatHook(ChatHook hook) {
        chatHooks.add(hook);
        bot.getEChat().getLogger().info("Added chat hook: " + hook.getClass().getName());
    }

    public void addFilter(MessageFilter filter) {
        filters.add(filter);
        bot.getEChat().getLogger().info("Added chat filter: " + filter.getClass().getName());
    }

    public void executeHooks(Message message) {
        Member member = message.getMember();

        if (member == null)
            return;

        String user = message.getMember().getEffectiveName();

        chatHooks.parallelStream().forEach(h -> {
            if (h.matches(message)) {
                bot.getEChat().getLogger().info("Chat Hook " + h.getClass().getName() + " tripped by: " + user);
                h.messageCatch(message);
            }
        });
    }

    public void userSendMessage(Message message) {
        Member member = message.getMember();

        if (member == null)
            return;

        if (!applyFilters(message)) {
            logger.registerSentMessage(member, message);
            executeHooks(message);
        }
    }

    public void userDeleteMessage(long msgId) {
        logger.registerDeletedMessage(msgId);
    }

    public boolean isImmune(Member member) {
        List<Role> roles = immuneRoles.stream().map(bot::getGuildRole).collect(Collectors.toList());
        return roles.stream().anyMatch(r -> member.getRoles().contains(r));
    }

    public AFKHandler getAFKHandler() {
        return afkHandler;
    }

    public JDABot getBot() {
        return bot;
    }

    public MessageLogger getLogger() {
        return logger;
    }
}
