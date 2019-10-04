package pw.rayz.echat.chat.filter;

import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.punishment.Punishment;

public interface MessageFilter {

    Punishment checkMessage(Message message);

}
