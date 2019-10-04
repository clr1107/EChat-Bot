package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.afk.AFKHandler;
import pw.rayz.echat.chat.hooks.ChatHook;

public class AFKHook implements ChatHook {
    private static final String RESPONSE = "I have removed your AFK status, %s";
    private final JDABot bot;

    public AFKHook(JDABot bot) {
        this.bot = bot;

//        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
    }

    @Override
    public boolean matches(Message message) {
        Member member = message.getMember();

        AFKHandler handler = bot.getMessageAuthority().getAFKHandler();
        return handler.isAFK(member);
    }

    @Override
    public void messageCatch(Message message) {
        Member member = message.getMember();
        TextChannel channel = message.getTextChannel();

        if (member == null)
            return;

        bot.getMessageAuthority().getAFKHandler().disableAFK(member);
        channel.sendMessage(String.format(RESPONSE, member.getEffectiveName())).queue();
    }

}
