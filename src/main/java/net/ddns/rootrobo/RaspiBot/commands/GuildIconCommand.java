package net.ddns.rootrobo.RaspiBot.commands;

import net.ddns.rootrobo.RaspiBot.stuff.Command;
import net.ddns.rootrobo.RaspiBot.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

@SuppressWarnings("unused")
public class GuildIconCommand implements Command {
    @Override
    public void run(Message msg, String[] args, Guild guild, TextChannel channel) {
        msg.getChannel().sendTyping().queue();

        if(guild.getIconUrl() == null) {
            msg.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("No icon")
                    .setDescription("This server doesn't have an icon!")
                    .setColor(EmbedUtils.ERROR_COLOR).build()).queue();
            return;
        }
        msg.getChannel().sendMessage(new EmbedBuilder().setImage(guild.getIconUrl()+"?size=2048").build()).queue();
    }

    @Override
    public String getName() { return "guild-icon"; }

    @Override
    public String getDescription() { return "Returns the server icon in the highest possible quality."; }

    @Override
    public String[] getAliases() { return new String[]{"guildicon", "server-icon", "servericon"}; }

    @Override
    public Permission getPermission() { return null; }
}
