package pw.rayz.echat.punishment.implementations;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.punishment.PunishmentType;

public class IllegalChannelChatInfraction extends AbstractPunishment {
    private final MessageChannel channel;
    private String message;

    public IllegalChannelChatInfraction(@NotNull MessageChannel channel) {
        super(PunishmentType.CHAT_INFRACTION);

        this.channel = channel;
        this.loadMessage(true);
    }

    private void loadMessage(boolean first) {
        Configuration config = EChat.eChat().getConfig();

        if (first)
            config.addLoadTask(() -> this.loadMessage(false));

        this.message = config.getString("punishments.illegal_channel", false);
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
    public void send(@NotNull Member member) {
        MessageEmbed embed = createEmbedBuilder()
                .setTitle("*Illegal Channel Chat*")
                .setDescription(message)
                .addField("Channel", channel.getName(), true)
                .addField("Type", "Selfie", true)
                .build();

        member.getUser().openPrivateChannel().queue((c) -> {
            c.sendMessage(embed).queue();
        });
    }
}