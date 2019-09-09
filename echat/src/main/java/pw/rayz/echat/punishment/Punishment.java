package pw.rayz.echat.punishment;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public interface Punishment {

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
     * Send this specific punishment to a {@link Member}.
     * @param member {@link Member} to send this punishment to.
     */
    void send(Member member);

}
