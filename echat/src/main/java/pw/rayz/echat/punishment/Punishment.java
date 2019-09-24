package pw.rayz.echat.punishment;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Auditable;

public interface Punishment extends Auditable {

    /**
     * Unique ID, typically generated using {@link pw.rayz.echat.utils.IdentityService}.
     *
     * @return {@code long} unique id.
     */
    long getId();

    /**
     * The type of punishment.
     * @return Enum type of punishment {@link PunishmentType}.
     */
    @NotNull
    PunishmentType getType();

    /**
     * The message that will be sent to the user in a private channel.
     * @return {@link String} message to be sent to the user.
     */
    String getMessage();

    /**
     * Return the {@link Member} this punishment is for. May not be a member of the EChat guild!!
     * @return {@link Member}.
     */
    @NotNull
    Member getMember();

    /**
     * Send this specific punishment to the {@link Member}.
     */
    void send();

}
