package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.PunishmentType;

public class IllegalWordInfraction extends AbstractPunishment {
    private final Configuration config;
    private final MessageChannel channel;
    private final String word;
    private String message;

    public IllegalWordInfraction(@NotNull MessageChannel channel, String word) {
        super(PunishmentType.CHAT_INFRACTION);

        this.config = EChat.eChat().getConfig();
        this.channel = channel;
        this.word = word;

        config.addLoadTask(this::loadMessage, true);
    }

    private void loadMessage() {
        message = config.getString("punishments.illegal_word", false);
    }

    @NotNull
    public MessageChannel getChannel() {
        return channel;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void send(@NotNull Member member) {
        MessageEmbed embed = createEmbedBuilder()
                .addPunishmentChannel(channel.getName())
                .builder()
                .setTitle("*Banned Word*")
                .setDescription(message)
                .addField("Word", word, true)
                .build();

        member.getUser().openPrivateChannel().queue((c) -> {
            c.sendMessage(embed).queue();
        });
    }
}
