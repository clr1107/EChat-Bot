package pw.rayz.echat.urbandictionary.request;

public final class UrbanResponse {
    private String definition;
    private String word;
    private String author;
    private int thumbs_up;

    private UrbanResponse() {
    }

    private String truncate(String str) {
        if (str.length() <= 350)
            return str;
        else return str.substring(0, 347) + "...";
    }

    public String getDefinition() {
        definition = truncate(definition);
        return definition.replace("[", "").replace("]", "");
    }

    public String getWord() {
        word = truncate(word);
        return word;
    }

    public String getAuthor() {
        author = truncate(word);
        return author;
    }

    public int getThumbsUp() {
        return thumbs_up;
    }
}
