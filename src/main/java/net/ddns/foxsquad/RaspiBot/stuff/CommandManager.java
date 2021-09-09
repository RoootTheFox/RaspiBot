package net.ddns.foxsquad.RaspiBot.stuff;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.ddns.foxsquad.RaspiBot.log.LogFormatter;
import net.ddns.foxsquad.RaspiBot.Main;

import java.util.HashMap;

public class CommandManager {
    public static HashMap<String, Command> commands = new HashMap<>();
    public static HashMap<String, String> aliases = new HashMap<>();

    public static void registerCommand(Command cmd, boolean silent) {
        commands.put(cmd.getName(), cmd);

        for (String alias : cmd.getAliases()) {
            aliases.put(alias, cmd.getName());
        }

        if(!silent) {
            if(cmd.getPermission() == null) {
                Main.LOGGER.info(LogFormatter.ANSI_GREEN+"Successfully registered command: " +cmd.getName() + LogFormatter.ANSI_YELLOW + " without permission");
            } else {
                Main.LOGGER.info(LogFormatter.ANSI_GREEN+"Successfully registered command: " +cmd.getName() + " with permission "+LogFormatter.ANSI_CYAN+cmd.getPermission().name());
            }
        }
    }

    public static void register() {
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableClassInfo()
                             .scan()) {
            for (ClassInfo classInfo : scanResult.getClassesImplementing("net.ddns.rootrobo.RaspiBot.stuff.Command")) {
                @SuppressWarnings("unchecked")
                Class<? extends Command> cmdClass = (Class<? extends Command>) classInfo.loadClass();
                Command cmd = cmdClass.newInstance();
                registerCommand(cmd, false);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static Command getCommandByName(String name) {
        try {
            Command cmd = commands.get(name);
            if(cmd.getName() == null) {
                return null;
            } else {
                return cmd;
            }
        } catch (NullPointerException ee) {
            return null;
        }
    }

    public static Command getCommandByAlias(String alias) {
        try {
            Command cmd = commands.get(aliases.get(alias));
            if(cmd.getName() == null) {
                return null;
            } else {
                return cmd;
            }
        } catch (NullPointerException ee) {
            return null;
        }
    }

    public static int getCommandCount() {
        return commands.size();
    }
}
