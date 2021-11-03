package net.foxes4life.RaspiBot.console;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.stuff.ConsoleCommand;
import net.foxes4life.RaspiBot.stuff.ConsoleCommandManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Console {
    private static Thread thread;
    public static void start() {
        thread = new Thread(() -> {
            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input = null;
                try {
                    input = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert input != null;
                String[] consoleargs = input.split(" ");
                String command = consoleargs[0];
                consoleargs = Arrays.copyOfRange(consoleargs, 1, consoleargs.length);

                ConsoleCommand cmd = ConsoleCommandManager.getCommandByName(command);
                ConsoleCommand cmd2 = ConsoleCommandManager.getCommandByAlias(command);

                try {
                    if(cmd != null && (!cmd.getRequiresReady() || (cmd.getRequiresReady() && Main.READY))) {
                        cmd.run(command, consoleargs);
                    } else {
                        if(cmd2 != null && (!cmd2.getRequiresReady() || (cmd2.getRequiresReady() && Main.READY))) {
                            cmd2.run(command, consoleargs);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("consoleThread");
        thread.start();
    }

    public static void stop() {
        thread.interrupt();
    }
}
