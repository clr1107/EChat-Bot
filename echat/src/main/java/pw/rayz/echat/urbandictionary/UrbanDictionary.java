package pw.rayz.echat.urbandictionary;

import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.urbandictionary.request.UrbanResponse;
import pw.rayz.echat.utils.EmbedBuilderTemplate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Iterator;

public class UrbanDictionary {
    private final JDABot bot;
    private final Requester requester;
    private final Gson gson = new Gson();

    public UrbanDictionary(JDABot bot) {
        this.bot = bot;
        this.requester = new Requester();
    }

    private MessageEmbed buildEmbed(UrbanResponse response) {
        EmbedBuilder builder = new EmbedBuilderTemplate().apply(EmbedBuilderTemplate.EmbedType.URBAN_RESPONSE)
                .builder();

        if (response == null) {
            return builder
                    .addField("Not Found", "Sorry, this term could not be defined.", false)
                    .build();
        } else {
            return builder
                    .addField("Term", response.getWord(), true)
                    .addField("Author", response.getAuthor(), true)
                    .addField("Thumbs Up", Integer.toString(response.getThumbsUp()), true)
                    .addField("Definition", response.getDefinition(), false)
                    .build();
        }
    }

    private UrbanResponse deserialiseJsonToResponse(String json) {
        JsonElement outerElem = new JsonParser().parse(json);

        if (!outerElem.isJsonObject())
            return null;

        JsonObject outerObj = outerElem.getAsJsonObject();
        JsonElement listElem = outerObj.get("list");

        if (!listElem.isJsonArray())
            return null;

        JsonArray listArray = listElem.getAsJsonArray();
        Iterator<JsonElement> iterator = listArray.iterator();
        UrbanResponse response = null;

        while (iterator.hasNext() && response == null) {
            JsonElement elem = iterator.next();

            if (!elem.isJsonObject())
                continue;

            try {
                response = gson.fromJson(elem, UrbanResponse.class);
            } catch (JsonParseException exception) {
                response = null;
            }
        }

        return response;
    }

    private UrbanResponse parseResponseDefinition(@Nonnull Response response) {
        ResponseBody body = response.body();

        if (body != null) {
            try {
                String bodyStr = body.string();
                return deserialiseJsonToResponse(bodyStr);
            } catch (IOException exception) {
                return null;
            }
        }

        return null;
    }

    private void onRequesterFailure(@NotNull Call call, @NotNull IOException e, @Nonnull Message cause) {
        TextChannel channel = cause.getTextChannel();
        channel.sendMessage("Couldn't look that up, sorry.").queue();

        bot.getEChat().getLogger().warning(
                "Failed to perform Urban Dictionary lookup: " + e.getClass().getName()
        );
    }

    private void onRequesterSuccess(@NotNull Call call, @NotNull Response response, @NotNull Message cause) throws IOException {
        TextChannel channel = cause.getTextChannel();
        UrbanResponse urbanResponse = parseResponseDefinition(response);

        MessageEmbed embed = buildEmbed(urbanResponse);
        channel.sendMessage(embed).queue();
    }

    public void requestDefinition(@Nonnull String term, @Nonnull Message cause) {
        requester.defineTerm(term, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onRequesterFailure(call, e, cause);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                onRequesterSuccess(call, response, cause);
            }
        });
    }

    public JDABot getBot() {
        return bot;
    }
}
