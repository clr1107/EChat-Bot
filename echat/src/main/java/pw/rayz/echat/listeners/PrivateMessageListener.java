package pw.rayz.echat.listeners;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.utils.EmbedBuilderTemplate;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PrivateMessageListener extends ListenerAdapter {
    private final JDABot bot;
    private final Map<Long, Instant> previouslySent = new HashMap<>();
    private String message;

    public PrivateMessageListener(JDABot bot) {
        this.bot = bot;

        bot.getEChat().getConfig().addLoadTask(this::loadMessage, true);
    }

    private void loadMessage() {
        Configuration config = bot.getEChat().getConfig();

        message = config.getString("standard_messages.private_response", "Hi!", false);
    }

    private void sendResponse(User user) {
        MessageEmbed embed = new EmbedBuilderTemplate()
                .apply(EmbedBuilderTemplate.EmbedType.BASIC)
                .builder()
                .addField("Response", message, false)
                .addField("GitHub", "https://github.com/clr1107/echat-bot", false)
                .build();

        user.openPrivateChannel().queue((c) -> {
            c.sendMessage(embed).queue((m) -> sent(user));
        });
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || !bot.isInGuild(user))
            return; // not in the echat server; or a bot.

        if (shouldSend(user))
            sendResponse(user);
    }

    private void sent(User user) {
        previouslySent.put(user.getIdLong(), Instant.now());
    }

    private boolean shouldSend(User user) {
        Instant previous = previouslySent.get(user.getIdLong());
        return previous == null || previous.isBefore(Instant.now().minusSeconds(10));
    }
}
