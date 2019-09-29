package pw.rayz.echat.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandHandler extends ListenerAdapter {
    private final JDABot bot;
    private final Set<Command> commandSet = new HashSet<>();

    public CommandHandler(JDABot bot) {
        this.bot = bot;
    }

    public void registerCommand(Command command) {
        this.commandSet.add(command);
    }

    private void executeCommand(Command command, CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();

        if (member == null)
            return;

        String msg = "Attempting to execute command: \"" + command.getName() + "\" for user displayed as: \"" + member.getEffectiveName() + "\"";
        bot.getEChat().getLogger().info(msg);

        if (!command.hasPermission(commandExecution.getCause().getMember()))
            command.invalidPermission(commandExecution);
        else {
            command.execute(commandExecution);
        }
    }

    private Command matchCommand(String label) {
        return commandSet.stream()
                .filter(c -> c.matchesName(label))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        Message message = event.getMessage();

        if (member != null && bot.isInGuild(member.getUser())) {
            String raw = message.getContentRaw();

            if (!raw.startsWith(EChat.COMMAND_PREFIX)) return;
            else raw = raw.substring(EChat.COMMAND_PREFIX.length());

            String[] parts = raw.length() > 0 ? raw.split(" ") : null;
            if (parts != null) {
                String labelUsed = parts[0];
                Command command = matchCommand(labelUsed);

                String[] args = Arrays.copyOfRange(parts, 1, parts.length);
                if (command != null) {
                    CommandExecution commandExecution = new CommandExecution(labelUsed, args, event);
                    executeCommand(command, commandExecution);
                }
            }
        }
    }
}
