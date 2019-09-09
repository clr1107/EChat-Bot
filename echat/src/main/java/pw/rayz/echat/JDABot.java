package pw.rayz.echat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.limits.SelfieChannelListener;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class JDABot {
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private JDA jda;

    JDABot() {
        this.jda = loadJDA(EChat.eChat().getConfig().getString("token", false));

        loadListeners();
    }

    private void loadListeners() {
        addListener(new SelfieChannelListener());
    }

    private JDA loadJDA(String token) {
        JDA jda = null;

        try {
            jda = new JDABuilder(token)
                    .setAutoReconnect(true)
                    .build();
        } catch (LoginException exception) {
            exception.printStackTrace();
        }

        return jda;
    }

    public void addListener(@NotNull EventListener listener) {
        jda.addEventListener(listener);
        logger.info("Added event: " + listener.getClass().getName());
    }

    public boolean awaitReady() {
        try {
            jda.awaitReady();
            return true;
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            logger.severe("Could not wait until JDA is ready.");

            return false;
        }
    }

    public JDA getJDA() {
        return jda;
    }
}
