package pw.rayz.echat.chat.hooks;

import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;

public interface ChatHook {

    boolean matches(@Nonnull Message message);

    void executeHook(@Nonnull Message message);

}
