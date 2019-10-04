package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.afk.AFKHandler;
import pw.rayz.echat.chat.afk.AFKInstance;
import pw.rayz.echat.chat.hooks.ChatHook;

import java.util.List;

public class AFKMentionHook implements ChatHook {
    private static final String RESPONSE = "%s is afk: %s (time: %s)";
    private final JDABot bot;

    public AFKMentionHook(JDABot bot) {
        this.bot = bot;

//        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
    }

    @Override
    public boolean matches(Message message) {
        Member member = message.getMember();

        AFKHandler handler = bot.getMessageAuthority().getAFKHandler();
        Guild guild = bot.getEChatGuild();

        if (member == null || guild == null)
            return false;

        return message.getMentionedMembers(guild).stream().anyMatch(handler::isAFK);
    }

    @Override
    public void messageCatch(Message message) {
        Member member = message.getMember();
        AFKHandler handler = bot.getMessageAuthority().getAFKHandler();
        Guild guild = bot.getEChatGuild();

        if (member == null || guild == null)
            return;

        List<Member> mentionedMembers = message.getMentionedMembers(guild);
        StringBuilder builder = new StringBuilder();

        for (Member mentioned : mentionedMembers) {
            AFKInstance instance = handler.getAFKInstance(mentioned);

            String msg = String.format(
                    RESPONSE, mentioned.getEffectiveName(), instance.getReason(), instance.timeSinceAFK()
            );

            builder.append(msg).append(". ");
        }

        String msg = builder.toString().trim();
        if (!msg.isBlank())
            message.getTextChannel().sendMessage(msg).queue();
    }

}
