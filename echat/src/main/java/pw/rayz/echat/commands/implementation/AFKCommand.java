package pw.rayz.echat.commands.implementation;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.commands.CommandExecution;

import java.util.ArrayList;
import java.util.List;

public class AFKCommand extends AbstractCommand {
    private final JDABot bot;
    private List<String> roles;

    public AFKCommand(JDABot bot) {
        super("afk", new String[]{"away"});

        this.bot = bot;
        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        Configuration config = bot.getEChat().getConfig();

        this.roles = (List<String>) config.getField("roles.staff", new ArrayList<>(), ArrayList.class, false);
    }

    private void sendAFK(TextChannel channel, Member member, String msg) {
        channel.sendMessage(member.getEffectiveName() + " is now afk: " + msg).queue();
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

        String msg = "AFK";
        if (commandExecution.getArgs().length != 0) {
            StringBuilder builder = new StringBuilder();

            for (String part : commandExecution.getArgs())
                builder.append(part).append(" ");

            msg = builder.toString();
        }

        bot.getMessageAuthority().getAFKHandler().enableAFK(member, msg);
        channel.sendMessage(member.getEffectiveName() + " is now afk: " + msg).queue();
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
