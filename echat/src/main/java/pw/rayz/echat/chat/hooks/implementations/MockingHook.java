package pw.rayz.echat.chat.hooks.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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

    @Override
    public boolean matches(Message message) {
        if (!enabled)
            return false;

        String raw = message.getContentRaw().toLowerCase();
        return raw.length() > MATCH.length() && raw.startsWith(MATCH);
    }

    @Override
    public void messageCatch(Message message) {
        Member member = message.getMember();
        TextChannel channel = message.getTextChannel();

        if (member != null) {
            message.delete().queue();

            String rawMsg = message.getContentRaw().substring(MATCH.length());
            String msg = String.format("**%s** - %s", member.getEffectiveName(), mockStr(rawMsg));

            channel.sendMessage(msg).queue();
        }
    }

}
