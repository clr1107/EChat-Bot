package pw.rayz.echat.commands;

import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;

public interface Command {

    /**
     * The name of the command, the main way to use it.
     *
     * @return {@link String}.
     */
    @Nonnull
    String getName();

    /**
     * Aliases of the command, fixed amount.
     *
     * @return Nonnull {@link String[]}, may be empty.
     */
    @Nonnull
    String[] getAliases();

    /**
     * Whether a {@code member} has permission to use this command.
     *
     * @param member {@link Member}.
     * @return {@code boolean} for whether they have permission or not.
     */
    boolean hasPermission(Member member);

    /**
     * Whether the provided {@code label} is the name of the command, or one of its
     * aliases.
     * @param label Label to test.
     * @return whether it matches.
     */
    boolean matchesName(String label);

    /**
     * Execute this command.
     */
    void execute(CommandExecution commandExecution);

    /**
     * To be implemented.
     * @param commandExecution {@link CommandExecution}.
     */
    void invalidPermission(CommandExecution commandExecution);

    /**
     * To be implemented.
     * @param commandExecution {@link CommandExecution}.
     */
    void invalidSyntax(CommandExecution commandExecution);

}
