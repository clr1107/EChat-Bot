package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.PunishmentType;
import pw.rayz.echat.utils.EmbedBuilderTemplate;

public class IllegalWordInfraction extends AbstractPunishment {
    private final Configuration config;
    private final MessageChannel channel;
    private final String word;
    private String message;

    public IllegalWordInfraction(@NotNull MessageChannel channel, @NotNull String word, @NotNull Member member) {
        super(PunishmentType.ILLEGAL_WORD_CHAT_INFRACTION, member);

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
    public void send() {
        MessageEmbed embed = new EmbedBuilderTemplate(id).apply(EmbedBuilderTemplate.EmbedType.PUNISHMENT)
                .addPunishmentChannel(channel.getName())
                .addPunishmentType(getType())
                .builder()
                .setDescription(message)
                .addField("Word", word, true)
                .build();

        member.getUser().openPrivateChannel().queue((c) -> {
            c.sendMessage(embed).queue();
        });
    }

    @Override
    protected EmbedBuilder prepareAuditEmbed() {
        return new EmbedBuilderTemplate(id)
                .apply(EmbedBuilderTemplate.EmbedType.PUNISHMENT_AUDIT)
                .addPunishmentChannel(channel.getName())
                .addPunishmentType(getType())
                .builder()
                .setDescription("A user attempted to say a banned word.")
                .addField("Word", word, true)
                .addField("User punished", "<@" + member.getUser().getId() + ">", true);
    }
}
