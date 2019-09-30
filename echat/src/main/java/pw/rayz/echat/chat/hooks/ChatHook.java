package pw.rayz.echat.chat.hooks;

import net.dv8tion.jda.api.entities.Message;

public interface ChatHook {

    boolean matches(Message message);

    void messageCatch(Message message);

}
