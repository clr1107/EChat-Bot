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

public class IllegalCharacterCountInfraction extends AbstractPunishment {
    private final Configuration config;
    private final MessageChannel channel;
    private String message;

    public IllegalCharacterCountInfraction(@NotNull MessageChannel channel, @NotNull Member member) {
        super(PunishmentType.ILLEGAL_CHARACTER_COUNT_CHAT_INFRACTION, member);

        this.config = EChat.eChat().getConfig();
        this.channel = channel;

        config.addLoadTask(this::loadMessage, true);
    }

    private void loadMessage() {
        message = config.getString("punishments.illegal_character_count", false);
    }

    @NotNull
    public MessageChannel getChannel() {
        return channel;
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
                .setDescription("A user's message was too long")
                .addField("User punished", "<@" + member.getUser().getId() + ">", true);
    }
}
