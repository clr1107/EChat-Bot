package pw.rayz.echat.chat.filter;

import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.punishment.Punishment;

import javax.annotation.Nonnull;

public interface MessageFilter {

    /**
     * Return a {@link Punishment} for a {@link Message}, if it matches
     * this filter.
     *
     * @param message {@link Message} to check.
     * @return {@link Punishment} or {@code null} if there is none.
     */
    Punishment checkMessage(@Nonnull Message message);

}
