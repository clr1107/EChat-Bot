package pw.rayz.echat.commands.implementation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.time.DurationFormatUtils;
import pw.rayz.echat.Configuration;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.chat.MessageLogger;
import pw.rayz.echat.commands.CommandExecution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RetrieveCommand extends AbstractCommand {
    private final JDABot bot;
    private List<String> roles;

    public RetrieveCommand(JDABot bot) {
        super("retrieve", new String[]{"ret"});

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

    private String prepareMessage(Collection<MessageLogger.MessageInstance> instances) {
        if (instances.isEmpty())
            return "No deleted messages logged. Sorry.";

        StringBuilder builder = new StringBuilder();
        Guild guild = bot.getEChatGuild();
        final String fmt = "[%s] **%s** - %s";

        if (guild == null)
            return "An error occurred, sorry,";

        for (MessageLogger.MessageInstance instance : instances) {
            Member member = guild.getMemberById(instance.userId);
            long elapsed = System.currentTimeMillis() - instance.instant.toEpochMilli();
            String elapsedStr = DurationFormatUtils.formatDuration(elapsed, "HH:mm:ss") + " ago";

            if (member == null)
                continue;

            builder.append(String.format(fmt, elapsedStr, member.getEffectiveName(), instance.msg))
                    .append("\n");
        }

        return builder.toString().trim();
    }

    @Override
    public void execute(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        TextChannel channel = commandExecution.getCause().getChannel();
        String[] args = commandExecution.getArgs();
        int amount;

        if (member == null)
            return;

        if (args.length != 1)
            amount = 8;
        else {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                channel.sendMessage("Invalid amount.").queue();
                return;
            }
        }

        MessageLogger logger = bot.getMessageAuthority().getLogger();
        List<MessageLogger.MessageInstance> instances = logger.getDeletedMessages(amount);

        channel.sendMessage(prepareMessage(instances)).queue();
    }

    @Override
    public void invalidPermission(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();
        if (member == null)
            return;

        commandExecution.getCause().getMessage().delete().queue();

        member.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("You don't have permission to retrieve deleted messages.").queue();
        });
    }

    @Override
    public void invalidSyntax(CommandExecution commandExecution) {
    }
}
