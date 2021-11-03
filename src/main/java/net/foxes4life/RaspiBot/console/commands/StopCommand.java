package net.foxes4life.RaspiBot.console.commands;

import net.foxes4life.RaspiBot.Main;
import net.foxes4life.RaspiBot.stuff.ConsoleCommand;

@SuppressWarnings("unused")
public class StopCommand implements ConsoleCommand {
    @Override
    public void run(String command, String[] args) {
        Main.shutdown();
    }

    @Override
    public String getName() { return "stop"; }

    @Override
    public String getDescription() { return "Stops the bot."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public boolean getRequiresReady() { return false; }
}
