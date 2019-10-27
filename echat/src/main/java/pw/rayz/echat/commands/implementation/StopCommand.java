package pw.rayz.echat.commands.implementation;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.commands.CommandExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StopCommand extends AbstractCommand {
    private final JDABot bot;
    private List<String> roles;

    public StopCommand(JDABot bot) {
        super("stop", new String[]{});

        this.bot = bot;
        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = bot.getEChat().getConfig();

        this.roles = (List<String>) config.getField("roles.staff", new ArrayList<>(), ArrayList.class, false);
    }

    private void stop(User user) {
        Logger logger = Logger.getLogger("EChat-Bot");
        logger.warning(user.getAsTag() + " has issued the stop command!!!");

        EChat.eChat().stop();
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().stream().map(ISnowflake::getId).anyMatch(r -> roles.contains(r));
    }

    @Override
    public void execute(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        Message message = commandExecution.getCause().getMessage();

        if (member == null)
            return;

        member.getUser().openPrivateChannel().queue((c) -> {
            c.sendMessage("Stopping the bot.").queue();
        });

        message.delete().queue();
        stop(member.getUser());
    }

    @Override
    public void invalidPermission(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        if (member == null)
            return;

        commandExecution.getCause().getMessage().delete().queue();

        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You don't have permission to stop the server.").queue();
        });
    }

    @Override
    public void invalidSyntax(CommandExecution commandExecution) {
    }
}
