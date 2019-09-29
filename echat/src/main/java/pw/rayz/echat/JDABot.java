package pw.rayz.echat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.rayz.echat.chat.ChatListener;
import pw.rayz.echat.chat.MessageFilter;
import pw.rayz.echat.commands.CommandHandler;
import pw.rayz.echat.commands.implementation.AFKCommand;
import pw.rayz.echat.listeners.PrivateMessageListener;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class JDABot {
    private final EChat eChat;
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private JDA jda;
    private MessageFilter messageFilter;
    private CommandHandler commandHandler;
    private String guildId;
    private String logChannelId;
    private final Map<Long, String> afkMap = new HashMap<>();

    JDABot(EChat eChat) {
        this.eChat = eChat;
        this.jda = loadJDA(eChat.getConfig().getString("token", false));

        eChat.getConfig().addLoadTask(this::loadConfiguration, true);
    }

    void load() {
        logger.info("Calling JDABot#load - loading listeners & other necessary functions.");

        messageFilter = new MessageFilter(this);
        commandHandler = new CommandHandler(this);

        loadListeners();
        loadCommands();
    }

    private void loadConfiguration() {
        guildId = eChat.getConfig().getString("guild_id", null, false);
        logChannelId = eChat.getConfig().getString("channels.log", null, false);
    }

    private void loadListeners() {
        addListener(new ChatListener(this));
        addListener(new PrivateMessageListener(this));
        addListener(commandHandler);
    }

    private void loadCommands() {
        commandHandler.registerCommand(new AFKCommand(this));
    }

    private JDA loadJDA(String token) {
        JDA jda = null;

        try {
            jda = new JDABuilder(token)
                    .setAutoReconnect(true)
                    .build();

            jda.awaitReady();
            logger.info("Connected to E-Chat server");
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }

        return jda;
    }

    public void addListener(@NotNull EventListener listener) {
        jda.addEventListener(listener);
        logger.info("Added event listener: " + listener.getClass().getName());
    }

    public boolean awaitReady() {
        try {
            jda.awaitReady();
            return true;
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            logger.severe("Cannot wait until JDA is ready.");

            return false;
        }
    }

    @Nullable
    public Guild getEChatGuild() {
        return guildId != null ? jda.getGuildById(guildId) : null;
    }

    public boolean isInGuild(@Nullable User user) {
        Guild guild = getEChatGuild();

        if (user != null && guild != null)
            return guild.isMember(user);
        else return false;
    }

    public boolean isGuildChannel(@Nullable GuildChannel channel) {
        Guild guild = getEChatGuild();

        if (channel != null && guild != null)
            return channel.getGuild().getId().equals(guildId);
        else return false;
    }

    @Nullable
    public TextChannel getLogChannel() {
        Guild guild = getEChatGuild();

        if (guild != null && logChannelId != null)
            return guild.getTextChannelById(logChannelId);
        else return null;
    }

    @Nullable
    public Role getGuildRole(String id) {
        Guild guild = getEChatGuild();

        if (guild != null && id != null)
            return guild.getRoleById(id);
        else return null;
    }

    public MessageFilter getMessageFilter() {
        return messageFilter;
    }

    public EChat getEChat() {
        return eChat;
    }

    public JDA getJDA() {
        return jda;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public Map<Long, String> getAFKMap() {
        return afkMap;
    }
}
