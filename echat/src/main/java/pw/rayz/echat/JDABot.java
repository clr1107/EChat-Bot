package pw.rayz.echat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.rayz.echat.limits.BannedWordListener;
import pw.rayz.echat.limits.CharacterLimitListener;
import pw.rayz.echat.limits.SelfieChannelListener;
import pw.rayz.echat.limits.SpamFilterListener;
import pw.rayz.echat.listeners.PrivateMessageListener;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class JDABot {
    private final EChat eChat = EChat.eChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private JDA jda;
    private String guildId;
    private String logChannelId;

    JDABot() {
        this.jda = loadJDA(eChat.getConfig().getString("token", false));

        eChat.getConfig().addLoadTask(this::loadConfiguration, true);
        loadListeners();
    }

    private void loadConfiguration() {
        guildId = eChat.getConfig().getString("guild_id", null, false);
        logChannelId = eChat.getConfig().getString("channels.log", null, false);
    }

    private void loadListeners() {
        addListener(new SelfieChannelListener());
        addListener(new BannedWordListener());
        addListener(new PrivateMessageListener());
        addListener(new CharacterLimitListener());
        addListener(new SpamFilterListener());
    }

    private JDA loadJDA(String token) {
        JDA jda = null;

        try {
            jda = new JDABuilder(token)
                    .setAutoReconnect(true)
                    .build();

            jda.awaitReady();
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

    public boolean isInGuild(@Nullable User user) {
        if (user != null && guildId != null) {
            Guild guild = jda.getGuildById(guildId);
            return guild != null && guild.isMember(user);
        } else return false;
    }

    public boolean isGuildChannel(@Nullable GuildChannel channel) {
        if (channel != null && guildId != null) {
            Guild guild = jda.getGuildById(guildId);
            return guild != null && channel.getGuild().getId().equals(guildId);
        } else return false;
    }

    @Nullable
    public TextChannel getLogChannel() {
        if (guildId != null && logChannelId != null) {
            Guild guild = jda.getGuildById(guildId);
            return guild != null ? guild.getTextChannelById(logChannelId) : null;
        } else return null;
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
}
