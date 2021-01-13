package net.ddns.rootrobo.RaspiBot.console.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.ConsoleCommand;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

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
