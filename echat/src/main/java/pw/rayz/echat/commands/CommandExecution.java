package pw.rayz.echat.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandExecution {
    private final String labelUsed;
    private final String[] args;
    private final GuildMessageReceivedEvent event;

    public CommandExecution(String labelUsed, String[] args, GuildMessageReceivedEvent event) {
        this.labelUsed = labelUsed;
        this.args = args;
        this.event = event;
    }

    public String getLabelUsed() {
        return labelUsed;
    }

    public String[] getArgs() {
        return args;
    }

    public GuildMessageReceivedEvent getCause() {
        return event;
    }

}
