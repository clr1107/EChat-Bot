package pw.rayz.echat.urbandictionary.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import pw.rayz.echat.JDABot;
import pw.rayz.echat.commands.CommandExecution;
import pw.rayz.echat.commands.implementation.AbstractCommand;
import pw.rayz.echat.urbandictionary.UrbanDictionary;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DefineCommand extends AbstractCommand {
    private final JDABot bot;
    private final Map<Long, Instant> pastUse = new HashMap<>();
    private long delay;
    private boolean allowUrban;

    public DefineCommand(JDABot bot) {
        super("urbandictionary", new String[]{
                "ud", "udictionary", "urban", "define"
        });

        this.bot = bot;
        bot.getEChat().getConfig().addLoadTask(this::loadConfig, true);
    }

    private void loadConfig() {
        delay = bot.getEChat().getConfig().getLong("limits.urban", 20000L, false);
        allowUrban = bot.getEChat().getConfig().getBoolean("limits.allow_urban", false, false);
    }

    private boolean checkTime(Member member) {
        Instant previous = pastUse.get(member.getIdLong());
        boolean immune = bot.getMessageAuthority().isImmune(member);

        if (!immune && previous != null && previous.plusMillis(delay).isAfter(Instant.now())) {
            member.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage(
                        "Please wait at least " + (delay / 1000) + " seconds before doing that again!"
                ).queue();
            });

            return false;
        }

        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return allowUrban && member != null && !member.getUser().isBot();
    }

    @Override
    public void execute(CommandExecution commandExecution) {
        String[] args = commandExecution.getArgs();
        Member member = commandExecution.getCause().getMember();
        Message message = commandExecution.getCause().getMessage();

        if (member == null || member.getUser().isBot())
            return;

        if (!checkTime(member)) {
            message.delete().queue();
            return;
        }

        if (args.length < 1) {
            invalidSyntax(commandExecution);
            return;
        }

        String term = commandExecution.assembleArgs(0, -1);

        if (term != null) {
            UrbanDictionary dictionary = bot.getDictionary();
            dictionary.requestDefinition(term, message);

            pastUse.put(member.getIdLong(), Instant.now());
        }
    }

    @Override
    public void invalidPermission(CommandExecution commandExecution) {
        Message message = commandExecution.getCause().getMessage();
        Member member = message.getMember();

        message.delete().queue();

        if (member != null) {
            member.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("You cannot do this right now.").queue();
            });
        }
    }

    @Override
    public void invalidSyntax(CommandExecution commandExecution) {
        Member member = commandExecution.getCause().getMember();

        if (member != null) {
            member.getUser().openPrivateChannel().queue((channel) -> {
                channel.sendMessage("Usage: `::urbandictionary <term/phrase to define>`").queue();
            });
        }
    }
}
