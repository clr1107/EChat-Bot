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

    // todo
    public String assembleArgs(int from, int to) {
        if (from < 0 || to > args.length)
            return null;

        StringBuilder builder = new StringBuilder();
        for (int i = from; i < to || (to < 0 && i < args.length); i++)
            builder.append(args[i]);

        return builder.toString();
    }

    public GuildMessageReceivedEvent getCause() {
        return event;
    }

}
