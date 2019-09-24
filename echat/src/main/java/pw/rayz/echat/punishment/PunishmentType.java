package pw.rayz.echat.punishment;

public enum PunishmentType {

    ILLEGAL_CHANNEL_CHAT_INFRACTION("Illegal Channel"),
    ILLEGAL_WORD_CHAT_INFRACTION("Illegal Word"),
    ILLEGAL_CHARACTER_COUNT_CHAT_INFRACTION("Illegal character count");

    public final String name;

    PunishmentType(String name) {
        this.name = name;
    }
}
