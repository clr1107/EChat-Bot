package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.hooks.ChatHook;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MockingHook implements ChatHook {
    private static final String MATCH = "/mocking/ ";
    private final JDABot bot;
    private boolean enabled = false;

    public MockingHook(JDABot bot) {
        this.bot = bot;

        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        enabled = bot.getEChat().getConfig().getBoolean("hooks.mocking", false, false);
    }

    private String mockStr(String original) {
        Random random = ThreadLocalRandom.current();
        StringBuilder newStr = new StringBuilder();

        for (char c : original.toLowerCase().toCharArray()) {
            if (random.nextBoolean())
                newStr.append(Character.toUpperCase(c));
            else newStr.append(c);
        }

        return newStr.toString();
    }

    /**
     * Return whether this message matches the hook. The conditions are:
     * - check the member is not null
     * - this feature is enabled
     * - the length of their message is longer than just "/mocking/"
     * - the message starts with "/mocking/"
     *
     * @param message {@link Message} to check.
     * @return whether the message matches
     */
    @Override
    public boolean matches(@NotNull Message message) {
        Member member = message.getMember();

        if (member == null)
            return false;

        if (!enabled)
            return false;

        String raw = message.getContentRaw().toLowerCase();
        return raw.length() > MATCH.length() && raw.startsWith(MATCH);
    }

    /**
     * If the message matches the hook, do the following:
     * - delete the original message
     * - turn the message into a mocking string with SWitCHing capiTALs and add the
     * "kaj" emoji at the end, if it's not null.
     * - send this message to the channel.
     *
     * @param message matching {@link Message}
     */
    @Override
    public void executeHook(@NotNull Message message) {
        Member member = message.getMember();
        TextChannel channel = message.getTextChannel();

        if (member != null) {
            message.delete().queue();

            Emote emote = bot.getJDA().getEmotesByName("kaj", true).stream().findFirst().orElse(null);
            String rawMsg = message.getContentRaw().substring(MATCH.length());
            String msg = ">>> " + String.format(
                    "**%s** - %s %s",
                    member.getEffectiveName(),
                    mockStr(rawMsg),
                    emote != null ? emote.getAsMention() : ""
            );

            channel.sendMessage(msg).queue();
        }
    }

}
