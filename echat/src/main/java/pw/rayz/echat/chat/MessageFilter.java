package pw.rayz.echat.chat;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.hooks.ChatHook;
import pw.rayz.echat.chat.hooks.implementations.BloodHook;
import pw.rayz.echat.chat.hooks.implementations.DatingHook;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MessageFilter {
    private final JDABot bot;
    private final Map<Long, Instant> previousMessageInstants = new ConcurrentHashMap<>();
    private final Properties properties = new Properties();
    private final Set<ChatHook> chatHooks = new HashSet<>();
    private List<String> bannedWords;
    private List<String> immuneRoles;
    private List<String> selfieChannels;

    public MessageFilter(JDABot bot) {
        this.bot = bot;

        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
        loadChatHooks();
    }

    private void loadConfig() {
        Configuration config = bot.getEChat().getConfig();

        properties.put("check_caps", config.getBoolean("limits.check_caps", false, false));
        properties.put("spam_millis", config.getInt("limits.spam_millis", -1, false));
        properties.put("caps_percentage", config.getInt("limits.caps_percentage", 100, false));
        properties.put("character_limit", config.getInt("limits.characters", 800, false));

        bannedWords = (List<String>) config.getField("banned_words", new ArrayList<>(), ArrayList.class, false);
        immuneRoles = (List<String>) config.getField("roles.message_filter_bypass", new ArrayList<>(), ArrayList.class, false);
        selfieChannels = (List<String>) config.getField("channels.selfies", new ArrayList<>(), ArrayList.class, false);
    }

    private void loadChatHooks() {
        chatHooks.add(new BloodHook(bot));
        chatHooks.add(new DatingHook(bot));
    }

    public void registerSentMessage(Member member) {
        previousMessageInstants.put(member.getIdLong(), Instant.now());
    }

    public boolean isImmune(Member member) {
        List<Role> roles = immuneRoles.stream().map(bot::getGuildRole).collect(Collectors.toList());
        return roles.stream().anyMatch(r -> member.getRoles().contains(r));
    }

    public boolean isIllegalChannel(Message message) {
        if (isImmune(message.getMember()))
            return false;

        return selfieChannels.contains(message.getChannel().getId()) && message.getAttachments().isEmpty();
    }

    public boolean checkForSpam(Member member) {
        if (isImmune(member)) return false;

        Instant previous = previousMessageInstants.get(member.getUser().getIdLong());
        int millis = (int) properties.get("spam_millis");

        return previous != null && millis != -1 && Instant.now().isBefore(previous.plusMillis(millis));
    }

    public boolean checkForCaps(Message message) {
        boolean check = (boolean) properties.get("check_caps");

        if (message == null || !check)
            return false;

        if (isImmune(message.getMember()))
            return false;

        String raw = message.getContentRaw();
        int capsPercentage = (int) properties.get("caps_percentage");

        if (raw.length() >= 7) {
            long capsCount = raw.chars().mapToObj(c -> (char) c).filter(Character::isUpperCase).count();
            return ((double) capsCount / (double) raw.length()) >= capsPercentage;
        } else return false;
    }

    public boolean checkForLength(Message message) {
        if (message == null)
            return false;

        if (isImmune(message.getMember()))
            return false;

        String raw = message.getContentRaw();
        int length = (int) properties.get("character_limit");

        return raw.length() >= length;
    }

    public String checkForBannedWords(Message message) {
        // for now, do not use percentage similarity, just not working... yet.
        if (message == null)
            return null;

        if (isImmune(message.getMember()))
            return null;

        String raw = message.getContentRaw();

        for (String word : raw.split(" ")) {
            Optional<String> contains = bannedWords.stream()
                    .filter(banned -> banned.trim().equalsIgnoreCase(word))
                    .findFirst();

            if (contains.isPresent())
                return contains.get();
        }

        return null;
    }

    public void testForHooks(Message message) {
        Member member = message.getMember();

        if (member == null)
            return;

        String user = message.getMember().getEffectiveName();
        chatHooks.forEach(h -> {
            if (h.matches(message)) {
                bot.getEChat().getLogger().info("Chat Hook tripped: " + h.getClass().getName() + " by: " + user);
                h.messageCatch(message);
            }
        });
    }

    public Set<ChatHook> getChatHooks() {
        return chatHooks;
    }

    public Properties getProperties() {
        return properties;
    }
}
