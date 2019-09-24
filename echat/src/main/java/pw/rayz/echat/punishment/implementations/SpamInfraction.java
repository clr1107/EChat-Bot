package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.punishment.PunishmentType;
import pw.rayz.echat.utils.EmbedBuilderTemplate;

public class SpamInfraction extends AbstractPunishment {
    private final MessageChannel channel;
    private final String message;

    public SpamInfraction(@NotNull MessageChannel channel, @NotNull String message, @NotNull Member member) {
        super(PunishmentType.SPAM_CHAT_INFRACTION, member);

        this.channel = channel;
        this.message = message;
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
                .addField("Message", message, true)
                .setDescription("Please wait at least a quater of a second before sending another message.")
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
                .addField("Message", message, true)
                .setDescription("A User sent two messages within a quater of a second of eachother (SPAM)")
                .addField("User punished", "<@" + member.getUser().getId() + ">", true);
    }
}
