package pw.rayz.echat.utils;

import pw.rayz.echat.EChat;

import java.awt.*;
import java.time.Instant;

public class EmbedBuilderTemplate {
    private final EChat eChat = EChat.eChat();
    private final net.dv8tion.jda.api.EmbedBuilder state;
    private final long id = IdentityService.getService().nextId();
    private String iconURL;

    public EmbedBuilderTemplate() {
        this.state = new net.dv8tion.jda.api.EmbedBuilder();
        eChat.getConfig().addLoadTask(this::loadURL, true);
    }

    public enum EmbedType {
        BASIC, PUNISHMENT
    }

    private void loadURL() {
        iconURL = eChat.getConfig().getString("icon", false);
    }

    public EmbedBuilderTemplate apply(EmbedType type) {
        switch (type) {
            case BASIC:
                state.setThumbnail(iconURL);
                state.setColor(Color.BLACK);
                state.setAuthor("EChat Bot");
                state.setFooter("id: " + Long.toHexString(id));
                state.setTimestamp(Instant.now());
                break;
            case PUNISHMENT:
                state.setThumbnail(iconURL);
                state.setColor(Color.RED);
                state.setAuthor("EChat Infraction");
                state.setFooter("Contact a staff member if you believe this to be invalid, id: " + Long.toHexString(id));
                state.setTimestamp(Instant.now());
                break;
        }

        return this;
    }

    public net.dv8tion.jda.api.EmbedBuilder builder() {
        return state;
    }

    public EmbedBuilderTemplate addPunishmentChannel(String channel) {
        state.addField("Channel", channel, true);
        return this;
    }
}
