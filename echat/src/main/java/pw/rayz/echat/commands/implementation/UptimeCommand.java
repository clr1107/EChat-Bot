package pw.rayz.echat.commands.implementation;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.EChat;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.commands.CommandExecution;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UptimeCommand extends AbstractCommand {
    private final JDABot bot;
    private List<String> roles;

    public UptimeCommand(JDABot bot) {
        super("uptime", new String[]{
                "ut", "utime", "up"
        });

        this.bot = bot;
        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = bot.getEChat().getConfig();

        this.roles = (List<String>) config.getField("roles.staff", new ArrayList<>(), ArrayList.class, false);
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().stream().map(ISnowflake::getId).anyMatch(r -> roles.contains(r));
    }

    @Override
    public void execute(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        TextChannel channel = commandExecution.getCause().getChannel();

        if (member == null)
            return;

        commandExecution.getCause().getMessage().delete().queue();
        // if successful, remove their msg.

        long seconds = EChat.eChat().millisSinceStartup() / 1000;
        String msg = "Uptime: " + LocalTime.MIN.plusSeconds(seconds).toString();

        channel.sendMessage(msg).queue();
    }

    @Override
    public void invalidPermission(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        if (member == null)
            return;

        commandExecution.getCause().getMessage().delete().queue();

        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You don't have permission to go afk.").queue();
        });
    }

    @Override
    public void invalidSyntax(CommandExecution commandExecution) {
    }
}
