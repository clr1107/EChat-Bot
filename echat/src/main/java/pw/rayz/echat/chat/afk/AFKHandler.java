package pw.rayz.echat.chat.afk;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.PermissionException;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class AFKHandler {
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final Set<AFKInstance> instances = new HashSet<>();

    /**
     * Change a member's status to be AFK.
     *
     * @param member {@link Member} who should now be AFK.
     * @param reason Their reason for becoming AFK, null is not accepted, not even for a default reason.
     */
    public void enableAFK(@Nonnull Member member, @Nonnull String reason) {
        disableAFK(member);

        AFKInstance instance = new AFKInstance(member.getIdLong(), Instant.now(), member.getEffectiveName(), reason);
        instances.add(instance);

        try {
            member.modifyNickname("(AFK) " + member.getEffectiveName()).queue();
        } catch (PermissionException exception) {
            logger.warning("Could not change nickname (setting afk) for: " + member.getEffectiveName());
        }
    }

    /**
     * Remove a member's AFK status, if they have one.
     *
     * @param member {@link Member} who should no longer be AFK.
     */
    public void disableAFK(@Nullable Member member) {
        AFKInstance instance = getAFKInstance(member);

        if (member != null && instance != null) {
            instances.remove(instance);

            try {
                member.modifyNickname(instance.getPreviousNickname()).queue();
            } catch (PermissionException exception) {
                logger.warning("Could not change nickname (removing afk) for: " + member.getEffectiveName());
            }
        }
    }

    /**
     * Remove all member's AFK status.
     */
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

    /**
     * If a member is AFK, and the member is of the EChat Guild, return their
     * AFK instance.
     *
     * @param member {@link Member} to search for.
     * @return {@link AFKInstance} or null.
     */
    public AFKInstance getAFKInstance(@Nullable Member member) {
        Guild guild = EChat.eChat().getBot().getEChatGuild();

        if (guild == null || member == null)
            return null;

        return instances.stream()
                .filter(a -> a.getId() == member.getIdLong() && member.getGuild().getIdLong() == guild.getIdLong())
                .findFirst()
                .orElse(null);
    }

    public boolean isAFK(@Nullable Member member) {
        return getAFKInstance(member) != null;
    }
}
