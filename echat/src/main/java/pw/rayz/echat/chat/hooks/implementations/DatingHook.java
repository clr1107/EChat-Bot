package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.hooks.ChatHook;

public class DatingHook implements ChatHook {
    private final JDABot bot;
    private String msg;

    public DatingHook(JDABot bot) {
        this.bot = bot;

        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        msg = bot.getEChat().getConfig().getString("standard_messages.dating_server_hook", false);
    }

    /**
     * Return whether this message matches the hook. The conditions are:
     * - check the member is not null
     * - the member is not immune
     * - the message contains both "dating" and "server"
     *
     * @param message {@link Message} to check.
     * @return whether the message matches
     */
    @Override
    public boolean matches(@NotNull Message message) {
        Member member = message.getMember();

        if (member == null)
            return false;

        if (bot.getMessageAuthority().isImmune(message.getMember())) // immune members can say "dating" & "server"
            return false;

        String raw = message.getContentRaw().toLowerCase();
        return raw.contains("dating") && raw.contains("server");
    }

    /**
     * If the message matches the hook, do the following:
     * - send the standard response from the configuration.
     *
     * @param message matching {@link Message}
     */
    @Override
    public void executeHook(@NotNull Message message) {
        message.getChannel().sendMessage(msg).queue();
    }

}
