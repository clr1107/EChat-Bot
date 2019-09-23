package pw.rayz.echat.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.utils.IdentityService;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PrivateMessageListener extends ListenerAdapter {
    private final EChat eChat = EChat.eChat();
    private final Map<Long, Instant> previouslySent = new HashMap<>();
    private String message;

    public PrivateMessageListener() {
        eChat.getConfig().addLoadTask(this::loadMessage, true);
    }

    private void loadMessage() {
        message = eChat.getConfig().getString("standard_messages.private_response", "Hi!", false);
    }

    private void sendResponse(User user) {
        final String iconURL = eChat.getConfig().getString("icon", false);
        long id = IdentityService.getService().nextId();

        MessageEmbed embed = new EmbedBuilder()
                .setThumbnail(iconURL)
                .setColor(Color.CYAN)
                .setAuthor("EChat Bot")
                .setFooter("id: " + Long.toHexString(id))
                .setTimestamp(Instant.now())
                .addField("Response", message, false)
                .build();

        user.openPrivateChannel().queue((c) -> {
            c.sendMessage(embed).queue((m) -> sent(user));
        });
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (!eChat.getBot().isInGuild(user))
            return; // not in the echat server.

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
