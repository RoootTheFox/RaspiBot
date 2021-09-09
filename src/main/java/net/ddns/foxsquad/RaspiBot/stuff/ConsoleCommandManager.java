package net.ddns.foxsquad.RaspiBot.stuff;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.ddns.foxsquad.RaspiBot.log.LogFormatter;
import net.ddns.foxsquad.RaspiBot.Main;

import java.util.Arrays;
import java.util.HashMap;

public class ConsoleCommandManager {
    public static HashMap<String, ConsoleCommand> commands = new HashMap<>();
    public static HashMap<String, String> aliases = new HashMap<>();

    public static void registerCommand(ConsoleCommand cmd, boolean silent) {
        commands.put(cmd.getName(), cmd);
        for (String alias : cmd.getAliases()) {
            aliases.put(alias, cmd.getName());
        }

        if(!silent) {
            Main.LOGGER.info(LogFormatter.ANSI_GREEN+"Successfully registered console command: " +cmd.getName() + " (aliases: "+ Arrays.toString(cmd.getAliases())+")" );
        }
    }

    public static void register() {
        String pkg = "net.ddns.foxsquad.RaspiBot.console.commands";
        try (ScanResult scanResult =
                     new ClassGraph()
                             .acceptPackages(pkg)
                             .enableClassInfo()
                             .scan()) {
            for (ClassInfo classInfo : scanResult.getClassesImplementing("net.ddns.foxsquad.RaspiBot.stuff.ConsoleCommand")) {
                @SuppressWarnings("unchecked") // classes implementing ConsoleCommand always implement ConsoleCommand
                Class<? extends ConsoleCommand> cmdClass = (Class<? extends ConsoleCommand>) classInfo.loadClass();
                ConsoleCommand cmd = cmdClass.newInstance();
                registerCommand(cmd, false);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static ConsoleCommand getCommandByName(String name) {
        try {
            ConsoleCommand cmd = commands.get(name);
            if(cmd.getName() == null) {
                return null;
            } else {
                return cmd;
            }
        } catch (NullPointerException ee) {
            return null;
        }
    }

    public static ConsoleCommand getCommandByAlias(String alias) {
        try {
            ConsoleCommand cmd = commands.get(aliases.get(alias));
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
