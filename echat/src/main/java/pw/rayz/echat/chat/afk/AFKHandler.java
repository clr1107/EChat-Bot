package pw.rayz.echat.chat.afk;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.PermissionException;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class AFKHandler {
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Set<AFKInstance> instances = new HashSet<>();

    public void enableAFK(Member member, String reason) {
        disableAFK(member);

        AFKInstance instance = new AFKInstance(member.getIdLong(), Instant.now(), member.getEffectiveName(), reason);
        instances.add(instance);

        try {
            member.modifyNickname("(AFK) " + member.getEffectiveName()).queue();
        } catch (PermissionException exception) {
            logger.warning("Could not change nickname (setting afk) for: " + member.getEffectiveName());
        }
    }

    public void disableAFK(Member member) {
        AFKInstance instance = getAFKInstance(member);

        if (instance != null) {
            instances.remove(instance);

            try {
                member.modifyNickname(instance.getPreviousNickname()).queue();
            } catch (PermissionException exception) {
                logger.warning("Could not change nickname (removing afk) for: " + member.getEffectiveName());
            }
        }
    }

    public void disableAllAFK() {
        JDABot bot = EChat.eChat().getBot();
        Guild guild = bot.getEChatGuild();

        if (guild != null) {
            for (AFKInstance instance : instances) {
                long id = instance.getId();
                Member member = guild.getMemberById(id);

                if (member != null) disableAFK(member);
            }
        }
    }

    public AFKInstance getAFKInstance(Member member) {
        return instances.stream().filter(a -> a.getId() == member.getIdLong()).findFirst().orElse(null);
    }

    public boolean isAFK(Member member) {
        return getAFKInstance(member) != null;
    }
}
