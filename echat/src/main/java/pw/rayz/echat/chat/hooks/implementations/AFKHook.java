package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
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

    /**
     * Return whether this message matches the hook. The conditions are:
     * - the user is afk.
     *
     * @param message {@link Message} to check.
     * @return whether the message matches
     */
    @Override
    public boolean matches(@NotNull Message message) {
        Member member = message.getMember();

        AFKHandler handler = bot.getMessageAuthority().getAFKHandler();
        return handler.isAFK(member);
    }

    /**
     * If the message matches the hook, do the following:
     * - check the member is not null
     * - disable their afk
     * - send a message in the appropriate channel announcing they are no longer afk.
     *
     * @param message matching {@link Message}
     */
    @Override
    public void executeHook(@NotNull Message message) {
        Member member = message.getMember();
        TextChannel channel = message.getTextChannel();

        if (member == null)
            return;

        bot.getMessageAuthority().getAFKHandler().disableAFK(member);
        channel.sendMessage(String.format(RESPONSE, member.getEffectiveName())).queue();
    }

}
