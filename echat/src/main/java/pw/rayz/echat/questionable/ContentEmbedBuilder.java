package pw.rayz.echat.questionable;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ContentEmbedBuilder {
    private final QuestionableContent content;

    public ContentEmbedBuilder(QuestionableContent content) {
        this.content = content;
    }

    public EmbedBuilder build() {
        final String description = "Questionable Content Pinned by: " + content.getQuestioner().getNickname();

        return new EmbedBuilder()
                .setAuthor(content.getMessage().getMember().getNickname())
                .setTitle("Questionable Content")
                .setDescription(description)
                .setColor(Color.GREEN)
                .addField("Message", content.getMessage().getContentDisplay(), false);
    }
}
