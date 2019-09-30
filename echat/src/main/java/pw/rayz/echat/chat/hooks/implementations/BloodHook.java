package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.hooks.ChatHook;

public class BloodHook implements ChatHook {
    private final JDABot bot;
    private Emote emote;

    public BloodHook(JDABot bot) {
        this.bot = bot;

        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        String emoteName = bot.getEChat().getConfig().getString("standard_messages.blood_gang_hook", false);
        emote = bot.getJDA().getEmotesByName(emoteName, true).stream().findFirst().orElse(null);
    }

    @Override
    public boolean matches(Message message) {
        String raw = message.getContentRaw().toLowerCase();
        return raw.startsWith("blood gang");
    }

    @Override
    public void messageCatch(Message message) {
        message.getChannel().sendMessage(emote.getAsMention()).queue();
    }

}
