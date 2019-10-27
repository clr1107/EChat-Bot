package pw.rayz.echat.urbandictionary;

import okhttp3.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.logging.Logger;

public class Requester {
    private static final String ENDPOINT = "https://api.urbandictionary.com/v0/";
    private final OkHttpClient client;

    public Requester() {
        this.client = new OkHttpClient();
    }

    public boolean defineTerm(String term, Callback callback) {
        Logger.getLogger("EChat-Bot").info("Requesting UrbanDictionary api, term: \"" + term + "\"");
        HttpUrl url = assembleUrl(term);

        if (url != null) {
            Call call = requestEndpoint(url);
            call.enqueue(callback);

            return true;
        }

        return false;
    }

    private Call requestEndpoint(HttpUrl url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request);
    }

    @Nullable
    private HttpUrl assembleUrl(@Nonnull String term) {
        HttpUrl url = HttpUrl.parse(ENDPOINT);

        if (url != null) {
            HttpUrl.Builder builder = url.newBuilder();
            builder.addPathSegment("define");

            builder.addQueryParameter("term", term);
            return builder.build();
        }

        return null;
    }
}
