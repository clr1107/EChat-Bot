package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
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
        String emoteName = bot.getEChat().getConfig().getString("hooks.blood_emoji", false);
        emote = bot.getJDA().getEmotesByName(emoteName, true).stream().findFirst().orElse(null);
    }

    /**
     * Return whether this message matches the hook. The conditions are:
     * - the member is not null
     * - the message starts with either "blood gang" or "treyway"
     *
     * @param message {@link Message} to check.
     * @return whether the message matches
     */
    @Override
    public boolean matches(@NotNull Message message) {
        Member member = message.getMember();

        if (member == null)
            return false;

        String raw = message.getContentRaw().toLowerCase();
        return raw.startsWith("blood gang") || raw.startsWith("treyway");
    }

    /**
     * If the message matches the hook, do the following:
     * - get the blood emote, and check it's not null
     * - if it's not null, send it as a response.
     *
     * @param message matching {@link Message}
     */
    @Override
    public void executeHook(@NotNull Message message) {
        if (emote != null)
            message.getChannel().sendMessage(emote.getAsMention()).queue();
    }

}
