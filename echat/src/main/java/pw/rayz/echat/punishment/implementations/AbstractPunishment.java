package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.Punishment;
import pw.rayz.echat.punishment.PunishmentType;
import pw.rayz.echat.utils.IdentityService;

import java.util.logging.Level;

public abstract class AbstractPunishment implements Punishment {
    protected final EChat eChat = EChat.eChat();
    private final PunishmentType type;
    protected final long id;
    protected final Member member;

    public AbstractPunishment(@NotNull PunishmentType type, long id, @NotNull Member member) {
        this.type = type;
        this.id = id;
        this.member = member;
    }

    public AbstractPunishment(@NotNull PunishmentType type, @NotNull Member member) {
        this(type, IdentityService.getService().nextId(), member);
    }

    @Nullable
    abstract protected EmbedBuilder prepareAuditEmbed();

    @Override
    public void sendAudit() {
        TextChannel channel = EChat.eChat().getBot().getLogChannel();
        String msg = "Sending punishment of type: \"%s\" to user displayed as \"%s\" (id: %s)";

        if (channel != null) {
            EmbedBuilder builder = prepareAuditEmbed();
            MessageEmbed embed = builder != null ? builder.build() : null;

            eChat.getLogger().log(Level.INFO, String.format(msg, type.name, member.getEffectiveName(), Long.toHexString(id)));

            if (embed != null)
                channel.sendMessage(embed).queue();
        }
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

    @NotNull
    public Member getMember() {
        return member;
    }
}
