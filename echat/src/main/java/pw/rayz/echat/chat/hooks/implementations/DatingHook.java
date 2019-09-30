package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Message;
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

    @Override
    public boolean matches(Message message) {
        if (bot.getMessageFilter().isImmune(message.getMember())) // immune members can say "dating" & "server"
            return false;

        String raw = message.getContentRaw().toLowerCase();
        return raw.contains("dating") && raw.contains("server");
    }

    @Override
    public void messageCatch(Message message) {
        message.getChannel().sendMessage(msg).queue();
    }

}
