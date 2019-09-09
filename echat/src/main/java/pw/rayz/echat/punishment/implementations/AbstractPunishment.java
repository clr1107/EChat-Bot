package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.PunishmentType;
import pw.rayz.echat.utils.IdentityService;

import java.awt.*;
import java.time.Instant;

public abstract class AbstractPunishment implements Punishment {
    protected final EChat eChat = EChat.eChat();
    private final PunishmentType type;
    private final long id;

    public AbstractPunishment(@NotNull PunishmentType type, long id) {
        this.type = type;
        this.id = id;
    }

    public AbstractPunishment(PunishmentType type) {
        this.type = type;
        this.id = IdentityService.getService().nextId();
    }

    protected EmbedBuilder createEmbedBuilder() {
        final String iconURL = eChat.getConfig().getString("icon", false);

        return new EmbedBuilder()
                .setThumbnail(iconURL)
                .setColor(Color.RED)
                .setAuthor("EChat Infraction")
                .setFooter("Contact a staff member if you believe this to be invalid")
                .setTimestamp(Instant.now());
    }

    @Override
    public long getId() {
        return id;
    }

    @NotNull
    @Override
    public PunishmentType getType() {
        return type;
    }
}
