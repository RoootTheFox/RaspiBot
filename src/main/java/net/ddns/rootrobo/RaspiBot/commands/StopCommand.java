package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.Main;
import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.logging.Level;

@SuppressWarnings("unused")
public class StopCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        if (!(msg.getAuthor().getId().equals(Main.DEVELOPER_ID))) {
            return;
        }
        EmbedUtils.sendTextEmbed(Main.bot.getSelfUser().getName(), "Stopped the Bot!\n Goodbye!" , msg.getChannel());
        Main.LOGGER.log(Level.INFO, "The Bot was stopped by " + msg.getAuthor().getAsTag() + "!");
        //msg.getChannel().sendMessage("bot stopped.").complete();
        Main.shutdown();
    }

    @Override
    public String getName() { return "stop"; }

    @Override
    public String getDescription() { return null; }

    @Override
    public String[] getAliases() { return new String[0]; }

    @Override
    public Permission getPermission() { return null; }
}
