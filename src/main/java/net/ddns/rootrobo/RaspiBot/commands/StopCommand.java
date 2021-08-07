package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class StopCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if (!(msg.getAuthor().getId().equals(Main.DEVELOPER_ID))) {
            return;
        }

        msg.getChannel().sendMessage("bot stopped.").complete();
        Main.shutdown();
    }

    @Override
    public String getName() { return "stop"; }

    @Override
    public String getDescription() { return "Stops the bot."; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }

    @Override
    public boolean getIsPublic() {
        return false;
    }
}
