package pw.rayz.echat.utils;

import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.PunishmentType;

import java.awt.*;
import java.time.Instant;

public class EmbedBuilderTemplate {
    private final EChat eChat = EChat.eChat();
    private final net.dv8tion.jda.api.EmbedBuilder state;
    private long id;
    private String iconURL;

    public EmbedBuilderTemplate(long id) {
        this.id = id;
        this.state = new net.dv8tion.jda.api.EmbedBuilder();

        eChat.getConfig().addLoadTask(this::loadURL, true);
    }

    public EmbedBuilderTemplate() {
        this(IdentityService.getService().nextId());
    }

    public enum EmbedType {
        BASIC, PUNISHMENT, PUNISHMENT_AUDIT
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
                state.setFooter("Message id: " + Long.toHexString(id));
                state.setTimestamp(Instant.now());
                break;
            case PUNISHMENT:
                state.setThumbnail(iconURL);
                state.setColor(Color.RED);
                state.setAuthor("EChat Infraction");
                state.setFooter("Contact a staff member if you believe this to be invalid, id: " + Long.toHexString(id));
                state.setTimestamp(Instant.now());
                break;
            case PUNISHMENT_AUDIT:
                state.setThumbnail(iconURL);
                state.setColor(Color.MAGENTA);
                state.setAuthor("EChat Infraction Log");
                state.setFooter("Punishment id: " + Long.toHexString(id));
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

    public EmbedBuilderTemplate addPunishmentType(PunishmentType type) {
        state.setTitle("*" + type.name + "*");
        return this;
    }
}
