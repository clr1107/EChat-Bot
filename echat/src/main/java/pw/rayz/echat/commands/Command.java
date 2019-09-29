package pw.rayz.echat.commands;

import net.dv8tion.jda.api.entities.Member;

public interface Command {

    String getName();

    String[] getAliases();

    boolean hasPermission(Member member);

    boolean matchesName(String label);

    void execute(CommandExecution commandExecution);

    void invalidPermission(CommandExecution commandExecution);

    void invalidSyntax(CommandExecution commandExecution);

}
