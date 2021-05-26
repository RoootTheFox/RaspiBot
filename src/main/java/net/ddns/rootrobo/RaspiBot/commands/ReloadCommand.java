package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.stuff.CommandManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;

public class ReloadCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        msg.getChannel().sendTyping().complete();
        if (!(msg.getAuthor().getId().equals(Main.DEVELOPER_ID))) {
            return;
        }
        CommandManager.commands = new HashMap<>();
        CommandManager.aliases = new HashMap<>();

        CommandManager.register();
        //noinspection CallToThreadRun
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }).run();
        msg.getChannel().sendMessage("Done reloading.\n"+"Command Count: "+CommandManager.getCommandCount()).complete();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the commands";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
