package pw.rayz.echat.commands.implementation;

import pw.rayz.echat.commands.Command;

public abstract class AbstractCommand implements Command {
    private final String name;
    private final String[] aliases;

    public AbstractCommand(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases != null ? aliases : new String[0];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public boolean matchesName(String label) {
        if (label == null)
            return false;

        if (name.equalsIgnoreCase(label))
            return true;
        else {
            for (String alias : aliases) {
                if (alias.equalsIgnoreCase(label))
                    return true;
            }
        }

        return false;
    }
}
