package pw.rayz.echat.punishment.implementations;

import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.PunishmentType;
import pw.rayz.echat.utils.EmbedBuilderTemplate;
import pw.rayz.echat.utils.IdentityService;

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

    protected EmbedBuilderTemplate createEmbedBuilder() {
        return new EmbedBuilderTemplate().apply(EmbedBuilderTemplate.EmbedType.PUNISHMENT);
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
