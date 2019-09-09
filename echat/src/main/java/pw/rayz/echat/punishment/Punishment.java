package pw.rayz.echat.punishment;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public interface Punishment {

    long getId();

    @NotNull
    PunishmentType getType();

    String getMessage();

    void send(Member member);

}
