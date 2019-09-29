package pw.rayz.echat.chat.afk;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import pw.rayz.echat.EChat;

import java.time.Instant;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AFKHandler {
    private final Set<AFKInstance> instances = new HashSet<>();

    public void setAfk(TextChannel channel, Member member, String msg) {
        AFKInstance instance = new AFKInstance(member.getIdLong(), Instant.now(), member.getEffectiveName(), msg);

        channel.sendMessage(member.getEffectiveName() + " is now afk: " + msg).queue();

        try {
            member.modifyNickname("(AFK) " + member.getEffectiveName()).queue();
        } catch (PermissionException exception) {
            EChat.eChat().getLogger().warning("Could not change the nickname of " + member.getEffectiveName());
        }

        instances.add(instance);
    }

    public void removeAFK(TextChannel channel, Member member) {
        AFKInstance instance = getAFK(member);

        if (instance != null) {
            instances.remove(instance);

            try {
                member.modifyNickname(instance.getPreviousNickname()).queue();
            } catch (PermissionException exception) {
                EChat.eChat().getLogger().warning("Could not change the nickname of " + member.getEffectiveName());
            }

            channel.sendMessage("I have removed your afk status, " + member.getEffectiveName()).queue();
        }
    }

    public void taggedAFK(Message message) {
        List<Member> mentioned = message.getMentionedMembers();
        StringBuilder builder = new StringBuilder();

        for (Member member : mentioned) {
            AFKInstance instance = getAFK(member);

            if (instance != null) {
                long seconds = (System.currentTimeMillis() - instance.getInstant().toEpochMilli()) / 1000;
                String time = LocalTime.MIN.plusSeconds(seconds).toString();

                String msg = member.getEffectiveName() + " is afk: " + instance.getMsg() + " (time: " + time + ")";
                builder.append(msg).append(". ");
            }
        }

        String msg = builder.toString();
        if (!msg.isBlank())
            message.getChannel().sendMessage(msg).queue();
    }

    public AFKInstance getAFK(Member member) {
        return instances.stream().filter(a -> a.getId() == member.getIdLong()).findFirst().orElse(null);
    }
}
