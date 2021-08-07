package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

@SuppressWarnings("unused")
public class NetherPortalCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild) {
        String o = "<:obsidian:708484180681424916>";
        String p = "<a:nether_portal:708480437051195452>";
        String top_bottom = o+o+o+o;
        String middle = o+p+p+o;
        String portal = top_bottom+"\n"+middle+"\n"+middle+"\n"+middle+"\n"+top_bottom;
        msg.getChannel().sendMessage(portal).complete();
    }

    @Override
    public String getName() { return "portal"; }

    @Override
    public String getDescription() { return "Sends a Minecraft Nether Portal. idk why"; }

    @Override
    public String[] getAliases() { return new String[]{"netherportal"}; }

    @Override
    public Permission getPermission() { return null; }
}
